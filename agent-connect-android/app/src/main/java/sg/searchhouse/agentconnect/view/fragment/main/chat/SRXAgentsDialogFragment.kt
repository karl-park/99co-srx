package sg.searchhouse.agentconnect.view.fragment.main.chat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentSrxAgentsBinding
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingInfoActivity
import sg.searchhouse.agentconnect.view.adapter.chat.SRXAgentsListAdapter
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment
import sg.searchhouse.agentconnect.view.widget.common.ActionButton
import sg.searchhouse.agentconnect.viewmodel.fragment.main.chat.SRXAgentsDialogViewModel

class SRXAgentsDialogFragment : FullScreenDialogFragment() {

    private lateinit var binding: DialogFragmentSrxAgentsBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: SRXAgentsDialogViewModel
    private lateinit var adapter: SRXAgentsListAdapter

    private val currentUser = SessionUtil.getCurrentUser()
    private var selectedAgentPO: AgentPO? = null

    companion object {
        private const val TAG_SRX_AGENT_DIALOG = "TAG_SRX_AGENT_DIALOG"
        private const val EXTRA_KEY_SRX_AGENT_SOURCE = "EXTRA_KEY_SRX_AGENT_SOURCE"

        fun newInstance(source: SRXAgentsSource): SRXAgentsDialogFragment {
            val dialogFragment = SRXAgentsDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_SRX_AGENT_SOURCE, source)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragment: FragmentManager, source: SRXAgentsSource) {
            newInstance(source).show(fragment, TAG_SRX_AGENT_DIALOG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_srx_agents, container, false)
        viewModel = ViewModelProvider(this).get(SRXAgentsDialogViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupParamsFromExtra()
        setupListAndAdapter()
        performRequest()
        handleListeners()
        observeLiveData()
    }

    private fun setupParamsFromExtra() {
        val bundle = arguments ?: return
        val source = bundle.getSerializable(EXTRA_KEY_SRX_AGENT_SOURCE) ?: return
        viewModel.source.value = source as SRXAgentsSource
    }

    private fun setupListAndAdapter() {
        adapter = SRXAgentsListAdapter(
            viewModel.agents,
            onSelectAgentItem = { agent -> onSelectAgent(agent) }
        )
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.listSrxAgents.layoutManager = linearLayoutManager
        binding.listSrxAgents.adapter = adapter
        //register selected agent layout
        binding.layoutChatSelectedAgents.removeAgentItemFromList = removeAgentItemFromList
    }

    private fun performRequest() {
        viewModel.loadSRXAgents()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleListeners() {

        binding.toolbar.setNavigationOnClickListener { dialog?.dismiss() }

        binding.listSrxAgents.setOnTouchListener { _, _ ->
            ViewUtil.hideKeyboard(binding.etSearchAgent)
            return@setOnTouchListener false
        }

        binding.tvAddAgent.setOnClickListener { showConfirmationToAdd() }

        binding.etSearchAgent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val chars = charSequence ?: return
                if (chars.trim().length > 2 || chars.trim().isEmpty()) {
                    viewModel.searchText = chars.toString()
                    viewModel.loadSRXAgents()
                    val agents = viewModel.selectedAgents.value ?: arrayListOf()
                    binding.layoutChatSelectedAgents.populateSelectedAgents(agents)
                }
            }
        })

        ViewUtil.listenVerticalScrollEnd(binding.listSrxAgents, reachBottom = {
            if (viewModel.canLoadNext() && viewModel.agents.last() !is Loading) {
                viewModel.agents.add(Loading())
                adapter.notifyItemChanged(viewModel.agents.size - 1)
                viewModel.page = viewModel.page + 1
                viewModel.loadMoreSRXAgents()
            }
        })


