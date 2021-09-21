package sg.searchhouse.agentconnect.view.fragment.main.chat

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_chat.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentChatBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.event.agent.NotifyRetrieveConfigEvent
import sg.searchhouse.agentconnect.event.chat.ChatConversationClickEvent
import sg.searchhouse.agentconnect.event.chat.ChatEditEvent
import sg.searchhouse.agentconnect.event.chat.ChatRefreshEvent
import sg.searchhouse.agentconnect.event.chat.ChatSelectAllEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.LifecycleUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.view.adapter.chat.ChatPagerAdapter
import sg.searchhouse.agentconnect.view.adapter.chat.SearchChatResultAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.chat.ChatViewModel

class ChatFragment : ViewModelFragment<ChatViewModel, FragmentChatBinding>() {

    private lateinit var mContext: Context
    private lateinit var adapter: SearchChatResultAdapter
    private var chatPagerAdapter: ChatPagerAdapter? = null

    companion object {
        fun newInstance() = ChatFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeRxBus()
        setupPagerAndAdapter()
        checkSearchConvosAccessibility()
        handleViewPagerOnPageChanged()
        observeResultLiveData()
        handleViewListeners()
    }

    private fun setupPagerAndAdapter() {
        //create adapter object
        chatPagerAdapter = ChatPagerAdapter(mContext, childFragmentManager)
        vp_chat_list.adapter = chatPagerAdapter
        vp_chat_list.offscreenPageLimit = 4
        tl_chat_list.onCheckChangedTabCheckBox = onCheckChangedTabCheckBox
        tl_chat_list.setupWithViewPager(vp_chat_list)

        adapter = SearchChatResultAdapter(mContext, onItemClickListener = { conversationId ->
            onSearchConversationItemClick(conversationId)
        })
        list_search_result.layoutManager = LinearLayoutManager(this.activity)
        list_search_result.adapter = adapter
    }

    private fun checkSearchConvosAccessibility() {
        viewModel.isSearchConversationEnabled.value =
            AuthUtil.isAccessibilityModule(
                module = AdvisorModule.CHAT,
                function = InAccessibleFunction.CHAT_SEARCH
            )
    }

    private fun observeRxBus() {
        listenRxBus(NotifyRetrieveConfigEvent::class.java) {
            checkSearchConvosAccessibility()
        }

        listenRxBus(ChatRefreshEvent::class.java) {
            when (it) {
                ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER -> {
                    viewModel.searchResults.value?.let { results ->
                        if (results.isNotEmpty()) {
                            viewModel.searchConversationsByKeywords()
                        }
                    }
                }
                else -> {
                    //Do Nothing
                }
            }
        }

        listenRxBus(ChatConversationClickEvent::class.java) { event ->
            if (event.ssmConversationPO.type == ChatEnum.ConversationType.AGENT_ENQUIRY.value) {
                showCustomerEnquiryDialog(event.ssmConversationPO)
            } else {
                if (NumberUtil.isNaturalNumber(event.ssmConversationPO.conversationId)) {
                    showChatMessagingScreen(event.ssmConversationPO.conversationId)
                }
            }
        }
    }

    private fun showChatMessagingScreen(conversationId: String) {
        activity?.run { ChatMessagingActivity.launch(this, conversationId = conversationId) }
    }

    private fun showCustomerEnquiryDialog(ssmConversation: SsmConversationPO) {
        activity?.run {
            if (LifecycleUtil.isLaunchFragmentSafe(lifecycle)) {
                ChatCustomerEnquiryDialogFragment.launch(supportFragmentManager, ssmConversation)
            }
        }
    }

