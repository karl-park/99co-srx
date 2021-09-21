package sg.searchhouse.agentconnect.view.activity.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_filter_project.*
import kotlinx.android.synthetic.main.button_filter_property_type_residential.view.*
import kotlinx.android.synthetic.main.layout_filter_project_residential_property_main_types.*
import kotlinx.android.synthetic.main.layout_range_number_boxes.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityFilterProjectBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.model.api.project.FilterProjectEditPO
import sg.searchhouse.agentconnect.view.activity.project.ProjectDirectoryActivity.*
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.project.*
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.FilterProjectViewModel

class FilterProjectActivity :
    ViewModelActivity<FilterProjectViewModel, ActivityFilterProjectBinding>(isSliding = true) {

    companion object {
        private const val AGE_MAX_VALUE = 50
        private const val EXTRA_KEY_PRE_FILTER_PROJECT_EDIT_PO =
            "EXTRA_KEY_PRE_FILTER_PROJECT_EDIT_PO"
        private const val EXTRA_KEY_PROJECT_SEARCH_TYPE = "EXTRA_KEY_PROJECT_SEARCH_TYPE"
        const val EXTRA_KEY_FILTER_PROJECT_EDIT_PO = "EXTRA_KEY_FILTER_PROJECT_EDIT_PO"

        fun launch(
            activity: Activity,
            requestCode: Int,
            filterProject: FilterProjectEditPO? = null,
            projectSearchType: ProjectSearchType? = null
        ) {
            val intent = Intent(activity, FilterProjectActivity::class.java)
            filterProject?.let { intent.putExtra(EXTRA_KEY_PRE_FILTER_PROJECT_EDIT_PO, it) }
            projectSearchType?.let { intent.putExtra(EXTRA_KEY_PROJECT_SEARCH_TYPE, it) }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
        setupToolbarTitle()
        populateTypeOfArea()
        populateCompletion()
        populateTenures()
        setupRanges()
        observeStatusLiveData()
        handlePropertyMainTypeClickListeners()
        handleListeners()
    }

    private fun setupExtraParams() {
        if (intent.hasExtra(EXTRA_KEY_PRE_FILTER_PROJECT_EDIT_PO)) {
            viewModel.prevFilterProject.value =
                intent.getSerializableExtra(EXTRA_KEY_PRE_FILTER_PROJECT_EDIT_PO) as FilterProjectEditPO
        }

        if (intent.hasExtra(EXTRA_KEY_PROJECT_SEARCH_TYPE)) {
            viewModel.projectSearchType.value =
                intent.getSerializableExtra(EXTRA_KEY_PROJECT_SEARCH_TYPE) as ProjectSearchType
        } else {
            viewModel.showRadiusOption.postValue(true)
            populateRadius()
        }
    }

    private fun setupToolbarTitle() {
        supportActionBar?.title = getString(R.string.activity_filter_listing_results)
    }

    private fun populateTypeOfArea() {
        viewModel.typeOfAreasList = ProjectEnum.TypeOfArea.values().map { addEachTypeOfArea(it) }
    }

    private fun addEachTypeOfArea(typeOfArea: ProjectEnum.TypeOfArea): FilterProjectTypeOfAreaPill {
        val pill = FilterProjectTypeOfAreaPill(this)
        pill.binding.typeOfArea = typeOfArea
        pill.binding.viewModel = viewModel
        pill.binding.btnFilterPill.setOnClickListener {
            viewModel.selectedTypeOfArea.value = typeOfArea
        }
        binding.layoutTypeOfArea.addView(pill)
        return pill
    }

    private fun populateCompletion() {
        viewModel.completionList = ProjectEnum.Completion.values().map { addCompletion(it) }
    }

    private fun addCompletion(completion: ProjectEnum.Completion): FilterProjectCompletionPill {
        val pill = FilterProjectCompletionPill(this)
        pill.binding.completion = completion
        pill.binding.isSelected = viewModel.hasCompletion(completion)
        pill.binding.btnFilterPill.setOnClickListener {
            viewModel.selectedCompletion.value = completion
        }
        binding.layoutCompletion.addView(pill)
        return pill
    }

    private fun populateRadius() {
        viewModel.radiusList = ProjectEnum.ProjectRadius.values().map { addRadius(it) }
    }

    private fun addRadius(radius: ProjectEnum.ProjectRadius): FilterProjectRadiusPill {
        val pill = FilterProjectRadiusPill(this)
        pill.binding.radius = radius
        pill.binding.viewModel = viewModel
        pill.binding.btnFilterProject.setOnClickListener {
            viewModel.selectedRadius.value = radius
        }
        binding.layoutRadius.addView(pill)
        return pill
    }

    private fun populateTenures() {
        viewModel.tenureList = ProjectEnum.Tenure.values().map { addTenure(it) }
    }

    private fun addTenure(tenure: ProjectEnum.Tenure): FilterProjectTenurePill {
        val pill = FilterProjectTenurePill(this)
        pill.binding.tenure = tenure
        pill.binding.isSelected = viewModel.isTenureSelected(tenure)
        pill.binding.btnFilterProject.setOnClickListener {
            viewModel.selectedTenure.value = tenure
        }
        binding.layoutTenure.addView(pill)
        return pill
    }

    private fun setupRanges() {
        val transformer = { number: Int?, numberString: String? ->
            if (number != null) {
                "$numberString"
            } else {
                getString(R.string.label_any)
            }
        }
        layout_age.et_min.setup(AGE_MAX_VALUE, transformer) { minAge ->
            viewModel.minAge.value = minAge
        }
        layout_age.et_max.setup(AGE_MAX_VALUE, transformer) { maxAge ->
            viewModel.maxAge.value = maxAge
        }
    }

    private fun observeStatusLiveData() {
        viewModel.selectedTypeOfArea.observe(this, Observer { typeOfArea ->
            if (typeOfArea != null) {
                viewModel.typeOfAreasList.map { it.binding.invalidateAll() }
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
                layout_age.et_min.isEnabled = completion == ProjectEnum.Completion.ANY
                layout_age.et_max.isEnabled = completion == ProjectEnum.Completion.ANY
            }
        })

        viewModel.selectedRadius.observe(this, Observer { radius ->
            if (radius != null) {
                viewModel.radiusList.map { it.binding.invalidateAll() }
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
            }
        })

        viewModel.propertySubTypes.observe(this, Observer { subTypes ->
            if (subTypes != null) {
                viewModel.propertySubTypeList.map {
                    it.binding.propertySubType?.let { subType ->
                        it.binding.isSelected = viewModel.hasPropertySubType(subType)
                    }
                    it.binding.invalidateAll()
                }
            }
        })

        viewModel.propertyMainType.observe(this, Observer {
            val mainType = it ?: return@Observer
            populatePropertySubTypesByMainType(mainType)
        })

        viewModel.prevFilterProject.observe(this, Observer { editPO ->
            val filterProject = editPO ?: return@Observer

            //property type
            viewModel.prevPropertySubTypes =
                filterProject.cdResearchSubtypes?.split(",")?.mapNotNull { type ->
                    PropertySubType.values().find { it.type == NumberUtil.toInt(type) }
                }
            viewModel.propertyMainType.value = filterProject.propertyMainType

            //completion
            viewModel.selectedCompletion.value =
                ProjectEnum.Completion.values().find { it.value == filterProject.age }
                    ?: ProjectEnum.Completion.ANY
            //age
            viewModel.minAge.value = filterProject.ageMin
            viewModel.maxAge.value = filterProject.ageMax
            updateAgeMinMaxValues(filterProject.ageMin, filterProject.ageMax)
            //Radius
            viewModel.selectedRadius.value =
                ProjectEnum.ProjectRadius.values().find { it.value == filterProject.radius }
                    ?: ProjectEnum.ProjectRadius.ANY
            //Tenure
            viewModel.selectedTenure.value =
                ProjectEnum.Tenure.values().find { it.value == filterProject.tenureType }
                    ?: ProjectEnum.Tenure.ANY
            //Type of Area
            viewModel.selectedTypeOfArea.value =
                ProjectEnum.TypeOfArea.values().find { it.value == filterProject.typeOfArea }
                    ?: ProjectEnum.TypeOfArea.ANY
        })

        viewModel.projectSearchType.observe(this, Observer { searchType ->
            when (searchType) {
                ProjectSearchType.HDB -> {
                    viewModel.showRadiusOption.postValue(false)
                    viewModel.propertyMainType.postValue(PropertyMainType.HDB)
                }
                ProjectSearchType.DISTRICT -> {
                    viewModel.showRadiusOption.postValue(false)
                }
                else -> {
                    //do nothing for now
                }
            }
        })
    }

    private fun handlePropertyMainTypeClickListeners() {
        btn_property_main_type_residential.button.setOnClickListener {
            viewModel.propertyMainType.postValue(PropertyMainType.RESIDENTIAL)
        }
        btn_property_main_type_hdb.button.setOnClickListener {
            viewModel.propertyMainType.postValue(PropertyMainType.HDB)
        }
        btn_property_main_type_condo.button.setOnClickListener {
            viewModel.propertyMainType.postValue(PropertyMainType.CONDO)
        }
        btn_property_main_type_landed.button.setOnClickListener {
            viewModel.propertyMainType.postValue(PropertyMainType.LANDED)
        }
    }

    private fun populatePropertySubTypesByMainType(mainType: PropertyMainType) {
        layout_property_sub_types.removeAllViews()
        when (mainType) {
            PropertyMainType.RESIDENTIAL -> {
                viewModel.propertySubTypes.postValue(null)
            }
            else -> {
                viewModel.propertySubTypeList =
                    mainType.propertySubTypes.map { addPropertySubTypeButton(it) }
                val previousPropertySubTypes = viewModel.prevPropertySubTypes ?: emptyList()
                val hasDefaultSubTypes =
                    previousPropertySubTypes.isNotEmpty() && mainType.propertySubTypes.intersect(
                        previousPropertySubTypes
                    ).isNotEmpty()
                if (hasDefaultSubTypes) {
                    viewModel.propertySubTypes.postValue(previousPropertySubTypes)
                } else {
                    viewModel.propertySubTypes.postValue(mainType.propertySubTypes)
                }
            }
        }
    }

    private fun addPropertySubTypeButton(propertySubType: PropertySubType): FilterProjectPropertySubTypePill {
        val pill = FilterProjectPropertySubTypePill(this)
        pill.binding.propertySubType = propertySubType
        pill.binding.isSelected = viewModel.hasPropertySubType(propertySubType)
        pill.binding.button.setOnClickListener {
            viewModel.updatePropertySubTypes(propertySubType)
        }
        layout_property_sub_types.addView(pill)
        return pill
    }

    private fun handleListeners() {
        binding.btnSubmit.setOnClickListener { submitFilterProjects() }
    }

    private fun submitFilterProjects() {
        //property type
        viewModel.filterProject.propertyMainType = viewModel.propertyMainType.value
        viewModel.filterProject.cdResearchSubtypes =
            viewModel.propertySubTypes.value?.map { it.type }?.joinToString(",")

        //radius
        viewModel.selectedRadius.value?.let { projectRadius ->
            if (projectRadius == ProjectEnum.ProjectRadius.ANY) {
                viewModel.filterProject.radius = null
            } else {
                viewModel.filterProject.radius = projectRadius.value
            }
        }

        //tenure
        viewModel.selectedTenure.value?.let { tenure ->
            if (tenure == ProjectEnum.Tenure.ANY) {
                viewModel.filterProject.tenureType = null
            } else {
                viewModel.filterProject.tenureType = tenure.value
            }
        }

        //type of area
        viewModel.selectedTypeOfArea.value?.let { typeOfArea ->
            if (typeOfArea == ProjectEnum.TypeOfArea.ANY) {
                viewModel.filterProject.typeOfArea = null
            } else {
                viewModel.filterProject.typeOfArea = typeOfArea.value
            }
        }

        //completion
        viewModel.selectedCompletion.value?.let { completion ->
            if (completion == ProjectEnum.Completion.ANY) {
                viewModel.filterProject.age = null
            } else {
                viewModel.filterProject.age = completion.value
            }
        }

        //Age
        viewModel.filterProject.ageMax = viewModel.maxAge.value
        viewModel.filterProject.ageMin = viewModel.minAge.value

        val intent = Intent()
        intent.putExtra(EXTRA_KEY_FILTER_PROJECT_EDIT_PO, viewModel.filterProject)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_transaction, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_item_reset -> {
                resetParams()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun resetParams() {
        //Reset all data
        viewModel.selectedTypeOfArea.value = ProjectEnum.TypeOfArea.ANY
        viewModel.selectedCompletion.value = ProjectEnum.Completion.ANY
        viewModel.selectedRadius.value = ProjectEnum.ProjectRadius.ANY
        viewModel.selectedTenure.value = ProjectEnum.Tenure.ANY

        viewModel.minAge.value = null
        viewModel.maxAge.value = null
        updateAgeMinMaxValues(null, null)

        viewModel.prevFilterProject.value = null
        viewModel.prevPropertySubTypes = null

        viewModel.propertyPurpose.value = PropertyPurpose.RESIDENTIAL
        when (viewModel.projectSearchType.value) {
            ProjectSearchType.HDB -> viewModel.propertyMainType.value = PropertyMainType.HDB
            else -> viewModel.propertyMainType.value = PropertyMainType.RESIDENTIAL
        }
    }

    private fun updateAgeMinMaxValues(minValue: Int?, maxValue: Int?) {
        layout_age.et_min.setNumber(minValue)
        layout_age.et_max.setNumber(maxValue)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_filter_project
    }

    override fun getViewModelClass(): Class<FilterProjectViewModel> {
        return FilterProjectViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}