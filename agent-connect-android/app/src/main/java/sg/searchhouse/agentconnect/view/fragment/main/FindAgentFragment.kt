package sg.searchhouse.agentconnect.view.fragment.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentFindAgentBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.app.UpdateUserProfileEvent
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.agent.cv.AgentCvActivity
import sg.searchhouse.agentconnect.view.activity.agent.cv.CvCreateUrlActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.DistrictSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.HdbTownSearchActivity
import sg.searchhouse.agentconnect.view.adapter.findagent.FindAgentAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.FindAgentViewModel

class FindAgentFragment : ViewModelFragment<FindAgentViewModel, FragmentFindAgentBinding>() {

    companion object {
        const val REQUEST_CODE_AD_HDB_TOWN = 201
        const val REQUEST_CODE_AD_DISTRICT = 200

        fun newInstance() = FindAgentFragment()
    }

    private lateinit var mContext: Context
    private lateinit var adapter: FindAgentAdapter
    private var selectedAgentId: Int = 0
    private var currentUser = SessionUtil.getCurrentUser()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeRxBuses()
        setupListAndAdapter()
        performRequest()
        observeLiveData()
        setupLayoutEvents()
        handleListeners()
    }

    private fun observeRxBuses() {
        listenRxBus(UpdateUserProfileEvent::class.java) {
            if (currentUser == null) {
                currentUser = SessionUtil.getCurrentUser()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListAndAdapter() {
        adapter = FindAgentAdapter(
            viewModel.agents,
            onItemClickListener = { agent -> handleOnAgentItemClick(agent) }
        )
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.listAgents.layoutManager = linearLayoutManager
        binding.listAgents.adapter = adapter
        binding.listAgents.setOnTouchListener { _, _ ->
            ViewUtil.hideKeyboard(binding.layoutAgentSearch.binding.etSearchAgent)
            return@setOnTouchListener false
        }
    }

    private fun performRequest() {
        viewModel.loadAgents()
    }

    private fun observeLiveData() {
        // Handle main response
        viewModel.mainResponse.observe(this, Observer { mainResponse ->
            val response = mainResponse ?: return@Observer
            //load more complete
            viewModel.total.postValue(response.total)
            viewModel.agents.removeAll(viewModel.agents.filterIsInstance<Loading>())
            if (response.agents.isNotEmpty()) {
                if (viewModel.page == 1) {
                    viewModel.agents.clear()
                }
                viewModel.agents.addAll(response.agents)
            } else {
                viewModel.agents.clear()
            }
            adapter.notifyDataSetChanged()
        })

        viewModel.agentPO.observe(this, Observer { response ->
            response?.let {
                if (it.agentCvPO == null || (it.agentCvPO.changedPublicUrlInd == false &&
                            TextUtils.isEmpty(it.agentCvPO.publicProfileUrl))
                ) {
                    startActivity(Intent(mContext, CvCreateUrlActivity::class.java))
                } else {
                    if (selectedAgentId > 0) {
                        //already added check accessibility
                        activity?.run {
                            AgentCvActivity.launch(this, selectedAgentId)
                        }
                    }
                }
            }
        })
    }

    private fun setupLayoutEvents() {
        binding.layoutAgentSearch.searchAgentByFilter = searchAgentByFilter
        binding.layoutAgentSearch.onPressHDB = onPressHDB
        binding.layoutAgentSearch.onPressDistrict = onPressDistrict
    }

    private fun handleListeners() {
        ViewUtil.listenVerticalScrollEnd(binding.listAgents, reachBottom = {
            if (viewModel.canLoadNext() && viewModel.agents.last() !is Loading) {
                viewModel.agents.add(Loading())
                adapter.notifyItemChanged(viewModel.agents.size - 1)
                viewModel.page = viewModel.page + 1
                viewModel.loadMoreFindAgent()
            }
        })
    }

    private fun handleOnAgentItemClick(agentPO: AgentPO) {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.AGENT_CV,
            onSuccessAccessibility = {
                val user = currentUser ?: return@checkModuleAccessibility
                if (user.id == agentPO.userId) {
                    //Current Agent CV
                    selectedAgentId = agentPO.userId
                    viewModel.getAgentCv(agentPO.userId)
                } else {
                    activity?.run {
                        AgentCvActivity.launch(this, agentPO.userId)
                    }
                }
            }
        )
    }

    private val searchAgentByFilter: (String, String, String, String) -> Unit =
        { searchText, selectedDistrictIds, selectedHdbTownIds, selectedAreaSpecializations ->
            viewModel.searchText = searchText
            viewModel.selectedDistrictIds = selectedDistrictIds
            viewModel.selectedHdbTownIds = selectedHdbTownIds
            viewModel.selectedAreaSpecializations = selectedAreaSpecializations
            viewModel.loadAgents()
        }

    private val onPressHDB: (String) -> Unit = {
        val intent = Intent(mContext, HdbTownSearchActivity::class.java)
        intent.putExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_IDS, it)
        startActivityForResult(intent, REQUEST_CODE_AD_HDB_TOWN)
    }

    private val onPressDistrict: (String) -> Unit = {
        val intent = Intent(mContext, DistrictSearchActivity::class.java)
        intent.putExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_IDS, it)
        startActivityForResult(intent, REQUEST_CODE_AD_DISTRICT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_AD_HDB_TOWN) {
                binding.layoutAgentSearch.getSelectedHdbTowns(
                    data?.getStringExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_IDS)
                        .toString(),
                    data?.getSerializableExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_NAMES)
                        .toString()
                )
            } else if (requestCode == REQUEST_CODE_AD_DISTRICT) {
                binding.layoutAgentSearch.getSelectedDistricts(
                    data?.getStringExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_IDS)
                        .toString()
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_find_agent
    }

    override fun getViewModelClass(): Class<FindAgentViewModel> {
        return FindAgentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

}