    private fun observeResultLiveData() {
        //edit mode
        viewModel.isEditMode.observe(this, Observer { editMode ->
            editMode?.let {
                RxBus.publish(ChatEditEvent(it))
                tl_chat_list.setEditMode(it)
            }
        })

        viewModel.apiCallStatus.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    refreshConversationList()
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })

        viewModel.searchResults.observe(this, Observer { result ->
            if (result.isNotEmpty()) {
                adapter.searchResults = result
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun handleViewListeners() {
        ib_compose_message.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AdvisorModule.CHAT,
                function = InAccessibleFunction.CHAT_CREATE,
                onSuccessAccessibility = {
                    closeEditMode()
                    showSrxAgentsDialog()
                })
        }
        tv_blacklist.setOnClickListener {
            showBlackListConfirmationDialog()
        }
        tv_mark_as_read.setOnClickListener {
            showMarkAsReadConfirmationDialog()
        }
        tv_delete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        btn_search_box.setOnClickListener {
            AuthUtil.showNonSubscriberPrompt(allowDismiss = true, module = AdvisorModule.CHAT)
        }
    }

    private fun showSrxAgentsDialog() {
        SRXAgentsDialogFragment.launch(
            childFragmentManager,
            SRXAgentsDialogFragment.SRXAgentsSource.SOURCE_CHAT_MAIN
        )
    }


    private fun handleViewPagerOnPageChanged() {
        vp_chat_list?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        RxBus.publish(ChatRefreshEvent.ALL)
                    }
                    1 -> {
                        RxBus.publish(ChatRefreshEvent.PUBLIC)
                    }
                    2 -> {
                        RxBus.publish(ChatRefreshEvent.SRX)
                    }
                    3 -> {
                        RxBus.publish(ChatRefreshEvent.AGENT)
                    }
                }
                closeEditMode()
            }
        })
    }

    private fun refreshConversationList() {
        RxBus.publish(ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER)
        closeEditMode()
        setSelectedConversations(ArrayList(), false)
    }

    //access this fun from child
    fun setSelectedConversations(
        selectedConversations: ArrayList<SsmConversationPO>,
        checkAll: Boolean
    ) {
        //update selected conversations
        viewModel.selectedConversations.postValue(selectedConversations)
        //true false check all
        if (selectedConversations.isEmpty()) {
            tl_chat_list.changeTabCheckBoxState(false)
        } else {
            tl_chat_list.changeTabCheckBoxState(checkAll)
        }
    }

    private val onCheckChangedTabCheckBox: (Boolean) -> Unit = { checkAll ->
        when (tl_chat_list.selectedTabPosition) {
            0 -> {
                RxBus.publish(
                    ChatSelectAllEvent(
                        checkAll,
                        ChatSelectAllEvent.ChatConversationType.ALL
                    )
                )
            }
            1 -> {
                RxBus.publish(
                    ChatSelectAllEvent(
                        checkAll,
                        ChatSelectAllEvent.ChatConversationType.PUBLIC
                    )
                )
            }
            2 -> {
                RxBus.publish(
                    ChatSelectAllEvent(
                        checkAll,
                        ChatSelectAllEvent.ChatConversationType.SRX
                    )
                )
            }
            3 -> {
                RxBus.publish(
                    ChatSelectAllEvent(
                        checkAll,
                        ChatSelectAllEvent.ChatConversationType.AGENT
                    )
                )
            }
            else -> {
                //do nothing..
            }
        }
    }

    private fun closeEditMode() {
        if (viewModel.isEditMode.value == true) {
            viewModel.isEditMode.value = false
        }
    }

    private fun showDeleteConfirmationDialog() {
        dialogUtil.showActionDialog(R.string.msg_delete_conversations) {
            viewModel.onDeleteConversations()
        }
    }

    private fun showBlackListConfirmationDialog() {
        viewModel.selectedConversations.value?.let {
            if (it.isNotEmpty()) {
                val notOneToOneConversations =
                    it.filter { conversation -> conversation.type != ChatEnum.ConversationType.ONE_TO_ONE.value }
                if (notOneToOneConversations.isNullOrEmpty()) {
                    dialogUtil.showActionDialog(R.string.msg_blacklist_agents) {
                        viewModel.onBlacklistAgents()
                    }
                } else {
                    ViewUtil.showMessage(R.string.msg_cannot_black_list)
                }
            }
        }
    }

    private fun showMarkAsReadConfirmationDialog() {
        dialogUtil.showActionDialog(R.string.msg_mark_as_read) {
            viewModel.onMarkAsReadConversations()
        }
    }

    private fun onSearchConversationItemClick(conversationId: String) {
        viewModel.getConversationById(conversationId)
        viewModel.conversation.removeObservers(this)
        viewModel.conversation.observe(this, Observer {
            val ssmConversation = it ?: return@Observer
            if (ssmConversation.type == ChatEnum.ConversationType.AGENT_ENQUIRY.value) {
                showCustomerEnquiryDialog(ssmConversation)
            } else {
                if (NumberUtil.isNaturalNumber(ssmConversation.conversationId)) {
                    showChatMessagingScreen(ssmConversation.conversationId)
                }
            }
            viewModel.conversation.postValue(null)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_chat
    }

    override fun getViewModelClass(): Class<ChatViewModel> {
        return ChatViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}
