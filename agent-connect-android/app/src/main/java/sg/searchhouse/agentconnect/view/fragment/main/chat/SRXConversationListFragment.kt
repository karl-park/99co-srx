package sg.searchhouse.agentconnect.view.fragment.main.chat

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_chat_srx_conversations.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentChatSrxConversationsBinding
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.event.chat.*
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.chat.ConversationListAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.chat.SRXConversationListViewModel

class SRXConversationListFragment :
    ViewModelFragment<SRXConversationListViewModel, FragmentChatSrxConversationsBinding>() {

    private lateinit var adapter: ConversationListAdapter

    companion object {
        fun newInstance() = SRXConversationListFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        observeRxBus()
        observeResultLiveData()
        handleViewsListener()
    }

    private fun observeRxBus() {
        listenRxBus(LoginAsAgentEvent::class.java) { viewModel.conversations.clear() }

        listenRxBus(ChatNewNotificationEvent::class.java) {
            if (!TextUtils.isEmpty(it.conversationId) && TextUtils.isEmpty(it.enquiryId)) {
                viewModel.reloadSRXConversations()
            }
        }

        listenRxBus(ChatEditEvent::class.java) { refreshEditCheckBox(it.editMode) }

        listenRxBus(ChatRefreshEvent::class.java) { event ->
            when (event) {
                ChatRefreshEvent.SRX, ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER -> {
                    viewModel.refreshSRXConversations()
                }
                else -> {
                    //Do nothing
                }
            }
        }

        listenRxBus(ChatSelectAllEvent::class.java) {
            when (it.type) {
                ChatSelectAllEvent.ChatConversationType.SRX -> {
                    checkOrUncheckConversationList(it.checkAll)
                }
                else -> {
                    //Do nothing
                }
            }
        }

    }

    private fun setUpAdapter() {
        activity?.run {
            adapter =
                ConversationListAdapter(this, viewModel.conversations) { onClickConversation(it) }
            list_srx_conversations.layoutManager = LinearLayoutManager(this)
            list_srx_conversations.adapter = adapter
        }
//      viewModel.initialLoadSRXConversations()
    }

    private fun observeResultLiveData() {
        viewModel.storedConversations.removeObservers(this)
        viewModel.storedConversations.observe(viewLifecycleOwner, Observer { items ->

            srl_srx_conversations.isRefreshing = false
            viewModel.conversations.removeAll(viewModel.conversations.filterIsInstance<Loading>())

            if (items.isNotEmpty()) {
                when (viewModel.startIndex) {
                    0 -> {
                        viewModel.conversations.clear()
                        viewModel.conversations.addAll(items)
                    }
                    else -> {
                        items.forEach { item ->
                            val isContain =
                                viewModel.conversations.filterIsInstance<SsmConversationPO>()
                                    .find { it.conversationId == item.conversationId }
                            isContain?.let {
                                viewModel.conversations.set(
                                    viewModel.conversations.indexOf(it),
                                    item
                                )
                            } ?: viewModel.conversations.add(item)
                        }
                    }
                }
            }
            updateSelectedConversations()
            adapter.notifyDataSetChanged()
        })
    }

    private fun handleViewsListener() {
        srl_srx_conversations.setOnRefreshListener {
            viewModel.reloadSRXConversations()
        }

        ViewUtil.listenVerticalScrollEnd(list_srx_conversations, reachBottom = {
            if (viewModel.canLoadNext()) {
                if (viewModel.conversations.last() !is Loading) {
                    viewModel.conversations.add(Loading())
                    adapter.notifyItemChanged(viewModel.conversations.size - 1)
                    viewModel.loadMoreSRXConversations()
                }
            }
        })
    }

    private fun onClickConversation(conversation: SsmConversationPO) {
        val contain =
            viewModel.selectedConversations.any { ssm -> ssm.hashCode() == conversation.hashCode() }
        if (contain) {
            viewModel.selectedConversations.remove(conversation)
        } else {
            viewModel.selectedConversations.add(conversation)
        }
        updateSelectedConversations()
    }

    private fun refreshEditCheckBox(showCheckBox: Boolean) {
        viewModel.selectedConversations.clear()
        viewModel.conversations.forEach { conversation ->
            if (conversation is SsmConversationPO) {
                conversation.isSelected = false
            }
        }
        updateSelectedConversations()
        adapter.toggleCheckBoxState(showCheckBox)
        adapter.notifyDataSetChanged()
    }

    private fun checkOrUncheckConversationList(checkAll: Boolean) {
        //clear all selected conversations
        viewModel.selectedConversations.clear()
        if (checkAll) {
            viewModel.selectedConversations.addAll(viewModel.conversations.filterIsInstance<SsmConversationPO>())
        }
        viewModel.conversations.forEach { conversation ->
            if (conversation is SsmConversationPO) {
                conversation.isSelected = checkAll
            }
        }
        updateSelectedConversations()
        adapter.notifyDataSetChanged()
    }

    private fun updateSelectedConversations() {
        (parentFragment as ChatFragment).setSelectedConversations(
            viewModel.selectedConversations,
            viewModel.selectedConversations.size == viewModel.conversations.size
        )
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_chat_srx_conversations
    }

    override fun getViewModelClass(): Class<SRXConversationListViewModel> {
        return SRXConversationListViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}