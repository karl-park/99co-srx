package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.card_dashboard_conversations_empty.view.*
import kotlinx.android.synthetic.main.card_dashboard_conversations_occupied.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_conversations.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardConversationsBinding
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.event.app.SwipeMainPageEvent
import sg.searchhouse.agentconnect.event.chat.ChatNewNotificationEvent
import sg.searchhouse.agentconnect.event.chat.ChatRefreshEvent
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.view.adapter.main.MainBottomNavigationTab
import sg.searchhouse.agentconnect.view.adapter.main.MainPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.fragment.main.chat.ChatCustomerEnquiryDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardConversationsViewModel

class DashboardConversationsFragment :
    ViewModelFragment<DashboardConversationsViewModel, FragmentDashboardConversationsBinding>() {

    companion object {
        fun newInstance() = DashboardConversationsFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        performRequest()
        observeRxBuses()
        observeLiveData()
        handleListeners()
    }

    private fun performRequest() {
        viewModel.findUnreadAllSsmConversationsFirst()
    }

    private fun observeRxBuses() {
        listenRxBus(ChatNewNotificationEvent::class.java) {
            viewModel.findUnreadAllSsmConversations()
        }
        listenRxBus(ChatRefreshEvent::class.java) {
            when (it) {
                ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER -> {
                    viewModel.findUnreadAllSsmConversations()
                }
                else -> {
                    //Do Nothing
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                card_conversations.populate(it.convos, it.unreadMessageCount)
            }
        })
    }

    private fun handleListeners() {
        card_conversations.adapter.onItemClickListener = onItemClickListener
        card_conversations.btn_view_new_messages.setOnClickListener { showChatTab() }
        card_conversations.ll_view_all_conversations.setOnClickListener { showChatTab() }
        card_conversations_empty.btn_empty_view_all_message.setOnClickListener {
            showChatTab()
        }
        card_conversations_empty.layout_empty_view_all_conversations.setOnClickListener { showChatTab() }
    }

    private fun showChatTab() {
        publishRxBus(SwipeMainPageEvent(MainBottomNavigationTab.CHAT.position))
    }

    private val onItemClickListener: (SsmConversationPO) -> Unit = { ssmConversation ->
        if (ssmConversation.type == ChatEnum.ConversationType.AGENT_ENQUIRY.value) {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                ChatCustomerEnquiryDialogFragment.launch(supportFragmentManager, ssmConversation)
            }
        } else {
            if (NumberUtil.isNaturalNumber(ssmConversation.conversationId)) {
                activity?.run {
                    ChatMessagingActivity.launch(
                        this,
                        conversationId = ssmConversation.conversationId
                    )
                }
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_conversations
    }

    override fun getViewModelClass(): Class<DashboardConversationsViewModel> {
        return DashboardConversationsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}