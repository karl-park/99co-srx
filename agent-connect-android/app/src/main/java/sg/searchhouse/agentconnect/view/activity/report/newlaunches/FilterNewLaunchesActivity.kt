package sg.searchhouse.agentconnect.view.activity.report.newlaunches

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_filter_new_launches.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityFilterNewLaunchesBinding
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.report.FilterNewLaunchesCompletionPill
import sg.searchhouse.agentconnect.view.widget.report.FilterNewLaunchesPropertyTypePill
import sg.searchhouse.agentconnect.view.widget.report.FilterNewLaunchesTenurePill
import sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches.FilterNewLaunchesViewModel

class FilterNewLaunchesActivity :
    ViewModelActivity<FilterNewLaunchesViewModel, ActivityFilterNewLaunchesBinding>(isSliding = true) {

    companion object {
        const val EXTRA_KEY_PROPERTY_MAIN_TYPE = "EXTRA_KEY_PROPERTY_MAIN_TYPE"
        const val EXTRA_KEY_CD_RESEARCH_SUB_TYPES = "EXTRA_KEY_CD_RESEARCH_SUB_TYPES"
        const val EXTRA_KEY_TENURE = "EXTRA_KEY_TENURE"
        const val EXTRA_KEY_COMPLETION = "EXTRA_KEY_COMPLETION"
        const val EXTRA_KEY_SEARCH_TEXT = "EXTRA_KEY_SEARCH_TEXT"
        const val EXTRA_KEY_DISTRICT_IDS = "EXTRA_KEY_DISTRICT_IDS"
        const val EXTRA_KEY_HDB_TOWN_IDS = "EXTRA_KEY_HDB_TOWN_IDS"

        fun launch(
            activity: Activity,
            requestCode: Int,
            propertyMainTypes: String?,
            cdResearchSubTypes: String?,
            tenure: String?,
            completion: String?,
            searchText: String?,
            districtTownIds: String?,
            hdbTownIds: String?
        ) {
            val intent = Intent(activity, FilterNewLaunchesActivity::class.java)
            propertyMainTypes?.let { intent.putExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE, it) }
            cdResearchSubTypes?.let { intent.putExtra(EXTRA_KEY_CD_RESEARCH_SUB_TYPES, it) }
            tenure?.let { intent.putExtra(EXTRA_KEY_TENURE, it) }
            completion?.let { intent.putExtra(EXTRA_KEY_COMPLETION, it) }
            searchText?.let { intent.putExtra(EXTRA_KEY_SEARCH_TEXT, it) }
            districtTownIds?.let { intent.putExtra(EXTRA_KEY_DISTRICT_IDS, it) }
            hdbTownIds?.let { intent.putExtra(EXTRA_KEY_HDB_TOWN_IDS, it) }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupParamsFromExtras()
        populatePropertySubTypes()
        populateSupportToolBar()
        populateTenures()
        populateCompletion()
        observeNewLaunchesLiveData()
        handleListeners()
    }

    private fun setupParamsFromExtras() {
        intent.getStringExtra(EXTRA_KEY_SEARCH_TEXT)?.run {
            viewModel.searchText = this
        }
        intent.getStringExtra(EXTRA_KEY_DISTRICT_IDS)?.run {
            viewModel.districtTowns = this
        }
        intent.getStringExtra(EXTRA_KEY_HDB_TOWN_IDS)?.run {
            viewModel.hdbTowns = this
        }

        if (intent.hasExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE)) {
            intent.getStringExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE)?.run {
                viewModel.previousPropertySubTypes.value = this.split(",").mapNotNull { type ->
                    ReportEnum.NewLaunchesPropertyType.values()
                        .find { it.value == NumberUtil.toInt(type) }
                }
            }
        } else {
            viewModel.previousPropertySubTypes.value = null
        }

        intent.getStringExtra(EXTRA_KEY_CD_RESEARCH_SUB_TYPES)?.run {
            println("$this CD Research Subtypes")
        }
        intent.getStringExtra(EXTRA_KEY_TENURE)?.run {
            viewModel.selectedTenure.value =
                ReportEnum.NewLaunchesTenure.values().find { it.value == this }
        }
        intent.getStringExtra(EXTRA_KEY_COMPLETION)?.run {
            viewModel.selectedCompletion.value =
                ReportEnum.NewLaunchesCompletion.values().find { it.value == this }
        }
    }

    private fun populatePropertySubTypes() {
        val propertySubTypes = ReportEnum.NewLaunchesPropertyType.values().toList()
        viewModel.propertySubTypeList = propertySubTypes.map { addPropertySubTypeButton(it) }
        viewModel.propertyTypes.value = viewModel.previousPropertySubTypes.value ?: propertySubTypes
    }

    private fun addPropertySubTypeButton(propertySubType: ReportEnum.NewLaunchesPropertyType): FilterNewLaunchesPropertyTypePill {
        val pill = FilterNewLaunchesPropertyTypePill(this)
        pill.binding.propertyType = propertySubType
        pill.binding.isSelected = viewModel.hasPropertySubType(propertySubType)
        pill.binding.button.setOnClickListener { viewModel.updatePropertySubTypes(propertySubType) }
        layout_condo_sub_type.addView(pill)
        return pill
    }

    private fun populateSupportToolBar() {
        supportActionBar?.title = getString(R.string.activity_filter_listing_results)
    }

    private fun populateTenures() {
        viewModel.tenureList = ReportEnum.NewLaunchesTenure.values().map { addTenure(it) }
    }

    private fun addTenure(tenure: ReportEnum.NewLaunchesTenure): FilterNewLaunchesTenurePill {
        val pill = FilterNewLaunchesTenurePill(this)
        pill.binding.tenure = tenure
        pill.binding.isSelected = viewModel.isTenureSelected(tenure)
        pill.binding.btnFilterProject.setOnClickListener { viewModel.selectedTenure.value = tenure }
        binding.layoutTenure.addView(pill)
        return pill
    }

    private fun populateCompletion() {
        viewModel.completionList =
            ReportEnum.NewLaunchesCompletion.values().map { addCompletion(it) }
    }

    private fun addCompletion(completion: ReportEnum.NewLaunchesCompletion): FilterNewLaunchesCompletionPill {
        val pill = FilterNewLaunchesCompletionPill(this)
        pill.binding.completion = completion
        pill.binding.isSelected = viewModel.hasCompletion(completion)
        pill.binding.btnFilterPill.setOnClickListener {
            viewModel.selectedCompletion.value = completion
        }
        binding.layoutCompletion.addView(pill)
        return pill
    }

    private fun observeNewLaunchesLiveData() {
        viewModel.propertyTypes.observe(this, Observer { subTypes ->
            subTypes?.let {
                viewModel.propertySubTypeList.map { pill ->
                    pill.binding.propertyType?.let {
                        pill.binding.isSelected = viewModel.hasPropertySubType(it)
                    }
                    pill.binding.invalidateAll()
                }
                //TODO: commented for now. not sure will show number of project counts or not
//                viewModel.getNewLaunchesCountOnly()
            }
        })

        viewModel.selectedTenure.observe(this, Observer { tenure ->
            if (tenure != null) {
                viewModel.tenureList.map { pill ->
                    pill.binding.tenure?.let {
                        pill.binding.isSelected = viewModel.isTenureSelected(it)
                    }
                    pill.binding.invalidateAll()
                }
                //TODO: commented for now. not sure will show number of project counts or not
//                viewModel.getNewLaunchesCountOnly()
            }
        })

        viewModel.selectedCompletion.observe(this, Observer { completion ->
            if (completion != null) {
                viewModel.completionList.map { pill ->
                    pill.binding.completion?.let {
                        pill.binding.isSelected = viewModel.hasCompletion(it)
                    }
                    pill.binding.invalidateAll()
                }
                //TODO: commented for now. not sure will show number of project counts or not
//                viewModel.getNewLaunchesCountOnly()
            }
        })

        viewModel.newLaunchesCountStatus.observe(this, Observer {
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
        btn_submit.setOnClickListener {
            filterNewLaunches()
        }
    }

    private fun filterNewLaunches() {
        val mainPropertyTypes =
            viewModel.propertyTypes.value?.map { it.value }?.joinToString(",")
        val cdResearchSubTypes = viewModel.getCdResearchSubTypesFromTypes()
        val tenure = viewModel.getTenure()
        val completion = viewModel.getCompletion()

        val intent = Intent()
        intent.putExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE, mainPropertyTypes)
        intent.putExtra(EXTRA_KEY_CD_RESEARCH_SUB_TYPES, cdResearchSubTypes)
        intent.putExtra(EXTRA_KEY_TENURE, tenure)
        intent.putExtra(EXTRA_KEY_COMPLETION, completion)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_transaction, menu)
        return true
    }

    private fun resetFilters() {
        viewModel.propertyTypes.value = ReportEnum.NewLaunchesPropertyType.values().toList()
        viewModel.selectedTenure.value = ReportEnum.NewLaunchesTenure.ALL
        viewModel.selectedCompletion.value = ReportEnum.NewLaunchesCompletion.ALL
        viewModel.previousPropertySubTypes.value = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_item_reset -> {
                resetFilters()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_filter_new_launches
    }

    override fun getViewModelClass(): Class<FilterNewLaunchesViewModel> {
        return FilterNewLaunchesViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}