package sg.searchhouse.agentconnect.view.activity.chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityChatMessagingInfoBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.agent.cv.AgentCvActivity
import sg.searchhouse.agentconnect.view.adapter.chat.ChatMessagingInfoMembersAdapter
import sg.searchhouse.agentconnect.view.fragment.main.chat.SRXAgentsDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.chat.ChatMessagingInfoViewModel

class ChatMessagingInfoActivity :
    ViewModelActivity<ChatMessagingInfoViewModel, ActivityChatMessagingInfoBinding>() {

    private lateinit var adapter: ChatMessagingInfoMembersAdapter

    companion object {
        private const val EXTRA_KEY_CONVERSATION_CHAT_INFO = "EXTRA_CONVERSATION_CHAT_INFO"

        fun launch(activity: Activity, ssmConversation: SsmConversationPO) {
            val intent = Intent(activity, ChatMessagingInfoActivity::class.java)
            intent.putExtra(EXTRA_KEY_CONVERSATION_CHAT_INFO, ssmConversation)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolBarName()
        setupListAndAdapter()
        setupParamsFromExtra()
        observeLiveData()
        handleListeners()
    }

    private fun setupToolBarName() {
        supportActionBar?.title = ""
    }

    private fun setupListAndAdapter() {
        adapter = ChatMessagingInfoMembersAdapter(viewModel.members,
            onCallMember = { phoneNumber -> onCallMember(phoneNumber) },
            onMessageMember = { user -> onMessageMember(user) },
            onRemoveMember = { users -> showRemoveMemberConfirmationDialog(users) },
            onViewAgentCv = { user -> showAgentCv(user) }
        )
        val linearLayoutManager = LinearLayoutManager(this)
        binding.listMembers.layoutManager = linearLayoutManager
        binding.listMembers.adapter = adapter
    }

    private fun setupParamsFromExtra() {
        if (intent.hasExtra(EXTRA_KEY_CONVERSATION_CHAT_INFO)) {
            val data = intent.getSerializableExtra(EXTRA_KEY_CONVERSATION_CHAT_INFO) ?: return
            val ssmConversation = data as SsmConversationPO
            viewModel.showGroupName.value =
                ssmConversation.type == ChatEnum.ConversationType.GROUP_CHAT.value
            adapter.setShowingRemoveButtonFlag(ssmConversation.type == ChatEnum.ConversationType.GROUP_CHAT.value)
            viewModel.getSsmConversationInfo(ssmConversation.conversationId)
        }
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer {
            val response = it ?: return@Observer
            viewModel.conversationSetting.value = response.settings
            viewModel.groupName.value = response.settings.title

            viewModel.members.clear()
            viewModel.members.addAll(response.members)
            adapter.notifyDataSetChanged()
        })

        viewModel.removeMembersStatus.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    viewModel.members.removeAll(viewModel.removedMembers)
                    adapter.notifyDataSetChanged()
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    println("Do nothing for other state")
                }
            }
        })

        viewModel.updateSettingStatus.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> ViewUtil.showMessage(apiStatus?.body?.result)
                FAIL -> ViewUtil.showMessage(apiStatus.error?.error)
                else -> println("Do nothing for update settings")
            }
        })

        //todo: to refine
        viewModel.conversationId.observe(this, Observer { id ->
            if (id != null && NumberUtil.isNaturalNumber(id)) {
                ChatMessagingActivity.launchByClearTop(activity = this, conversationId = id)
            } else {
                ChatMessagingActivity.launchByClearTop(activity = this, user = viewModel.userPO)
            }
        })
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvAddAgents.setOnClickListener {
            SRXAgentsDialogFragment.launch(
                supportFragmentManager,
                SRXAgentsDialogFragment.SRXAgentsSource.SOURCE_MESSAGING_SCREEN
            )
        }

        binding.tvEdit.setOnClickListener {
            viewModel.editMode()
            val isEditTitle = viewModel.editTitle.value ?: return@setOnClickListener
            if (isEditTitle) {
                binding.etGroupName.isEnabled = true
                binding.etGroupName.requestFocus()
                ViewUtil.showKeyboard(binding.etGroupName)
            }
        }
    }

    private fun onCallMember(number: Int) {
        viewModel.phoneNumberToCall = number
        IntentUtil.dialPhoneNumber(this, number.toString())
    }

    private fun onMessageMember(user: UserPO) {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.CHAT,
            function = AccessibilityEnum.InAccessibleFunction.CHAT_CREATE,
            onSuccessAccessibility = {
                viewModel.userPO = user
                viewModel.findConversationByUserId(user.id)
            }
        )
    }

    private fun showAgentCv(user: UserPO) {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.AGENT_CV,
            onSuccessAccessibility = {
                AgentCvActivity.launch(this, user.id)
            })
    }

    private fun showRemoveMemberConfirmationDialog(members: ArrayList<UserPO>) {
        DialogUtil(this).showActionDialog(R.string.msg_remove_members) {
            viewModel.removedMembers.addAll(members)
            viewModel.removeMembersFromConversation(members)
        }
    }

    fun addMembersToConversation(agents: ArrayList<AgentPO>) {
        viewModel.addMembersToConversation(agents)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_CALL -> {
                PermissionUtil.handlePermissionResult(
                    Manifest.permission.CALL_PHONE,
                    permissions,
                    grantResults
                ) {
                    IntentUtil.dialPhoneNumber(this, viewModel.phoneNumberToCall.toString())
                }
                return
            } //end of request code call
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_chat_messaging_info
    }

    override fun getViewModelClass(): Class<ChatMessagingInfoViewModel> {
        return ChatMessagingInfoViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}