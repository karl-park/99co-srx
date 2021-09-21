package sg.searchhouse.agentconnect.view.activity.watchlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_watchlist_criteria_form.*
import kotlinx.android.synthetic.main.layout_criteria_add_on.*
import kotlinx.android.synthetic.main.layout_range_number_boxes_new.view.*
import kotlinx.android.synthetic.main.layout_watchlist_commercial_property_sub_types.view.*
import kotlinx.android.synthetic.main.layout_watchlist_types.*
import kotlinx.android.synthetic.main.pill_watchlist_property_sub_type.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityWatchlistCriteriaFormBinding
import sg.searchhouse.agentconnect.dsl.getIntListCount
import sg.searchhouse.agentconnect.dsl.isResidential
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.toUpperCaseDefault
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.dsl.widget.setOnTextChangedListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.CriteriaFormType
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.model.app.WatchlistSearchRadius
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.common.LocationSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.*
import sg.searchhouse.agentconnect.view.widget.watchlist.*
import sg.searchhouse.agentconnect.viewmodel.activity.watchlist.WatchlistCriteriaFormViewModel

abstract class WatchlistCriteriaFormActivity :
    ViewModelActivity<WatchlistCriteriaFormViewModel, ActivityWatchlistCriteriaFormBinding>() {
    private var subTypePills: List<WatchlistPropertySubTypePill>? = null
    private var areaTypePills: List<WatchlistAreaTypePill>? = null
    private var tenureTypePills: List<WatchlistTenureTypePill>? = null
    private var rentalTypePills: List<WatchlistRentalTypePill>? = null
    private var projectTypePills: List<WatchlistProjectTypePill>? = null

    private val projectRadii =
        listOf(WatchlistSearchRadius(null)) + (MIN_RADIUS_IN_METER..MAX_RADIUS_IN_METER step RADIUS_STEP).map {
            WatchlistSearchRadius(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.propertyType.observeNotNull(this) {
            populatePropertySubTypes(it)
            viewModel.propertySubTypes.postValue(
                when (it) {
                    ListingEnum.PropertyMainType.RESIDENTIAL -> null
                    else -> it.propertySubTypes
                }
            )
        }

        viewModel.isShowNameError.observeNotNull(this) {
            if (it) {
                et_criteria_name.requestFocusAndShowKeyboard()
            }
        }

        viewModel.propertySubTypes.observe(this) {
            subTypePills?.map { it.binding.invalidateAll() }
        }

        viewModel.location.observeNotNull(this) {
            if (it.isNotEmpty()) {
                viewModel.mrts.postValue(null)
                viewModel.districts.postValue(null)
                viewModel.hdbTowns.postValue(null)
                viewModel.schools.postValue(null)
            }
        }
        viewModel.mrtCount.observeNotNull(this) {
            if (it > 0) {
                viewModel.districts.postValue(null)
                viewModel.hdbTowns.postValue(null)
                viewModel.schools.postValue(null)
                viewModel.location.postValue(null)
            }
        }
        viewModel.districtCount.observeNotNull(this) {
            if (it > 0) {
                viewModel.mrts.postValue(null)
                viewModel.hdbTowns.postValue(null)
                viewModel.schools.postValue(null)
                viewModel.location.postValue(null)
            }
        }
        viewModel.hdbTownCount.observeNotNull(this) {
            if (it > 0) {
                viewModel.mrts.postValue(null)
                viewModel.districts.postValue(null)
                viewModel.schools.postValue(null)
                viewModel.location.postValue(null)
            }
        }
        viewModel.schoolCount.observeNotNull(this) {
            if (it > 0) {
                viewModel.mrts.postValue(null)
                viewModel.districts.postValue(null)
                viewModel.hdbTowns.postValue(null)
                viewModel.location.postValue(null)
            }
        }
        viewModel.areaType.observe(this) {
            areaTypePills?.map { it.binding.invalidateAll() }
        }
        viewModel.tenureType.observe(this) {
            tenureTypePills?.map { it.binding.invalidateAll() }
        }
        viewModel.rentalType.observe(this) {
            rentalTypePills?.map { it.binding.invalidateAll() }
        }
        viewModel.projectType.observe(this) {
            projectTypePills?.map { it.binding.invalidateAll() }
        }
        viewModel.isShowRadiusSeekBar.observeNotNull(this) {
            if (it) {
                fixSearchRadiusSeekBarLabelPosition()
            }
        }
        viewModel.hasListings.observeNotNull(this) {
            maybeDismissWatchlistTypeError()
        }
        viewModel.hasTransactions.observeNotNull(this) {
            maybeDismissWatchlistTypeError()
        }
        viewModel.propertyPurpose.observeNotNull(this) { purpose ->
            switch_property_purpose.isChecked = purpose == ListingEnum.PropertyPurpose.COMMERCIAL
            resetPropertyTypes(purpose)
        }
    }

    private fun resetPropertyTypes(purpose: ListingEnum.PropertyPurpose?) {
        when (purpose) {
            ListingEnum.PropertyPurpose.RESIDENTIAL -> resetResidentialPropertyTypes()
            ListingEnum.PropertyPurpose.COMMERCIAL -> resetCommercialPropertyTypes()
        }
    }

    private fun resetResidentialPropertyTypes() {
        val propertyType = viewModel.propertyType.value
        val subTypes = viewModel.propertySubTypes.value ?: emptyList()
        val hasResidential = subTypes.any { PropertyTypeUtil.isResidential(it.type) }
        if (propertyType?.isResidential() != true && !hasResidential) {
            viewModel.propertyType.postValue(ListingEnum.PropertyMainType.RESIDENTIAL)
        }
    }

    private fun resetCommercialPropertyTypes() {
        val subTypes = viewModel.propertySubTypes.value ?: emptyList()
        val hasCommercial = subTypes.any { PropertyTypeUtil.isCommercial(it.type) }
        if (!hasCommercial) {
            viewModel.propertySubTypes.postValue(subTypes + listOf(ListingEnum.PropertySubType.ALL_COMMERCIAL))
        }
    }

    private fun maybeDismissWatchlistTypeError() {
        if (viewModel.isShowWatchlistTypeError.value == true) {
            viewModel.isShowWatchlistTypeError.postValue(false)
        }
    }

    private fun fixSearchRadiusSeekBarLabelPosition() {
        sb_search_radius.post {
            sb_search_radius.updateLabelPosition()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    REQUEST_CODE_SEARCH_MRT -> {
                        val ids = data?.getStringExtra(MrtSearchActivity.EXTRA_MRT_STATION_IDS)
                        viewModel.mrts.postValue(ids)
                        if ((ids?.getIntListCount() ?: 0) > 0) {
                            et_criteria_name.setTruncatedText(data?.getStringExtra(MrtSearchActivity.EXTRA_MRT_STATION_NAMES))
                        }
                    }
                    REQUEST_CODE_SEARCH_DISTRICT -> {
                        val ids =
                            data?.getStringExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_IDS)
                        viewModel.districts.postValue(ids)
                        if ((ids?.getIntListCount() ?: 0) > 0) {
                            et_criteria_name.setTruncatedText(
                                data?.getStringExtra(
                                    DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_NAMES
                                )
                            )
                        }
                    }
                    REQUEST_CODE_SEARCH_AREA -> {
                        val ids =
                            data?.getStringExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_IDS)
                        viewModel.hdbTowns.postValue(ids)
                        if ((ids?.getIntListCount() ?: 0) > 0) {
                            et_criteria_name.setTruncatedText(
                                data?.getStringExtra(
                                    HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_NAMES
                                )
                            )
                        }
                    }
                    REQUEST_CODE_SEARCH_SCHOOL -> {
                        val ids =
                            data?.getStringExtra(SchoolSearchActivity.EXTRA_SELECTED_SCHOOL_IDS)
                        viewModel.schools.postValue(ids)
                        if ((ids?.getIntListCount() ?: 0) > 0) {
                            et_criteria_name.setTruncatedText(
                                data?.getStringExtra(
                                    SchoolSearchActivity.EXTRA_SELECTED_SCHOOL_NAMES
                                )
                            )
                        }
                    }
                    REQUEST_CODE_SEARCH_LOCATION -> {
                        val locationEntryPO =
                            data?.getSerializableExtra(LocationSearchActivity.EXTRA_RESULT_LOCATION_ENTRY) as LocationEntryPO?
                        viewModel.location.postValue(locationEntryPO?.displayText)
                        et_criteria_name.setTruncatedText(locationEntryPO?.displayText)
                    }
                    else -> {
                        super.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    // Set text truncated to max 50 chars
    private fun AppCompatEditText.setTruncatedText(inputText: String?) = run {
        val truncated = if (inputText != null && inputText.length >= 50) {
            "${inputText.substring(0, 50)}..."
        } else {
            inputText
        }
        setText(truncated)
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupSearchRadius()
        setupPriceRange()
        setupSizeRange()
        setupPsfRange()
        setupWatchlistTypeCheckBoxes()
        radio_group_ownership_type.onSelectOwnership = { viewModel.ownershipType.postValue(it) }
        selector_residential_main.onSelectPropertyMainType = {
            viewModel.propertyType.postValue(it)
        }

        selector_bedroom.onBedroomsSelect = {
            viewModel.bedroomCounts.postValue(it)
        }

        selector_bathroom.onBathroomsSelect = {
            viewModel.bathroomCounts.postValue(it)
        }

        btn_show_more.setOnClickQuickDelayListener { viewModel.isShowMore.postValue(true) }
        btn_save.setOnClickListener {
            viewModel.performAdd()
        }
        et_criteria_name.setOnTextChangedListener {
            viewModel.name.postValue(it)
            viewModel.isShowNameError.postValue(it.isEmpty())
        }
        btn_search.setOnClickListener {
            LocationSearchActivity.launchForResult(
                this,
                requestCode = REQUEST_CODE_SEARCH_LOCATION,
                includeNewProject = true
            )
        }
        switch_property_purpose.setOnClickListener {
            viewModel.propertyPurpose.postValue(
                when (switch_property_purpose.isChecked) {
                    true -> ListingEnum.PropertyPurpose.COMMERCIAL
                    false -> ListingEnum.PropertyPurpose.RESIDENTIAL
                }
            )
        }
        setupLookUpButtons()
        populateAreaTypes()
        populateTenureTypes()
        populateRentalTypes()
        populateProjectTypes()
        setCommercialPropertySubTypeOnClickListeners()
    }

    // TODO Try generalise on sub property type click
    private fun setCommercialPropertySubTypeOnClickListeners() {
        layout_commercial_sub_property_types.btn_property_sub_type_all_commercial.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.ALL_COMMERCIAL)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_retail.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.RETAIL)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_office.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.OFFICE)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_factory.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.FACTORY)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_warehouse.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.WAREHOUSE)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_land.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.LAND)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_hdb_shop_house.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.HDB_SHOP_HOUSE)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_shop_house.button.setOnClickListener {
            viewModel.toggleCommercialPropertySubType(ListingEnum.PropertySubType.SHOP_HOUSE)
        }
    }

    private fun setupLookUpButtons() {
        btn_search_by_mrt.setOnClickListener {
            MrtSearchActivity.launchForResult(
                this,
                viewModel.mrts.value,
                requestCode = REQUEST_CODE_SEARCH_MRT,
                returnOption = LookupBaseActivity.ReturnOption.SAVE
            )
        }
        btn_search_by_districts.setOnClickListener {
            DistrictSearchActivity.launchForResult(
                this,
                viewModel.districts.value,
                requestCode = REQUEST_CODE_SEARCH_DISTRICT,
                returnOption = LookupBaseActivity.ReturnOption.SAVE
            )
        }
        btn_search_by_areas.setOnClickListener {
            HdbTownSearchActivity.launchForResult(
                this,
                viewModel.hdbTowns.value,
                requestCode = REQUEST_CODE_SEARCH_AREA,
                returnOption = LookupBaseActivity.ReturnOption.SAVE
            )
        }
        btn_search_by_schools.setOnClickListener {
            SchoolSearchActivity.launchForResult(
                this, viewModel.schools.value,
                requestCode = REQUEST_CODE_SEARCH_SCHOOL,
                returnOption = LookupBaseActivity.ReturnOption.SAVE
            )
        }
    }

    private fun setupSearchRadius() {
        val labels = projectRadii.map { it.getLabel(this).toUpperCaseDefault() }
        sb_search_radius.setup(labels, projectRadii) {
            viewModel.searchRadius.postValue(it.valueInMeter)
        }
    }

    fun setSeekBarRadius(radiusInMeter: Int?) {
        sb_search_radius.binding.seekBar.progress = radiusInMeter?.run {
            val progress = projectRadii.indexOfFirst { it.valueInMeter == radiusInMeter }
            progress
        } ?: run {
            0
        }
    }

    fun setDefaultRadius() {
        setSeekBarRadius(DEFAULT_RADIUS_IN_METER)
    }

    private fun setupWatchlistTypeCheckBoxes() {
        cb_transactions.setOnCheckedChangeListener { _, isChecked ->
            viewModel.hasTransactions.postValue(isChecked)
        }
        cb_listings.setOnCheckedChangeListener { _, isChecked ->
            viewModel.hasListings.postValue(isChecked)
        }
    }

    private fun setupPriceRange() {
        layout_price_range.et_min.setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "\$${formattedNumberString}" }
                ?: getString(R.string.hint_text_box_price_min)
        }, onNumberChangeListener = {
            viewModel.minPrice.postValue(it)
        })

        layout_price_range.et_max.setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "\$${formattedNumberString}" }
                ?: getString(R.string.hint_text_box_price_max)
        }, onNumberChangeListener = {
            viewModel.maxPrice.postValue(it)
        })
    }

    private fun setupSizeRange() {
        layout_size.et_min.setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "$formattedNumberString sqft" }
                ?: getString(R.string.hint_text_box_area_min)
        }, onNumberChangeListener = {
            viewModel.minSize.postValue(it)
        })

        layout_size.et_max.setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "$formattedNumberString sqft" }
                ?: getString(R.string.hint_text_box_area_max)
        }, onNumberChangeListener = {
            viewModel.maxSize.postValue(it)
        })
    }

    private fun setupPsfRange() {
        layout_price_psf.et_min.setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "\$${formattedNumberString}" }
                ?: getString(R.string.hint_text_box_psf_min)
        }, onNumberChangeListener = {
            viewModel.minPsf.postValue(it)
        })

        layout_price_psf.et_max.setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "\$${formattedNumberString}" }
                ?: getString(R.string.hint_text_box_psf_max)
        }, onNumberChangeListener = {
            viewModel.maxPsf.postValue(it)
        })
    }

    private fun populatePropertySubTypes(propertyMainType: ListingEnum.PropertyMainType) {
        layout_property_sub_types.removeAllViews()
        subTypePills = when (propertyMainType) {
            ListingEnum.PropertyMainType.RESIDENTIAL, ListingEnum.PropertyMainType.COMMERCIAL -> emptyList()
            else -> propertyMainType.propertySubTypes.map {
                addSubTypePill(it)
            }
        }
    }

    private fun addSubTypePill(propertySubType: ListingEnum.PropertySubType): WatchlistPropertySubTypePill {
        val pill = WatchlistPropertySubTypePill(this)
        pill.binding.propertySubType = propertySubType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.togglePropertySubType(propertySubType)
        }
        layout_property_sub_types.addView(pill)
        return pill
    }

    private fun populateAreaTypes() {
        areaTypePills = WatchlistEnum.AreaType.values().map { addAreaTypePill(it) }
    }

    private fun addAreaTypePill(areaType: WatchlistEnum.AreaType): WatchlistAreaTypePill {
        val pill = WatchlistAreaTypePill(this)
        pill.binding.areaType = areaType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.areaType.postValue(areaType)
        }
        layout_area_type.addView(pill)
        return pill
    }

    private fun populateTenureTypes() {
        tenureTypePills = TransactionEnum.TenureType.values().map { addTenureTypePill(it) }
    }

    private fun addTenureTypePill(tenureType: TransactionEnum.TenureType): WatchlistTenureTypePill {
        val pill = WatchlistTenureTypePill(this)
        pill.binding.tenureType = tenureType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.tenureType.postValue(tenureType)
        }
        layout_tenure_type.addView(pill)
        return pill
    }

    private fun populateProjectTypes() {
        projectTypePills = WatchlistEnum.ProjectType.values().map { addProjectTypePill(it) }
    }

    private fun addProjectTypePill(projectType: WatchlistEnum.ProjectType): WatchlistProjectTypePill {
        val pill = WatchlistProjectTypePill(this)
        pill.binding.projectType = projectType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.projectType.postValue(projectType)
        }
        layout_project_type.addView(pill)
        return pill
    }

    private fun populateRentalTypes() {
        rentalTypePills = WatchlistEnum.RentalType.values().map { addRentalTypePill(it) }
    }

    private fun addRentalTypePill(rentalType: WatchlistEnum.RentalType): WatchlistRentalTypePill {
        val pill = WatchlistRentalTypePill(this)
        pill.binding.rentalType = rentalType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.rentalType.postValue(rentalType)
        }
        layout_rental_type.addView(pill)
        return pill
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_watchlist_criteria_form
    }

    override fun getViewModelClass(): Class<WatchlistCriteriaFormViewModel> {
        return WatchlistCriteriaFormViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
        binding.formType = getFormType()
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    companion object {
        private const val REQUEST_CODE_SEARCH_LOCATION = 5
        private const val REQUEST_CODE_SEARCH_MRT = 1
        private const val REQUEST_CODE_SEARCH_DISTRICT = 2
        private const val REQUEST_CODE_SEARCH_AREA = 3
        private const val REQUEST_CODE_SEARCH_SCHOOL = 4

        private const val DEFAULT_RADIUS_IN_METER = 1000

        private const val MIN_RADIUS_IN_METER = 100
        private const val MAX_RADIUS_IN_METER = 4000
        private const val RADIUS_STEP = 100
    }

    abstract fun getFormType(): CriteriaFormType
}
