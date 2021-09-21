package sg.searchhouse.agentconnect.view.adapter.chat

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemConversationBinding
import sg.searchhouse.agentconnect.databinding.ListItemMessageBinding
import sg.searchhouse.agentconnect.databinding.ListItemSearchChatTitleBinding
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.model.db.SsmMessageEntity
import sg.searchhouse.agentconnect.util.DateTimeUtil
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

class SearchChatResultAdapter(
    private val context: Context,
    private var onItemClickListener: ((String) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var searchResults = listOf<Any>()

    companion object {
        const val VIEW_TYPE_CONVERSATION = 0
        const val VIEW_TYPE_MESSAGE = 1
        const val VIEW_TYPE_TITLE = 2
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        return when (searchResults[position]) {
            is SsmConversationEntity -> VIEW_TYPE_CONVERSATION
            is SsmMessageEntity -> VIEW_TYPE_MESSAGE
            is String -> VIEW_TYPE_TITLE
            else -> throw ClassCastException("Wrong view type in search chat result adapter")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CONVERSATION -> {
                ConversationItemViewHolder(
                    ListItemConversationBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            VIEW_TYPE_MESSAGE -> {
                MessageItemViewHolder(
                    ListItemMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_TITLE -> {
                TitleItemViewHolder(
                    ListItemSearchChatTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid view type!")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConversationItemViewHolder -> {
                val entity = searchResults[position] as SsmConversationEntity
                val conversation = entity.fromEntityToSsmConversation()

                holder.binding.conversation = conversation

                when (conversation.type) {
                    ChatEnum.ConversationType.BLAST.value, ChatEnum.ConversationType.ONE_TO_ONE.value,
                    ChatEnum.ConversationType.AGENT_ENQUIRY.value, ChatEnum.ConversationType.SRX_ANNOUNCEMENTS.value -> {
                        holder.binding.contactName = conversation.otherUserName
                        if (!TextUtils.isEmpty(conversation.otherUserName)) {
                            holder.binding.layoutProfile.layoutProfileIcon.populateByInitialLetter(
                                conversation.otherUserPhoto,
                                conversation.otherUserName
                            )
                        }
                    }
                    ChatEnum.ConversationType.GROUP_CHAT.value -> {
                        holder.binding.contactName = conversation.title
                        if (!TextUtils.isEmpty(conversation.title)) {
                            holder.binding.layoutProfile.layoutProfileIcon.populateByInitialLetter(
                                conversation.otherUserPhoto,
                                conversation.title
                            )
                        }
                    }
                    else -> println("Not type..")
                }

                holder.itemView.setOnClickListener {
                    onItemClickListener.invoke(conversation.conversationId)
                }
            }
            is MessageItemViewHolder -> {
                val message = searchResults[position] as SsmMessageEntity
                //bind contact name
                if (!TextUtils.isEmpty(message.otherUserName)) {
                    holder.binding.contactName = message.otherUserName
                } else if (!TextUtils.isEmpty(message.sourceUserName)) {
                    holder.binding.contactName = message.sourceUserName
                }
                holder.binding.message = message.message
                holder.binding.date = DateTimeUtil.getFormattedDateTime(context, message.dateSent)

                holder.itemView.setOnClickListener {
                    onItemClickListener.invoke(message.conversationId)
                }
            }
            is TitleItemViewHolder -> {
                holder.binding.title = searchResults[position] as String
            }
        }
    }

    override fun getItemCount(): Int {
        return searchResults.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ConversationItemViewHolder(val binding: ListItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root)

    class MessageItemViewHolder(val binding: ListItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    class TitleItemViewHolder(val binding: ListItemSearchChatTitleBinding) :
        RecyclerView.ViewHolder(binding.root)
}