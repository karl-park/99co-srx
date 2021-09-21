package sg.searchhouse.agentconnect.view.activity.report.newlaunches

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.card_project_search_header.*
import kotlinx.android.synthetic.main.layout_bottom_action_bar.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityNewLaunchesReportBinding
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_HDB_TOWN_IDS
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_HDB_TOWN_NAMES
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_QUERY
import sg.searchhouse.agentconnect.view.activity.report.newlaunches.FilterNewLaunchesActivity.Companion.EXTRA_KEY_COMPLETION
import sg.searchhouse.agentconnect.view.activity.report.newlaunches.FilterNewLaunchesActivity.Companion.EXTRA_KEY_CD_RESEARCH_SUB_TYPES
import sg.searchhouse.agentconnect.view.activity.report.newlaunches.FilterNewLaunchesActivity.Companion.EXTRA_KEY_PROPERTY_MAIN_TYPE
import sg.searchhouse.agentconnect.view.activity.report.newlaunches.FilterNewLaunchesActivity.Companion.EXTRA_KEY_TENURE
import sg.searchhouse.agentconnect.view.adapter.report.newlaunches.NewLaunchesReportsAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches.NewLaunchesReportsViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel.*

class NewLaunchesReportsActivity :
    ViewModelActivity<NewLaunchesReportsViewModel, ActivityNewLaunchesReportBinding>(isSliding = true) {

    private lateinit var adapter: NewLaunchesReportsAdapter

    companion object {
        private const val REQUEST_CODE_FILTER_PROJECT = 1
        private const val REQUEST_CODE_SEND_REPORTS = 2
        private const val REQUEST_CODE_SEARCH_REPORTS = 3

        private const val MAX_REPORT_COUNT = 5

        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, NewLaunchesReportsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
        setupAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtraParams() {
        intent?.extras?.getString(ProjectSearchActivity.EXTRA_KEY_DISTRICT_IDS)
            ?.let { districtIds ->
                intent?.extras?.getString(ProjectSearchActivity.EXTRA_KEY_DISTRICT_NAMES)
                    ?.let { districtNames ->
                        viewModel.selectedDistrictTownNames.postValue(districtNames)
                    }
                viewModel.selectedDistrictIds.postValue(districtIds)
            }

        intent?.extras?.getString(EXTRA_KEY_HDB_TOWN_IDS)?.let { hdbTownIds ->
            intent?.extras?.getString(EXTRA_KEY_HDB_TOWN_NAMES)?.let { hdbTownsNames ->
                viewModel.selectedHdbTownNames.postValue(hdbTownsNames)
            }
            viewModel.selectedHdbTownIds.postValue(hdbTownIds)
        }

        intent?.extras?.get(EXTRA_KEY_PROPERTY_MAIN_TYPE)?.let { mainType ->
            println(mainType)
        }

        intent?.extras?.getString(EXTRA_KEY_QUERY)?.let { query ->
            viewModel.searchQuery.postValue(query)
        }
    }

    private fun setupAdapter() {
        adapter = NewLaunchesReportsAdapter(
            viewModel.reports,
            onCheckEachReport = { handleSelectedNewLaunches(it) },
            onDownloadReport = {
                if (!TextUtils.isEmpty(it.reportUrl)) {
                    ViewUtil.showMessage(
                        getString(
                            R.string.message_download_new_launches_report,
                            it.name
                        )
                    )
                    viewModel.downloadReport(it)
                }
            })
        binding.listProjects.layoutManager = LinearLayoutManager(this)
        binding.listProjects.adapter = adapter
    }

    private fun handleSelectedNewLaunches(project: GetNewLaunchesResponse.NewLaunchProject) {
        val items = viewModel.selectedReports.value ?: arrayListOf()
        val contain = items.any { it.crsId == project.crsId }
        if (contain) {
            items.remove(project)
        } else {
            if (items.size < MAX_REPORT_COUNT) {
                items.add(project)
            } else {
                ViewUtil.showMessage(R.string.msg_max_5_reports)
            }
        }
        viewModel.selectedReports.value = items
    }

    private fun performRequest() {
        viewModel.performNewLaunchesRequest()
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer {
            val response = it ?: return@Observer
            viewModel.reports.removeAll(viewModel.reports.filterIsInstance<Loading>())
            if (viewModel.page == 1) {
                viewModel.reports.clear()
            }
            viewModel.reports.addAll(response.result)
            adapter.notifyDataSetChanged()
        })

        viewModel.selectedReports.observe(this, Observer { list ->
            val items = list ?: return@Observer
            adapter.updateSelectedReportIds(items.map { it.crsId })
        })

        viewModel.orderCriteria.observe(this, Observer { type ->
            if (type != null) {
                performRequest()
            }
        })

        viewModel.searchQuery.observe(this, Observer { query ->
            if (query != null) {
                if (!TextUtils.isEmpty(query)) {
                    viewModel.searchText.postValue(query)
                } else {
                    viewModel.searchText.postValue(getString(R.string.label_listings_everywhere))
                }
            }
            performRequest()
        })

        viewModel.searchText.observe(this, Observer { searchText ->
            viewModel.showCloseBtn.value =
                !TextUtils.isEmpty(searchText) && searchText != getString(R.string.label_listings_everywhere)
        })

        viewModel.selectedHdbTownIds.observe(this, Observer { hdbTownIds ->
            if (hdbTownIds != null) {
                performRequest()
            }
        })

        viewModel.selectedHdbTownNames.observe(this, Observer {
            val townNames = it ?: return@Observer
            viewModel.searchText.postValue(townNames)
        })

        viewModel.selectedDistrictIds.observe(this, Observer { districtIds ->
            if (districtIds != null) {
                performRequest()
            }
        })

        viewModel.selectedDistrictTownNames.observe(this, Observer {
            val districtNames = it ?: return@Observer
            viewModel.searchText.postValue(districtNames)
        })

        viewModel.getNewLaunchesCountResponse.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        })
    }

    private fun handleListeners() {
        btn_action.setOnClickListener {
            viewModel.selectedReports.value?.run {
                SendNewLaunchesReportsActivity.launch(
                    this@NewLaunchesReportsActivity,
                    this.toList(),
                    REQUEST_CODE_SEND_REPORTS
                )
            }
        }
        btn_back.setOnClickListener { onBackPressed() }
        btn_filter_result.setOnClickListener {
            FilterNewLaunchesActivity.launch(
                activity = this,
                requestCode = REQUEST_CODE_FILTER_PROJECT,
                propertyMainTypes = viewModel.selectedPropertyMainTypes,
                cdResearchSubTypes = viewModel.selectedCdResearchSubTypes,
                tenure = viewModel.selectedTenure,
                completion = viewModel.selectedCompletion,
                searchText = viewModel.searchQuery.value,
                districtTownIds = viewModel.selectedDistrictIds.value,
                hdbTownIds = viewModel.selectedHdbTownIds.value
            )
        }
        tv_selected_sort_option.setOnClickListener { showSortOptionDialog() }

        ViewUtil.listenVerticalScrollEnd(binding.listProjects, reachBottom = {
            if (viewModel.canLoadNext() && viewModel.reports.last() !is Loading) {
                viewModel.reports.add(Loading())
                adapter.notifyItemChanged(viewModel.reports.size - 1)
                viewModel.page = viewModel.page + 1
                viewModel.loadMoreNewLaunchesReports()
            }
        })

        btn_search.setOnClickListener {
            ProjectSearchActivity.launch(
                this,
                REQUEST_CODE_SEARCH_REPORTS,
                searchType = SearchResultType.NEW_LAUNCHES_REPORTS
            )
        }

        btn_clear.setOnClickListener {
            resetLocationSearch()
        }
    }

    private fun resetLocationSearch() {
        viewModel.selectedDistrictIds.value = null
        viewModel.selectedDistrictTownNames.value = null
        viewModel.selectedHdbTownIds.value = null
        viewModel.selectedHdbTownNames.value = null
        viewModel.searchText.value = null
        viewModel.searchQuery.value = null
    }

    private fun showSortOptionDialog() {
        val sortOptions = ReportEnum.NewLaunchesReportSortType.values().toList()
        val sortOptionLabels = sortOptions.map { it.label }
        dialogUtil.showListDialog(sortOptionLabels, { _, position ->
            viewModel.orderCriteria.postValue(sortOptions[position])
        }, R.string.label_sort_by)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_FILTER_PROJECT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intent = data ?: return
                    setupFilterParams(intent)
                }
            }
            REQUEST_CODE_SEND_REPORTS -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.selectedReports.value = arrayListOf()
                    performRequest()
                }
            }
            REQUEST_CODE_SEARCH_REPORTS -> {
                if (resultCode == Activity.RESULT_OK) {
                    finish()
                }
            }
        }
    }

    private fun setupFilterParams(intent: Intent) {
        if (intent.hasExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE)) {
            viewModel.selectedPropertyMainTypes =
                intent.getStringExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE)
        }
        if (intent.hasExtra(EXTRA_KEY_CD_RESEARCH_SUB_TYPES)) {
            viewModel.selectedCdResearchSubTypes =
                intent.getStringExtra(EXTRA_KEY_CD_RESEARCH_SUB_TYPES)
        }
        if (intent.hasExtra(EXTRA_KEY_TENURE)) {
            viewModel.selectedTenure = intent.getStringExtra(EXTRA_KEY_TENURE)
        }
        if (intent.hasExtra(EXTRA_KEY_COMPLETION)) {
            viewModel.selectedCompletion = intent.getStringExtra(EXTRA_KEY_COMPLETION)
        }
        viewModel.selectedReports.value = arrayListOf()
        performRequest()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_new_launches_report
    }

    override fun getViewModelClass(): Class<NewLaunchesReportsViewModel> {
        return NewLaunchesReportsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}