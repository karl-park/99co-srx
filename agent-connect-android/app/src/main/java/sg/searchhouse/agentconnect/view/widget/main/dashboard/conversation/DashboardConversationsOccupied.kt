package sg.searchhouse.agentconnect.view.widget.main.dashboard.conversation

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardDashboardConversationsOccupiedBinding
import sg.searchhouse.agentconnect.databinding.LayoutConversationUnreadCountBinding
import sg.searchhouse.agentconnect.dsl.getBinding
import sg.searchhouse.agentconnect.dsl.inflate
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.adapter.main.IncomingConversationAdapter

class DashboardConversationsOccupied constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val convos: ArrayList<SsmConversationPO> = arrayListOf()
    val adapter = IncomingConversationAdapter(convos)

    private val binding : CardDashboardConversationsOccupiedBinding = context.inflate(R.layout.card_dashboard_conversations_occupied, this, true)
    private val layoutConversationUnreadCountBinding : LayoutConversationUnreadCountBinding = binding.layoutConversationUnreadCount.getBinding()

    init {
        binding.pagerNewMessages.adapter = adapter
    }

    fun populate(conversations: List<SsmConversationPO>, unreadMessageCount: Int) {
        val unreadConversations = conversations.filter { it.unreadCount > 0 }
        val unreadMessageCountText = NumberUtil.formatThousand(unreadMessageCount)
        // Set count label text and badge count
        binding.tvNewMessages.text = context.resources.getQuantityString(
            R.plurals.label_incoming_message_count,
            unreadMessageCount,
            unreadMessageCountText
        )
        layoutConversationUnreadCountBinding.tvBadge.text = unreadMessageCountText
        // Populate view pagers
        convos.clear()
        convos.addAll(unreadConversations)
        adapter.notifyDataSetChanged()
    }
}