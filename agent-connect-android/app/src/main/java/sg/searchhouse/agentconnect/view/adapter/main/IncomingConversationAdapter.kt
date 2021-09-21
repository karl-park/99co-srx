package sg.searchhouse.agentconnect.view.adapter.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemConversationIncomingBinding
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import java.text.ParseException

class IncomingConversationAdapter(val conversations: ArrayList<SsmConversationPO>) :
    PagerAdapter() {

    var onItemClickListener: ((SsmConversationPO) -> Unit)? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil
            .inflate<ListItemConversationIncomingBinding>(
                LayoutInflater.from(container.context),
                R.layout.list_item_conversation_incoming, container, false
            )

        val conversation = conversations[position]
        binding.conversation = conversation

        setSentDateText(container.context, conversation.dateSent, binding.tvDate)
        //show title for group chat
        if (conversation.type == ChatEnum.ConversationType.GROUP_CHAT.value) {
            binding.tvSenderName.text = conversation.title
            binding.layoutProfileIcon.populateByInitialLetter(
                conversation.photoUrl,
                conversation.title
            )
        } else {
            binding.tvSenderName.text = conversation.otherUserName
            binding.layoutProfileIcon.populateByInitialLetter(
                conversation.photoUrl,
                conversation.otherUserName
            )
        }
        binding.rlIncomingConversation.setOnClickListener {
            onItemClickListener?.invoke(conversation)
        }
        container.addView(binding.root)
        return binding.root
    }

    private fun setSentDateText(context: Context, dateSentString: String, tv_date: TextView) {
        try {
            val dateSent = DateTimeUtil.getFormattedDateTime(context, dateSentString)
            tv_date.text = dateSent
        } catch (e: IllegalArgumentException) {
            ErrorUtil.handleError(context, R.string.exception_illegal_argument, e)
        } catch (e: ParseException) {
            ErrorUtil.handleError(context, R.string.exception_parse, e)
        }
    }

    override fun getPageWidth(position: Int): Float {
        return if (conversations.size > 1) {
            ITEM_WIDTH_DEFAULT
        } else {
            ITEM_WIDTH_ONE
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is View) {
            container.removeView(`object`)
        }
    }

    override fun getCount(): Int {
        return conversations.size
    }
    
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    companion object {
        const val ITEM_WIDTH_ONE = 1.0F
        const val ITEM_WIDTH_DEFAULT = 0.8F
    }
}