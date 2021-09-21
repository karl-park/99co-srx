package sg.searchhouse.agentconnect.view.fragment.main.chat

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_chat_all_conversations.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentChatAllConversationsBinding
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.event.chat.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.chat.ConversationListAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.chat.AllConversationListViewModel

//TODO: in future -> if have time, pleas refine this codes
class AllConversationListFragment :
    ViewModelFragment<AllConversationListViewModel, FragmentChatAllConversationsBinding>() {

    private lateinit var adapter: ConversationListAdapter

    companion object {
        fun newInstance() = AllConversationListFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpAdapter()
        observeRxBus()
        observeLiveData()
        handleViewsListener()
    }

    private fun setUpAdapter() {
        activity?.run {
            adapter =
                ConversationListAdapter(this, viewModel.conversations) { onClickConversation(it) }
            list_all_conversations.layoutManager = LinearLayoutManager(this)
            list_all_conversations.adapter = adapter
        }
        viewModel.initialLoadConversations()
    }

    private fun observeRxBus() {
        listenRxBus(LoginAsAgentEvent::class.java) { viewModel.conversations.clear() }

        listenRxBus(ChatNewNotificationEvent::class.java) { viewModel.reloadConversations() }

        listenRxBus(ChatEditEvent::class.java) { refreshEditCheckBox(it.editMode) }

        listenRxBus(ChatRefreshEvent::class.java) { event ->
            when (event) {
                ChatRefreshEvent.ALL, ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER -> {
                    viewModel.refreshConversations()
                }
                else -> {
                    //Do nothing
                }
            }
        }
        listenRxBus(ChatSelectAllEvent::class.java) {
            when (it.type) {
                ChatSelectAllEvent.ChatConversationType.ALL -> {
                    checkOrUncheckConversationList(it.checkAll)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.storedConversations.removeObservers(this)
        viewModel.storedConversations.observe(viewLifecycleOwner, Observer { items ->

            srl_conversation_list.isRefreshing = false
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
            viewModel.status.postValue(ApiStatus.StatusKey.SUCCESS)
            updateSelectedConversations()
            adapter.notifyDataSetChanged()
        })
    }

    private fun handleViewsListener() {
        srl_conversation_list.setOnRefreshListener { viewModel.reloadConversations() }

        ViewUtil.listenVerticalScrollEnd(list_all_conversations, reachBottom = {
            if (viewModel.canLoadNext() && viewModel.conversations.last() !is Loading) {
                viewModel.conversations.add(Loading())
                adapter.notifyItemChanged(viewModel.conversations.size - 1)
                viewModel.loadMoreConversations()
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
        return R.layout.fragment_chat_all_conversations
    }

    override fun getViewModelClass(): Class<AllConversationListViewModel> {
        return AllConversationListViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}