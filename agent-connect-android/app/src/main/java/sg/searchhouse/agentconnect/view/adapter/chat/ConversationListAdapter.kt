package sg.searchhouse.agentconnect.view.adapter.chat

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemConversationBinding
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.chat.ChatConversationClickEvent
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import java.lang.ClassCastException

class ConversationListAdapter(
    val context: Context,
    private val conversations: List<Any>,
    private val onCheckboxClickListener: ((SsmConversationPO) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showCheckBox: Boolean = false

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_ITEM = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            conversations[position] is SsmConversationPO -> VIEW_TYPE_ITEM
            conversations[position] is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException("Wrong view type in conversation list adapter")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            ConversationItemViewHolder(
                ListItemConversationBinding.inflate(LayoutInflater.from(parent.context))
            )
        } else {
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loading_indicator,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return conversations.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConversationItemViewHolder -> {
                val ssmConversation = conversations[position] as SsmConversationPO

                holder.binding.conversation = ssmConversation
                //NAME
                when (ssmConversation.type) {
                    ChatEnum.ConversationType.GROUP_CHAT.value -> {
                        holder.binding.contactName = ssmConversation.title
                        if (!TextUtils.isEmpty(ssmConversation.title)) {
                            holder.binding.layoutProfile.layoutProfileIcon.populateByInitialLetter(
                                ssmConversation.otherUserPhoto,
                                ssmConversation.title
                            )
                        }
                    }
                    ChatEnum.ConversationType.BLAST_NEW_TYPE.value -> {
                        holder.binding.contactName = ssmConversation.otherUserName
                        if (!TextUtils.isEmpty(ssmConversation.otherUserName)) {
                            holder.binding.layoutProfile.layoutProfileIcon.populateByInitialLetter(
                                imageUrl = ssmConversation.otherUserPhoto,
                                name = ssmConversation.otherUserName,
                                showNormalImage = true
                            )
                        }
                    }
                    else -> {
                        holder.binding.contactName = ssmConversation.otherUserName
                        if (!TextUtils.isEmpty(ssmConversation.otherUserName)) {
                            holder.binding.layoutProfile.layoutProfileIcon.populateByInitialLetter(
                                ssmConversation.otherUserPhoto,
                                ssmConversation.otherUserName
                            )
                        }
                    }
                }

                holder.binding.showCheckBox = this.showCheckBox
                holder.binding.showEnquiryIndicator =
                    ssmConversation.type == ChatEnum.ConversationType.AGENT_ENQUIRY.value

                //Check box
                holder.binding.cbChatItem.isChecked = ssmConversation.isSelected
                holder.binding.cbChatItem.setOnClickListener {
                    ssmConversation.isSelected = holder.binding.cbChatItem.isChecked
                    onCheckboxClickListener.invoke(ssmConversation)
                }

                //item click
                holder.itemView.setOnClickListener {
                    if (this.showCheckBox) {
                        holder.binding.cbChatItem.isChecked = !ssmConversation.isSelected
                        ssmConversation.isSelected = holder.binding.cbChatItem.isChecked
                        onCheckboxClickListener.invoke(ssmConversation)
                    } else {
                        //implemented in chat fragment
                        RxBus.publish(ChatConversationClickEvent(ssmConversation))
                    }
                }
            }
            is LoadingViewHolder -> {
                println("Show loading indicator ")
            }
        }
    }

    fun toggleCheckBoxState(showCheckBox: Boolean) {
        this.showCheckBox = showCheckBox
    }

    class ConversationItemViewHolder(val binding: ListItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root)

}