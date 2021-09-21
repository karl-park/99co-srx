package sg.searchhouse.agentconnect.view.activity.agent.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.card_my_sg_home_clients_content.*
import kotlinx.android.synthetic.main.card_my_sg_home_clients_header.*
import kotlinx.android.synthetic.main.layout_bottom_action_bar.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityMySgHomeClientsBinding
import sg.searchhouse.agentconnect.enumeration.app.ClientModeOption
import sg.searchhouse.agentconnect.model.api.agent.SRXPropertyUserPO
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.JsonUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.agent.client.AgentClientAdapter
import sg.searchhouse.agentconnect.view.fragment.agent.client.InviteAgentClientDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.MySgHomeClientsViewModel

class MySgHomeClientsActivity :
    ViewModelActivity<MySgHomeClientsViewModel, ActivityMySgHomeClientsBinding>() {
    private val adapter = AgentClientAdapter(
        onItemClicked = { client ->
            IntentUtil.visitUrl(
                this@MySgHomeClientsActivity,
                client.urlForInviterView
            )
        },
        onPhoneActionClicked = { mobileNum ->
            IntentUtil.dialPhoneNumber(this, mobileNum.toString())
        }, onCheckBoxClicked = { client ->
            handleClientListSelection(client)
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        observeLiveData()
        viewModel.performRequest()
    }

    private fun setupView() {
        setupList()
        setOnClickListeners()
    }

    private fun setupList() {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
        layout_swipe_refresh.setOnRefreshListener {
            viewModel.setQuery("")
            layout_swipe_refresh.isRefreshing = false
        }
    }

    fun observeLiveData() {
        viewModel.clients.observe(this, Observer { clients ->
            adapter.items = clients ?: emptyList()
            adapter.notifyDataSetChanged()
        })
        viewModel.propertyPO.observe(this, Observer {
            viewModel.setQuery(it.postalCode.toString(), it.address)
        })
        viewModel.sortOrder.observe(this, Observer {
            viewModel.performRequest()
        })
        viewModel.query.observe(this, Observer {
            viewModel.performRequest()
        })
        viewModel.selectedClients.observe(this, Observer { clients ->
            adapter.selectedItems = clients.map { it.ptUserId }
            adapter.notifyDataSetChanged()
        })
    }

    private fun setOnClickListeners() {
        btn_back.setOnClickListener { finish() }
        btn_invite_clients.setOnClickListener {
            InviteAgentClientDialogFragment.launch(supportFragmentManager)
        }
        btn_title.setOnClickListener {
            launchActivityForResult(
                SearchClientActivity::class.java,
                requestCode = REQUEST_CODE_SEARCH_CLIENT
            )
        }
        btn_sort_address.setOnClickListener { viewModel.toggleSortOrder() }
        btn_sort_user.setOnClickListener { viewModel.toggleSortOrder() }
        btn_action.setOnClickListener { showClientOptionsDialog() }
    }

    private fun showClientOptionsDialog() {
        val list = ClientModeOption.values().map { it.label }
        dialogUtil.showListDialog(list, { dialogInterface, position ->
            val option = ClientModeOption.values()[position]
            val items = viewModel.selectedClients.value?.toList() ?: emptyList()
            ClientSendMessageActivity.launch(
                this,
                optionMode = option,
                clients = JsonUtil.parseToJsonString(items),
                requestCode = REQUEST_CODE_SEND_MESSAGE
            )
            dialogInterface.dismiss()
        })
    }

    private fun handleClientListSelection(client: SRXPropertyUserPO) {
        val clients = viewModel.selectedClients.value ?: arrayListOf()
        if (clients.contains(client)) {
            clients.remove(client)
        } else {
            clients.add(client)
        }
        viewModel.selectedClients.value = clients
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SEARCH_CLIENT -> {
                when (resultCode) {
                    SearchClientActivity.RESULT_ADDRESS_OK -> {
                        val propertyPO =
                            data?.extras?.getSerializable(SearchClientActivity.EXTRA_OUTPUT_PROPERTY_PO) as PropertyPO?
                                ?: throw IllegalStateException("Missing result `EXTRA_OUTPUT_PROPERTY_PO` from `SearchCli entActivity`")
                        viewModel.propertyPO.postValue(propertyPO)
                    }
                    SearchClientActivity.RESULT_CLIENT_NAME_OK -> {
                        val clientName =
                            data?.extras?.getSerializable(SearchClientActivity.EXTRA_OUTPUT_CLIENT_NAME) as String?
                                ?: throw IllegalStateException("Missing result `EXTRA_OUTPUT_CLIENT_NAME` from `SearchClientActivity`")
                        viewModel.setQuery(clientName)
                    }
                }
            }
            REQUEST_CODE_SEND_MESSAGE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        viewModel.selectedClients.value = arrayListOf()
                        adapter.selectedItems = listOf()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_my_sg_home_clients
    }

    override fun getViewModelClass(): Class<MySgHomeClientsViewModel> {
        return MySgHomeClientsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    companion object {
        private const val REQUEST_CODE_SEARCH_CLIENT = 1

        private const val REQUEST_CODE_SEND_MESSAGE = 2
    }
}