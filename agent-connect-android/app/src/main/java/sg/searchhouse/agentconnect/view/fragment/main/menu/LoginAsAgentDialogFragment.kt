package sg.searchhouse.agentconnect.view.fragment.main.menu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_fragment_login_as_agent.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentLoginAsAgentBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.findagent.FindAgentAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.menu.LoginAsAgentViewModel
import java.util.*

class LoginAsAgentDialogFragment :
    ViewModelDialogFragment<LoginAsAgentViewModel, DialogFragmentLoginAsAgentBinding>() {

    private lateinit var adapter: FindAgentAdapter
    private var timer = Timer()

    companion object {
        private const val TAG_LOGIN_AS_AGENT_DIALOG_FRAGMENT = "TAG_LOGIN_AS_AGENT_DIALOG_FRAGMENT"
        fun newInstance() = LoginAsAgentDialogFragment()

        fun launch(fm: FragmentManager) {
            newInstance().show(fm, TAG_LOGIN_AS_AGENT_DIALOG_FRAGMENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        observeLiveData()
        handleListeners()
    }

    private fun setupList() {
        adapter = FindAgentAdapter(viewModel.agents, onItemClickListener = { agentPO ->
            showConfirmationDialog(agentPO)
        })
        list_agents.layoutManager = this.activity?.let { LinearLayoutManager(it) }
        list_agents.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(viewLifecycleOwner, Observer {
            val response = it ?: return@Observer
            adapter.updateAllAgents(response.agents)
        })

        viewModel.loginAsAgentResponse.observe(viewLifecycleOwner, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val currentUser = SessionUtil.getCurrentUser() ?: return@Observer
                    if (AuthUtil.isStreetSineUser(currentUser.email)) {
                        SessionUtil.setStreetSineUser(currentUser)
                    }
                    ViewUtil.showMessage(it.body?.result)
                    RxBus.publish(LoginAsAgentEvent())
                    dialog?.dismiss()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    println("do nothing")
                }
            }
        })
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener { dialog?.dismiss() }
        //TODO: to refine handling search text in future -> not a good way to do like that. urgent fixes for CS
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //wait 1 sec after text changed.
                val searchText = s?.toString() ?: return
                try {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            if (searchText.length > 2 || searchText.isEmpty()) {
                                viewModel.findAgentsBySearchKeyword(searchText)
                            }
                        }
                    }, 1000L)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    //terminate timer and discarding scheduled tasks. and remove all cancelled tasks
                    timer.cancel()
                    timer.purge()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun showConfirmationDialog(agent: AgentPO) {
        val mContext = this.context ?: return
        DialogUtil(mContext).showActionDialog(
            getString(
                R.string.msg_confirm_to_login_as_agent,
                agent.name
            )
        ) {
            viewModel.loginAsAgent(agent)
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_login_as_agent
    }

    override fun getViewModelClass(): Class<LoginAsAgentViewModel> {
        return LoginAsAgentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}