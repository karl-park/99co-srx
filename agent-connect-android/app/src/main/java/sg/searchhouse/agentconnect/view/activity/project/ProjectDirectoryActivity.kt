package sg.searchhouse.agentconnect.view.activity.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.card_project_search_header.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ExtraKey
import sg.searchhouse.agentconnect.databinding.ActivityProjectDirectoryBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.model.api.project.FilterProjectEditPO
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchResultPO
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.project.FilterProjectActivity.Companion.EXTRA_KEY_FILTER_PROJECT_EDIT_PO
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_DISTRICT_IDS
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_DISTRICT_NAMES
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_HDB_TOWN_IDS
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_HDB_TOWN_NAMES
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_PROPERTY_MAIN_TYPE
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity.Companion.EXTRA_KEY_QUERY
import sg.searchhouse.agentconnect.view.adapter.project.ProjectDirectoryAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.ProjectDirectoryViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel.SearchResultType

class ProjectDirectoryActivity :
    ViewModelActivity<ProjectDirectoryViewModel, ActivityProjectDirectoryBinding>(isSliding = true) {

    private lateinit var adapter: ProjectDirectoryAdapter

    companion object {
        private const val REQUEST_CODE_FILTER_PROJECT = 1
        private const val REQUEST_CODE_PROJECT_SEARCH = 2
        private const val EXTRA_KEY_PROJECT_SOURCE = "EXTRA_KEY_PROJECT_SOURCE"

        fun launch(
            activity: Activity,
            source: Source? = null,
            propertyPurpose: ListingEnum.PropertyPurpose
        ) {
            val intent = Intent(activity, ProjectDirectoryActivity::class.java)
            source?.let { intent.putExtra(EXTRA_KEY_PROJECT_SOURCE, source) }
            intent.putExtra(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, propertyPurpose)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
        setupExtraParam()
        observeLiveData()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        adapter = ProjectDirectoryAdapter(onClickItem = {
            showProjectInfoDetailScreen(it)
        })
        binding.listProjects.layoutManager = GridLayoutManager(this, 2)
        binding.listProjects.adapter = adapter
    }

    private fun setupExtraParam() {
        // Order sensitive: Must put before HDB town IDs etc. because they trigger perform request
        val propertyPurpose =
            intent?.extras?.getSerializable(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE) as ListingEnum.PropertyPurpose?
                ?: ListingEnum.PropertyPurpose.RESIDENTIAL
        viewModel.propertyPurpose = propertyPurpose

        if (intent.hasExtra(EXTRA_KEY_PROJECT_SOURCE)) {
            viewModel.source.value = intent.getSerializableExtra(EXTRA_KEY_PROJECT_SOURCE) as Source
        } else {
            viewModel.isFilterEnabled.postValue(true)
            viewModel.showDefaultView.postValue(false)
        }

        intent?.extras?.getString(EXTRA_KEY_HDB_TOWN_IDS)?.let { hdbTownIds ->
            intent?.extras?.getString(EXTRA_KEY_HDB_TOWN_NAMES)?.let { hdbTownsNames ->
                viewModel.hdbTownNames.postValue(hdbTownsNames)
            }
            viewModel.hdbTownIds.postValue(hdbTownIds)
        }

        intent?.extras?.getString(EXTRA_KEY_DISTRICT_IDS)?.let { districtIds ->
            intent?.extras?.getString(EXTRA_KEY_DISTRICT_NAMES)?.let { districtNames ->
                viewModel.districtNames.postValue(districtNames)
            }
            viewModel.districtIds.postValue(districtIds)
        }

        intent?.extras?.get(EXTRA_KEY_PROPERTY_MAIN_TYPE)?.let { mainType ->
            val propertyMainType = mainType as ListingEnum.PropertyMainType
            viewModel.propertyMainType.postValue(propertyMainType)
        }

        intent?.extras?.getString(EXTRA_KEY_QUERY)?.let { query ->
            viewModel.searchQuery.postValue(query)
        }
    }

    private fun observeLiveData() {
        viewModel.source.observe(this, Observer { source ->
            if (source != null && source == Source.SEARCH_SHORTCUT) {
                viewModel.isFilterEnabled.postValue(false)
                viewModel.showDefaultView.postValue(true)
            }
        })

        viewModel.mainResponse.observe(this, Observer { response ->
            val result = response?.projects ?: return@Observer
            viewModel.projects.value = result
            adapter.updateProjectList(result)
        })

        viewModel.hdbTownIds.observe(this, Observer { hdbTownsIds ->
            if (hdbTownsIds != null) {
                viewModel.projectSearchType.postValue(ProjectSearchType.HDB)
                viewModel.loadProjectsByLocationSearch()
            }
        })

        viewModel.hdbTownNames.observe(this, Observer {
            val townNames = it ?: return@Observer
            viewModel.searchText.postValue(townNames)
        })

        viewModel.districtIds.observe(this, Observer { districtIds ->
            if (districtIds != null) {
                viewModel.projectSearchType.postValue(ProjectSearchType.DISTRICT)
                viewModel.loadProjectsByLocationSearch()
            }
        })

        viewModel.districtNames.observe(this, Observer {
            val districtNames = it ?: return@Observer
            viewModel.searchText.postValue(districtNames)
        })

        viewModel.searchQuery.observe(this, Observer { query ->
            if (query != null) {
                viewModel.searchText.postValue(query)
                viewModel.loadProjectsByLocationSearch()
            }
        })

        viewModel.filterEditPO.observe(this, Observer { editPO ->
            if (editPO != null) {
                viewModel.loadProjectsByFilterAndSort()
            }
        })

        viewModel.orderCriteria.observeNotNull(this) {
            //Note: search by order criteria only when update order criteria dialog
            if (viewModel.source.value != Source.SEARCH_SHORTCUT && viewModel.searchByOrderCriteriaIndicator == true) {
                viewModel.loadProjectsByFilterAndSort()
                viewModel.searchByOrderCriteriaIndicator = false
            }
        }
    }

    private fun handleListeners() {
        btn_filter_result.setOnClickListener { showFilterProjectScreen() }
        btn_search.setOnClickListener { showProjectLocationSearch() }
        btn_back.setOnClickListener { onBackPressed() }
        tv_selected_sort_option.setOnClickListener { showSortOptions() }
    }

    private fun showSortOptions() {
        val sortOptions = ProjectEnum.SortType.values().toList()
        val sortLabels = sortOptions.map { it.label }
        dialogUtil.showListDialog(sortLabels, { _, position ->
            viewModel.searchByOrderCriteriaIndicator = true
            viewModel.orderCriteria.postValue(sortOptions[position])
        }, R.string.label_sort_by)
    }

    private fun showProjectInfoDetailScreen(result: TransactionSearchResultPO) {
        if (NumberUtil.isNaturalNumber(result.id)) {
            ProjectInfoActivity.launch(this, result.id.toInt())
        }
    }

    private fun showFilterProjectScreen() {
        FilterProjectActivity.launch(
            this,
            REQUEST_CODE_FILTER_PROJECT,
            viewModel.filterEditPO.value,
            viewModel.projectSearchType.value
        )
    }

    private fun showProjectLocationSearch() {
        ProjectSearchActivity.launch(
            this,
            REQUEST_CODE_PROJECT_SEARCH,
            searchType = SearchResultType.PROJECTS,
            propertyPurpose = viewModel.propertyPurpose
                ?: ListingEnum.PropertyPurpose.RESIDENTIAL
        )
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_project_directory
    }

    override fun getViewModelClass(): Class<ProjectDirectoryViewModel> {
        return ProjectDirectoryViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_FILTER_PROJECT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intent = data ?: return
                    setupFilterParams(intent)
                }
            }
            REQUEST_CODE_PROJECT_SEARCH -> {
                if (resultCode == Activity.RESULT_OK) {
                    finish()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupFilterParams(intent: Intent) {
        if (intent.hasExtra(EXTRA_KEY_FILTER_PROJECT_EDIT_PO)) {
            val editPO =
                intent.getSerializableExtra(EXTRA_KEY_FILTER_PROJECT_EDIT_PO) as FilterProjectEditPO
            viewModel.filterEditPO.postValue(editPO)
        }
    }

    enum class Source {
        SEARCH_SHORTCUT
    }

    enum class ProjectSearchType {
        //TODO: do for district and hdb first
        DISTRICT,
        HDB
    }
}