package sg.searchhouse.agentconnect.view.fragment.project

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_planning_decisions.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentPlanningDecisionsBinding
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.view.activity.project.ProjectInfoActivity
import sg.searchhouse.agentconnect.view.adapter.project.PlanningDecisionAdapter
import sg.searchhouse.agentconnect.view.adapter.project.PlanningDecisionTypeAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.project.PlanningDecisionsViewModel

class PlanningDecisionsFragment :
    ViewModelFragment<PlanningDecisionsViewModel, FragmentPlanningDecisionsBinding>() {
    private val typeAdapter = PlanningDecisionTypeAdapter {
        viewModel.selectedDecisionType.postValue(it)
    }
    private val decisionAdapter = PlanningDecisionAdapter()

    private var projectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = activity?.intent?.getIntExtra(ProjectInfoActivity.EXTRA_PROJECT_ID, -1)
            ?: throw IllegalArgumentException("Missing project ID argument in planning decision fragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLists()
        setupOnClickListeners()
        observeLiveData()
        viewModel.performGetTypes()
    }

    private fun setupOnClickListeners() {
        btn_filter.setOnClickListener {
            val decisionDates = ProjectEnum.DecisionDate.values()
            dialogUtil.showListDialog(decisionDates.map { it.label }, { _, position ->
                viewModel.decisionDate.postValue(decisionDates[position])
            }, R.string.dialog_title_project_decision_filter_date)
        }
    }

    private fun setupLists() {
        list_planning_decision_type.run {
            layoutManager = getTypeLayoutManager()
            adapter = typeAdapter
        }
        list_planning_decision.run {
            layoutManager = getDecisionLayoutManager()
            adapter = decisionAdapter
        }
    }

    private fun getTypeLayoutManager(): LinearLayoutManager {
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        return layoutManager
    }

    private fun getDecisionLayoutManager(): LinearLayoutManager {
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = RecyclerView.VERTICAL
        return layoutManager
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(viewLifecycleOwner) { response ->
            typeAdapter.run {
                setListItems(response?.details)
                viewModel.selectedDecisionType.postValue(getFirstItem())
            }
        }
        viewModel.selectedDecisionType.observe(viewLifecycleOwner) { decisionType ->
            typeAdapter.run {
                selectedItem = decisionType
                notifyDataSetChanged()
            }
            viewModel.performGetDecisions(projectId)
        }
        viewModel.decisionsResponse.observe(viewLifecycleOwner) { response ->
            decisionAdapter.run {
                setListItems(response?.details ?: emptyList())
                notifyDataSetChanged()
            }
        }
        viewModel.decisionDate.observe(viewLifecycleOwner) {
            viewModel.performGetDecisions(projectId)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_planning_decisions
    }

    override fun getViewModelClass(): Class<PlanningDecisionsViewModel> {
        return PlanningDecisionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

    companion object {
        fun newInstance(): PlanningDecisionsFragment {
            return PlanningDecisionsFragment()
        }
    }
}