        binding.tvGroup.setOnClickListener {
            val selectedAgents = viewModel.selectedAgents.value ?: return@setOnClickListener
            if (selectedAgents.size == 1) {
                //TODO: need to do checking
                val agent = selectedAgents.last()
                selectedAgentPO = agent
                viewModel.findConversationByUserId(agent.userId)
            } else {
                showGroupNameDialog()
            }
        }
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.total.postValue(it.total)
                viewModel.agents.removeAll(viewModel.agents.filterIsInstance<Loading>())
                if (it.agents.isNotEmpty()) {
                    if (viewModel.page == 1) {
                        viewModel.agents.clear()
                    }
                    viewModel.agents.addAll(response.agents)
                    //Remove if agent list includes login agent
                    currentUser?.let { user ->
                        viewModel.agents.filterIsInstance<AgentPO>().let { agents ->
                            agents.find { agent -> agent.userId == user.id }?.let { agent ->
                                viewModel.agents.remove(agent)
                            }
                        }
                    }
                } else {
                    viewModel.agents.clear()
                }
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.conversationId.removeObservers(this)
        viewModel.conversationId.observe(this, Observer { id ->
            dialog?.dismiss()
            if (id != null) {
                directToChatMessagingActivity(conversationId = id)
            } else {
                directToChatMessagingActivity(agent = selectedAgentPO)
            }
        })

        viewModel.createSsmLiveData.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    val response = apiStatus.body ?: return@Observer
                    if (!TextUtils.isEmpty(response.result)) {
                        dialog?.dismiss()
                        directToChatMessagingActivity(conversationId = response.convo.conversationId)
                    }
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    println("Do nothing for create ssm live data status")
                }
            }
        })
    }

    private fun onSelectAgent(agent: AgentPO) {
        val agents = viewModel.selectedAgents.value ?: arrayListOf()
        val index = agents.indexOfFirst { selectedAgent -> selectedAgent.userId == agent.userId }
        if (index > -1 && index < agents.size) {
            agents.removeAt(index)
        } else {
            agents.add(agent)
        }
        updateSelectedAgents(agents)
    }

    private val removeAgentItemFromList: (AgentPO) -> Unit = { agentPO ->
        val agents = viewModel.selectedAgents.value ?: arrayListOf()
        agents.remove(agentPO)
        updateSelectedAgents(agents)
    }

    private fun updateSelectedAgents(agents: ArrayList<AgentPO>) {
        binding.layoutChatSelectedAgents.populateSelectedAgents(agents)
        adapter.updateSelectedAgents(agents.map { it.userId })
        viewModel.selectedAgents.value = agents
    }

    private fun directToChatMessagingActivity(
        conversationId: String? = null,
        agent: AgentPO? = null
    ) {
        if (conversationId != null) {
            activity?.run { ChatMessagingActivity.launch(this, conversationId = conversationId) }
        } else if (agent != null) {
            activity?.run { ChatMessagingActivity.launch(this, agent = agent) }
        }
    }

    //GROUP DIALOG
    private fun showGroupNameDialog() {
        val builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog)
        builder.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.dialog_chat_group_name, null)
        val tvGroupName: EditText = view.findViewById(R.id.et_group_name)
        val btnCancel: ActionButton = view.findViewById(R.id.btn_cancel)
        val btnDone: ActionButton = view.findViewById(R.id.btn_done)
        builder.setView(view)
        //create alert dialog
        val alertDialog = builder.create()
        btnDone.setOnClickListener {
            viewModel.createGroup(tvGroupName.text.toString())
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun showConfirmationToAdd() {
        val agents = viewModel.selectedAgents.value ?: return
        DialogUtil(mContext)
            .showActionDialog(mContext.getString(R.string.msg_add_agent_to_group)) {
                if (viewModel.source.value == SRXAgentsSource.SOURCE_MESSAGING_SCREEN) {
                    //dismiss dialog
                    dialog?.dismiss()
                    //go to another screen
                    (activity as ChatMessagingInfoActivity).addMembersToConversation(agents)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    enum class SRXAgentsSource {
        SOURCE_CHAT_MAIN,
        SOURCE_MESSAGING_SCREEN
    }
}