package sg.searchhouse.agentconnect.view.activity.listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_filter_listing.*
import kotlinx.android.synthetic.main.button_filter_property_type_residential.view.*
import kotlinx.android.synthetic.main.layout_bathrooms.view.*
import kotlinx.android.synthetic.main.layout_bedrooms.view.*
import kotlinx.android.synthetic.main.layout_filter_listing_commercial_property_sub_types.view.*
import kotlinx.android.synthetic.main.layout_filter_listing_residential_property_main_types.view.*
import kotlinx.android.synthetic.main.layout_range_number_boxes.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityFilterListingBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.BlockStatus
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.listing.*
import sg.searchhouse.agentconnect.viewmodel.activity.listing.FilterListingViewModel

class FilterListingActivity :
    ViewModelActivity<FilterListingViewModel, ActivityFilterListingBinding>() {
    private var residentialSubTypePillListings: List<FilterListingPropertySubTypePill>? = null
    private var bathroomButtons: List<FilterBathroomButton>? = null

    private var floorPills: List<FilterFloorPill>? = null
    private var rentalTypePills: List<FilterRentalTypePill>? = null
    private var tenurePills: List<FilterTenurePill>? = null
    private var furnishPills: List<FilterFurnishPill>? = null
    private var listingDatePills: List<FilterListingDatePill>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        populateExtrasToParams()
        setupViews()
        observeLiveData()
        initParams()
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setBedroomButtonClickListeners()
        setBathroomClickListeners()
        populateRentalTypes()
        populateFloors()
        populateTenures()
        populateFurnishes()
        populateListingDates()
        setOnClickListeners()
        setupRanges()
    }

    private fun resetTextBoxesFromPreviousSession() {
        layout_price_range.et_min.setNumber(viewModel.previousMinPriceRange)
        layout_price_range.et_max.setNumber(viewModel.previousMaxPriceRange)

        layout_price_psf.et_min.setNumber(viewModel.previousMinPsf)
        layout_price_psf.et_max.setNumber(viewModel.previousMaxPsf)

        layout_floor_area.et_min.setNumber(viewModel.previousMinBuiltSize)
        layout_floor_area.et_max.setNumber(viewModel.previousMaxBuiltSize)

        layout_land_size.et_min.setNumber(viewModel.previousMinLandSize)
        layout_land_size.et_max.setNumber(viewModel.previousMaxLandSize)

        layout_construction_year.et_min.setNumber(viewModel.defaultMinConstructionYear)
        layout_construction_year.et_max.setNumber(viewModel.defaultMaxConstructionYear)
    }

    private fun setupActionBar() {
        supportActionBar?.let {
            it.setHomeAsUpIndicator(R.drawable.ic_cancel)
            it.title = ""
        }
    }

    private fun setupRanges() {
        val priceRangeTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                "\$$numberString"
            } else {
                getString(R.string.label_any)
            }
        }

        layout_price_range.et_min.setup(
            PRICE_MAX_VALUE,
            priceRangeTransformer
        ) { maybeRequestResultCount() }
        layout_price_range.et_max.setup(
            PRICE_MAX_VALUE,
            priceRangeTransformer
        ) { maybeRequestResultCount() }

        val pricePsfTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                "\$$numberString"
            } else {
                getString(R.string.label_any)
            }
        }

        layout_price_psf.et_min.setup(
            PRICE_PSF_MAX_VALUE,
            pricePsfTransformer
        ) { maybeRequestResultCount() }
        layout_price_psf.et_max.setup(
            PRICE_PSF_MAX_VALUE,
            pricePsfTransformer
        ) { maybeRequestResultCount() }

        val floorAreaTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                if (viewModel.isHdbPropertyMainType()) {
                    val squareFeet = (number * AppConstant.ONE_SQUARE_METER_TO_SQUARE_FEET).toInt()
                    "$numberString sqm ($squareFeet sqft)"
                } else {
                    val squareMeter = (number * AppConstant.ONE_SQUARE_FEET_TO_SQUARE_METER).toInt()
                    "$numberString sqft ($squareMeter sqm)"
                }
            } else {
                getString(R.string.label_any)
            }
        }

        val landSizeTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                "$numberString sqft"
            } else {
                getString(R.string.label_any)
            }
        }

        layout_floor_area.et_min.setup(
            FLOOR_AREA_MAX_VALUE,
            floorAreaTransformer
        ) { maybeRequestResultCount() }
        layout_floor_area.et_max.setup(
            FLOOR_AREA_MAX_VALUE,
            floorAreaTransformer
        ) { maybeRequestResultCount() }

        layout_land_size.et_min.setup(
            LAND_SIZE_MAX_VALUE,
            landSizeTransformer
        ) { maybeRequestResultCount() }
        layout_land_size.et_max.setup(
            LAND_SIZE_MAX_VALUE,
            landSizeTransformer
        ) { maybeRequestResultCount() }

        val constructionYearTransformer = { number: Int?, _: String? ->
            number?.toString() ?: getString(R.string.label_any)
        }

        val constructionYearMaxValue = DateTimeUtil.getCurrentYear()

        layout_construction_year.et_min.setup(
            constructionYearMaxValue,
            constructionYearTransformer
        ) { maybeRequestResultCount() }
        layout_construction_year.et_max.setup(
            constructionYearMaxValue,
            constructionYearTransformer
        ) { maybeRequestResultCount() }
    }

    private fun setOnClickListeners() {
        // TODO: Refactor with 2 way binding later
        pill_virtual_tours.setOnClickListener { viewModel.hasVirtualTours.postValue(viewModel.hasVirtualTours.value != true) }
        pill_drone_views.setOnClickListener { viewModel.hasDroneViews.postValue(viewModel.hasDroneViews.value != true) }
        pill_owner_certified.setOnClickListener { viewModel.ownerCertification.postValue(viewModel.ownerCertification.value != true) }
        pill_exclusive.setOnClickListener { viewModel.exclusiveListing.postValue(viewModel.exclusiveListing.value != true) }
        pill_x_listing_price.setOnClickListener { viewModel.xListingPrice.postValue(viewModel.xListingPrice.value != true) }

        btn_submit.setOnClickListener { submitFilter() }
        setResidentialPropertyMainTypeOnClickListeners()
        setCommercialPropertySubTypeOnClickListeners()
    }

    // TODO: Try generalise on main property type click
    private fun setResidentialPropertyMainTypeOnClickListeners() {
        layout_residential_main_property_types.btn_property_main_type_residential.button.setOnClickListener {
            viewModel.updatePropertyMainTypeTotalBlockStatus.postValue(BlockStatus.BLOCK)
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.RESIDENTIAL)
        }
        layout_residential_main_property_types.btn_property_main_type_hdb.button.setOnClickListener {
            viewModel.updatePropertyMainTypeTotalBlockStatus.postValue(BlockStatus.BLOCK)
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.HDB)
        }
        layout_residential_main_property_types.btn_property_main_type_condo.button.setOnClickListener {
            viewModel.updatePropertyMainTypeTotalBlockStatus.postValue(BlockStatus.BLOCK)
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.CONDO)
        }
        layout_residential_main_property_types.btn_property_main_type_landed.button.setOnClickListener {
            viewModel.updatePropertyMainTypeTotalBlockStatus.postValue(BlockStatus.BLOCK)
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.LANDED)
        }
    }

    // TODO: Try generalise on sub property type click
    private fun setCommercialPropertySubTypeOnClickListeners() {
        layout_commercial_sub_property_types.btn_property_sub_type_all_commercial.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.ALL_COMMERCIAL)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_retail.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.RETAIL)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_office.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.OFFICE)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_factory.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.FACTORY)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_warehouse.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.WAREHOUSE)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_land.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.LAND)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_hdb_shop_house.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.HDB_SHOP_HOUSE)
        }
        layout_commercial_sub_property_types.btn_property_sub_type_shop_house.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.SHOP_HOUSE)
        }
    }

    private fun submitFilter() {
        val intent = Intent()

        val propertyMainType = viewModel.propertyMainType.value
        intent.putExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)

        val propertySubTypes = viewModel.propertySubTypes.value?.map { it.type }?.joinToString(",")
        intent.putExtra(EXTRA_KEY_PROPERTY_SUB_TYPES, propertySubTypes)

        intent.putExtra(
            EXTRA_KEY_BEDROOM_COUNTS,
            viewModel.bedroomCounts.value?.map { it.value }?.joinToString(",")
        )
        intent.putExtra(
            EXTRA_KEY_BATHROOM_COUNTS,
            viewModel.bathroomCounts.value?.map { it.value }?.joinToString(",")
        )

        intent.putExtra(EXTRA_KEY_RENTAL_TYPE, viewModel.getRentalTypeValue())

        val floors = viewModel.floors.value?.joinToString(",") { it.value }
        intent.putExtra(EXTRA_KEY_FLOORS, floors)

        val tenures = viewModel.tenures.value?.map { it.value }?.joinToString(",")
        intent.putExtra(EXTRA_KEY_TENURES, tenures)

        val furnishes = viewModel.furnishes.value?.joinToString(",") { it.value }
        intent.putExtra(EXTRA_KEY_FURNISHES, furnishes)

        intent.putExtra(EXTRA_KEY_HAS_VIRTUAL_TOURS, viewModel.hasVirtualTours.value)
        intent.putExtra(EXTRA_KEY_HAS_DRONE_VIEWS, viewModel.hasDroneViews.value)
        intent.putExtra(EXTRA_KEY_OWNER_CERTIFICATION, viewModel.ownerCertification.value)
        intent.putExtra(EXTRA_KEY_EXCLUSIVE_LISTING, viewModel.exclusiveListing.value)
        intent.putExtra(EXTRA_KEY_X_LISTING_PRICE, viewModel.xListingPrice.value)

        intent.putExtra(EXTRA_KEY_IS_MIN_DATE_FIRST_POSTED, viewModel.minDateFirstPosted.value)

        // Populate range values

        val priceRangeMin = layout_price_range.et_min.value
        val priceRangeMax = layout_price_range.et_max.value

        val pricePsfMin = layout_price_psf.et_min.value
        val pricePsfMax = layout_price_psf.et_max.value

        val floorAreaMin = layout_floor_area.et_min.value
        val floorAreaMax = layout_floor_area.et_max.value

        val landSizeMin = layout_land_size.et_min.value
        val landSizeMax = layout_land_size.et_max.value

        val constructionYearMin = layout_construction_year.et_min.value
        val constructionYearMax = layout_construction_year.et_max.value

        intent.putExtra(EXTRA_KEY_MIN_PRICE_RANGE, priceRangeMin)
        intent.putExtra(EXTRA_KEY_MAX_PRICE_RANGE, priceRangeMax)
        intent.putExtra(EXTRA_KEY_MIN_PRICE_PSF, pricePsfMin)
        intent.putExtra(EXTRA_KEY_MAX_PRICE_PSF, pricePsfMax)
        intent.putExtra(EXTRA_KEY_MIN_FLOOR_AREA, floorAreaMin)
        intent.putExtra(EXTRA_KEY_MAX_FLOOR_AREA, floorAreaMax)
        intent.putExtra(EXTRA_KEY_MIN_LAND_SIZE, landSizeMin)
        intent.putExtra(EXTRA_KEY_MAX_LAND_SIZE, landSizeMax)
        intent.putExtra(EXTRA_KEY_MIN_CONSTRUCTION_YEAR, constructionYearMin)
        intent.putExtra(EXTRA_KEY_MAX_CONSTRUCTION_YEAR, constructionYearMax)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this) {
            viewModel.updateIsRequestInProgressList()
        }

        viewModel.propertyPurpose.observe(this) { maybeRequestResultCount() }

        viewModel.propertyMainType.observe(this) { populatePropertySubTypes(it) }

        viewModel.propertySubTypes.observe(this) { propertySubTypes ->
            if (viewModel.updatePropertyMainTypeTotalBlockStatus.value == BlockStatus.BLOCK) {
                viewModel.updatePropertyMainTypeTotalBlockStatus.postValue(BlockStatus.READY)
            } else {
                maybeRequestResultCount()
            }
            if (propertySubTypes != null) {
                when (viewModel.propertyPurpose.value) {
                    ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                        residentialSubTypePillListings?.map { it.binding.invalidateAll() }
                    }
                    else -> {
                    }
                }
            }
        }

        // Request total on update main property type
        // Refer FilterListingViewModel#isUpdatePropertyMainTypeTotal
        viewModel.updatePropertyMainTypeTotalBlockStatus.observe(this) {
            if (it == BlockStatus.READY) {
                maybeRequestResultCount()
                viewModel.updatePropertyMainTypeTotalBlockStatus.postValue(BlockStatus.IDLE)
            }
        }

        viewModel.bathroomCounts.observe(this) { bathroomCounts ->
            maybeRequestResultCount()
            if (bathroomCounts != null) {
                bathroomButtons?.map { it.binding.invalidateAll() }
            }
        }

        viewModel.bedroomCounts.observe(this) {
            maybeRequestResultCount()
        }

        viewModel.rentalType.observe(this) { rentalType ->
            maybeRequestResultCount()
            if (rentalType != null) {
                rentalTypePills?.map { it.binding.invalidateAll() }
            }
        }

        viewModel.floors.observe(this) { floors ->
            maybeRequestResultCount()
            if (floors != null) {
                floorPills?.map { it.binding.invalidateAll() }
            }
        }

        viewModel.tenures.observe(this) { tenures ->
            maybeRequestResultCount()
            if (tenures != null) {
                tenurePills?.map { it.binding.invalidateAll() }
            }
        }

        viewModel.furnishes.observe(this) { furnishes ->
            maybeRequestResultCount()
            if (furnishes != null) {
                furnishPills?.map { it.binding.invalidateAll() }
            }
        }

        viewModel.hasVirtualTours.observe(this) {
            maybeRequestResultCount()
        }

        viewModel.hasDroneViews.observe(this) {
            maybeRequestResultCount()
        }

        viewModel.ownerCertification.observeNotNull(this) {
            maybeRequestResultCount()
        }

        viewModel.exclusiveListing.observeNotNull(this) {
            maybeRequestResultCount()
        }

        viewModel.xListingPrice.observe(this) {
            maybeRequestResultCount()
        }

        viewModel.minDateFirstPosted.observe(this) { listingDate ->
            maybeRequestResultCount(isLastResetParam = true)
            if (listingDate != null) {
                listingDatePills?.map { it.binding.invalidateAll() }
            }
        }

        viewModel.resetBlockStatus.observe(this) {
            if (it == BlockStatus.READY) {
                requestResultCount()
                viewModel.resetBlockStatus.postValue(BlockStatus.IDLE)
            }
        }
    }

    private fun maybeRequestResultCount(isLastResetParam: Boolean = false) {
        if (viewModel.resetBlockStatus.value == BlockStatus.BLOCK) {
            if (isLastResetParam) {
                viewModel.resetBlockStatus.postValue(BlockStatus.READY)
            }
        } else {
            requestResultCount()
        }
    }

    private fun requestResultCount() {
        viewModel.requestResultCount(
            layout_price_range.et_min.value,
            layout_price_range.et_max.value,
            layout_price_psf.et_min.value,
            layout_price_psf.et_max.value,
            layout_floor_area.et_min.value,
            layout_floor_area.et_max.value,
            layout_land_size.et_min.value,
            layout_land_size.et_max.value,
            layout_construction_year.et_min.value,
            layout_construction_year.et_max.value
        )
    }

    private fun populateExtrasToParams() {
        // Part 1: Below are inputs only, no change of value in this filter
        val propertyPurpose =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE) as ListingEnum.PropertyPurpose?
                ?: ListingEnum.PropertyPurpose.RESIDENTIAL
        viewModel.propertyPurpose.postValue(propertyPurpose)

        val searchText = intent.getStringExtra(EXTRA_KEY_INPUT_SEARCH_TEXT)
        viewModel.searchTextInputOnly.postValue(searchText)

        val ownershipType =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE) as ListingEnum.OwnershipType?
        viewModel.ownershipTypeInputOnly.postValue(ownershipType)

        val isTransacted = intent.getBooleanExtra(EXTRA_KEY_INPUT_IS_TRANSACTED, false)
        viewModel.isTransactedInputOnly.postValue(isTransacted)

        val projectLaunchStatus =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_PROJECT_LAUNCH_STATUS) as ListingEnum.ProjectLaunchStatus?
        viewModel.projectLaunchStatusInputOnly.postValue(projectLaunchStatus)

        val propertyAge =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_PROPERTY_AGE) as ListingEnum.PropertyAge?
        viewModel.propertyAgeInputOnly.postValue(propertyAge)

        val amenitiesIds = intent.getStringExtra(EXTRA_KEY_INPUT_AMENITIES_IDS)
        viewModel.amenitiesIdsInputOnly.postValue(amenitiesIds)

        val districtIds = intent.getStringExtra(EXTRA_KEY_INPUT_DISTRICT_IDS)
        viewModel.districtIdsInputOnly.postValue(districtIds)

        val hdbTownIds = intent.getStringExtra(EXTRA_KEY_INPUT_HDB_TOWN_IDS)
        viewModel.hdbTownIdsInputOnly.postValue(hdbTownIds)

        val isIncludeNearBy = intent.getBooleanExtra(EXTRA_KEY_INPUT_IS_INCLUDE_NEARBY, true)
        viewModel.isIncludeNearbyInputOnly.postValue(isIncludeNearBy)

        val isNearbyApplicable = intent.getBooleanExtra(EXTRA_KEY_INPUT_IS_NEARBY_APPLICABLE, false)
        viewModel.isNearbyApplicable.postValue(isNearbyApplicable)

        // Part 2: Below have both input and output, so assign default value to normal variable, and assign to live data only when reset params
        val propertyMainType =
            intent.getSerializableExtra(EXTRA_KEY_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                ?: when (propertyPurpose) {
                    ListingEnum.PropertyPurpose.RESIDENTIAL -> ListingEnum.PropertyMainType.RESIDENTIAL
                    ListingEnum.PropertyPurpose.COMMERCIAL -> null
                }

        viewModel.defaultPropertyMainType = propertyMainType

        viewModel.defaultPropertySubTypes =
            getListFromStringExtra(EXTRA_KEY_INPUT_PROPERTY_SUB_TYPES) { type ->
                ListingEnum.PropertySubType.values().find { subType ->
                    subType.type == NumberUtil.toInt(type)
                }
            } ?: when (propertyPurpose) {
                ListingEnum.PropertyPurpose.RESIDENTIAL -> null
                ListingEnum.PropertyPurpose.COMMERCIAL -> listOf(
                    ListingEnum.PropertySubType.ALL_COMMERCIAL
                )
            }

        viewModel.defaultMinConstructionYear =
            getIntFromExtra(EXTRA_KEY_INPUT_MIN_CONSTRUCTION_YEAR)
        viewModel.defaultMaxConstructionYear =
            getIntFromExtra(EXTRA_KEY_INPUT_MAX_CONSTRUCTION_YEAR)

        viewModel.defaultTenures = getListFromStringExtra(EXTRA_KEY_INPUT_TENURES) {
            ListingEnum.Tenure.values().find { tenure -> tenure.value == NumberUtil.toInt(it) }
        }

        viewModel.defaultBedroomCounts = getListFromStringExtra(EXTRA_KEY_INPUT_BEDROOM_COUNTS) {
            ListingEnum.BedroomCount.values().find { bedroomCount ->
                bedroomCount.value == NumberUtil.toInt(it)
            }
        }

        // Part 3: Below are passed back, i.e. params set in previous session of FilterListingActivity
        viewModel.previousBathroomCounts = getListFromStringExtra(EXTRA_KEY_INPUT_BATHROOM_COUNTS) {
            ListingEnum.BathroomCount.values().find { bathroomCount ->
                bathroomCount.value == NumberUtil.toInt(it)
            }
        }

        viewModel.previousRentalType =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_RENTAL_TYPE) as ListingEnum.RentalType?

        viewModel.previousFloors = getListFromStringExtra(EXTRA_KEY_INPUT_FLOORS) {
            ListingEnum.Floor.values().find { floor ->
                TextUtils.equals(floor.value, it)
            }
        }

        viewModel.previousFurnishes = getListFromStringExtra(EXTRA_KEY_INPUT_FURNISHES) {
            ListingEnum.Furnish.values().find { furnish ->
                TextUtils.equals(furnish.value, it)
            }
        }

        viewModel.previousMinPriceRange = getIntFromExtra(EXTRA_KEY_INPUT_MIN_PRICE_RANGE)
        viewModel.previousMaxPriceRange = getIntFromExtra(EXTRA_KEY_INPUT_MAX_PRICE_RANGE)
        viewModel.previousMinPsf = getIntFromExtra(EXTRA_KEY_INPUT_MIN_PSF)
        viewModel.previousMaxPsf = getIntFromExtra(EXTRA_KEY_INPUT_MAX_PSF)
        viewModel.previousMinBuiltSize = getIntFromExtra(EXTRA_KEY_INPUT_MIN_BUILT_SIZE)
        viewModel.previousMaxBuiltSize = getIntFromExtra(EXTRA_KEY_INPUT_MAX_BUILT_SIZE)
        viewModel.previousMinLandSize = getIntFromExtra(EXTRA_KEY_INPUT_MIN_LAND_SIZE)
        viewModel.previousMaxLandSize = getIntFromExtra(EXTRA_KEY_INPUT_MAX_LAND_SIZE)

        viewModel.previousMinDateFirstPosted =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_MIN_DATE_FIRST_POSTED) as ListingEnum.MinDateFirstPosted?

        viewModel.previousHasVirtualTours =
            intent.extras?.get(EXTRA_KEY_INPUT_HAS_VIRTUAL_TOURS) as Boolean?
        viewModel.previousHasDroneViews =
            intent.extras?.get(EXTRA_KEY_INPUT_HAS_DRONE_VIEWS) as Boolean?
        viewModel.previousOwnerCertification =
            intent.extras?.get(EXTRA_KEY_INPUT_OWNER_CERTIFICATION) as Boolean?
        viewModel.previousExclusiveListing =
            intent.extras?.get(EXTRA_KEY_INPUT_EXCLUSIVE_LISTING) as Boolean?
        viewModel.previousXListingPrice =
            intent.extras?.get(EXTRA_KEY_INPUT_X_LISTING_PRICE) as Boolean?
    }

    private fun getIntFromExtra(extraKey: String): Int? {
        return when (val value = intent.getIntExtra(extraKey, -1)) {
            -1 -> null
            else -> value
        }
    }

    private fun <T : Any> getListFromStringExtra(
        extraKey: String,
        selection: (String) -> T?
    ): List<T>? {
        return intent.getStringExtra(extraKey)?.split(",")?.mapNotNull {
            selection.invoke(it)
        }
    }

    private fun populatePropertySubTypes(propertyMainType: ListingEnum.PropertyMainType?) {
        layout_property_sub_types.removeAllViews()
        propertyMainType?.let {
            when {
                propertyMainType == ListingEnum.PropertyMainType.RESIDENTIAL -> {
                    // For case of ALL_RESIDENTIAL
                    // Not populate sub types, but submit all of them
                    viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
                }
                viewModel.propertyPurpose.value == ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                    residentialSubTypePillListings =
                        propertyMainType.propertySubTypes.map { addPropertySubTypeButton(it) }

                    val defaultPropertySubTypes =
                        viewModel.defaultPropertySubTypes ?: emptyList()

                    // When it is in reset mode
                    // and this main type contains the default input sub types
                    // Choose the default one, otherwise choose all
                    val isDuringReset = viewModel.resetBlockStatus.value == BlockStatus.BLOCK

                    val hasDefaultSubTypes =
                        defaultPropertySubTypes.isNotEmpty() && propertyMainType.propertySubTypes.intersect(
                            defaultPropertySubTypes
                        ).isNotEmpty()

                    if (isDuringReset && hasDefaultSubTypes) {
                        viewModel.propertySubTypes.postValue(defaultPropertySubTypes)
                    } else {
                        // TODO Here
                        viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
                    }
                }
                else -> {
                    // Do nothing
                }
            }
        }
    }

    private fun addPropertySubTypeButton(propertySubType: ListingEnum.PropertySubType): FilterListingPropertySubTypePill {
        val pill = FilterListingPropertySubTypePill(this)
        pill.binding.propertySubType = propertySubType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.togglePropertySubType(propertySubType) }
        layout_property_sub_types.addView(pill)
        return pill
    }

    private fun setBedroomButtonClickListeners() {
        layout_bedrooms.btn_bedroom_count_any.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.ANY)
        }
        layout_bedrooms.btn_bedroom_count_one.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.ONE)
        }
        layout_bedrooms.btn_bedroom_count_two.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.TWO)
        }
        layout_bedrooms.btn_bedroom_count_three.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.THREE)
        }
        layout_bedrooms.btn_bedroom_count_four.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.FOUR)
        }
        layout_bedrooms.btn_bedroom_count_five.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.FIVE)
        }
        layout_bedrooms.btn_bedroom_count_six_and_above.setOnClickListener {
            selectBedroomCount(ListingEnum.BedroomCount.SIX_AND_ABOVE)
        }
    }

    private fun selectBedroomCount(bedroomCount: ListingEnum.BedroomCount) {
        val bedroomCounts = when {
            bedroomCount == ListingEnum.BedroomCount.ANY -> // Any
                listOf(ListingEnum.BedroomCount.ANY)
            viewModel.bedroomCounts.value?.contains(bedroomCount) == true -> {
                // Already have, un-select
                val existingBedRoomCounts =
                    viewModel.bedroomCounts.value!! // Confirm not null here
                existingBedRoomCounts.filter { it != bedroomCount }
            }
            else -> {
                // Select
                ((viewModel.bedroomCounts.value ?: emptyList()) + bedroomCount).filter {
                    it != ListingEnum.BedroomCount.ANY
                }
            }
        }
        viewModel.bedroomCounts.postValue(bedroomCounts)
    }

    private fun setBathroomClickListeners() {
        layout_bathrooms.btn_bathroom_count_any.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.ANY)
        }
        layout_bathrooms.btn_bathroom_count_one.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.ONE)
        }
        layout_bathrooms.btn_bathroom_count_two.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.TWO)
        }
        layout_bathrooms.btn_bathroom_count_three.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.THREE)
        }
        layout_bathrooms.btn_bathroom_count_four.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.FOUR)
        }
        layout_bathrooms.btn_bathroom_count_five.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.FIVE)
        }
        layout_bathrooms.btn_bathroom_count_six_and_above.setOnClickListener {
            selectBathroomCount(ListingEnum.BathroomCount.SIX_AND_ABOVE)
        }
    }

    private fun selectBathroomCount(bathroomCount: ListingEnum.BathroomCount) {
        val bathroomCounts = when {
            bathroomCount == ListingEnum.BathroomCount.ANY -> listOf(ListingEnum.BathroomCount.ANY)
            viewModel.bathroomCounts.value?.contains(bathroomCount) == true -> {
                // Already have, un-select
                val existingBathroomCounts =
                    viewModel.bathroomCounts.value!! // Confirm not null here
                existingBathroomCounts.filter { it != bathroomCount }
            }
            else -> ((viewModel.bathroomCounts.value ?: emptyList()) + bathroomCount).filter {
                it != ListingEnum.BathroomCount.ANY
            }
        }
        viewModel.bathroomCounts.postValue(bathroomCounts)
    }

    private fun populateRentalTypes() {
        rentalTypePills = ListingEnum.RentalType.values().map { addRentalTypePill(it) }
    }

    private fun addRentalTypePill(rentalType: ListingEnum.RentalType): FilterRentalTypePill {
        val pill = FilterRentalTypePill(this)
        pill.binding.rentalType = rentalType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.rentalType.postValue(rentalType) }
        layout_rental_types.addView(pill)
        return pill
    }

    private fun populateFloors() {
        floorPills = ListingEnum.Floor.values().map { addFloorPill(it) }
    }

    private fun addFloorPill(floor: ListingEnum.Floor): FilterFloorPill {
        val pill = FilterFloorPill(this)
        pill.binding.floor = floor
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.toggleFloor(floor) }
        layout_floor_levels.addView(pill)
        return pill
    }

    private fun populateTenures() {
        tenurePills = ListingEnum.Tenure.values().filter { !EXCLUDED_TENURES.contains(it) }
            .map { addTenurePill(it) }
    }

    private fun addTenurePill(tenure: ListingEnum.Tenure): FilterTenurePill {
        val pill = FilterTenurePill(this)
        pill.binding.tenure = tenure
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.toggleTenure(tenure) }
        layout_tenures.addView(pill)
        return pill
    }

    private fun populateFurnishes() {
        furnishPills = ListingEnum.Furnish.values().map { addFurnishPill(it) }
    }

    private fun addFurnishPill(furnish: ListingEnum.Furnish): FilterFurnishPill {
        val pill = FilterFurnishPill(this)
        pill.binding.furnish = furnish
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.toggleFurnish(furnish) }
        layout_furnishes.addView(pill)
        return pill
    }

    private fun populateListingDates() {
        listingDatePills = ListingEnum.MinDateFirstPosted.values().map { addListingDatePill(it) }
    }

    private fun addListingDatePill(listingDate: ListingEnum.MinDateFirstPosted): FilterListingDatePill {
        val pill = FilterListingDatePill(this)
        pill.binding.listingDate = listingDate
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.minDateFirstPosted.postValue(listingDate) }
        layout_listing_dates.addView(pill)
        return pill
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_listing, menu)
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
        viewModel.resetBlockStatus.value = BlockStatus.BLOCK
        resetTextBoxes()
        viewModel.resetParams()
    }

    // Difference from resetParams: Include params from previous session
    private fun initParams() {
        viewModel.resetBlockStatus.value = BlockStatus.BLOCK
        resetTextBoxesFromPreviousSession()
        viewModel.initParams()
    }

    private fun resetTextBoxes() {
        layout_price_range.et_min.clear()
        layout_price_range.et_max.clear()

        layout_price_psf.et_min.clear()
        layout_price_psf.et_max.clear()

        layout_floor_area.et_min.clear()
        layout_floor_area.et_max.clear()

        layout_land_size.et_min.clear()
        layout_land_size.et_max.clear()

        layout_construction_year.et_min.clear()
        layout_construction_year.et_max.clear()
    }

    companion object {
        private val EXCLUDED_TENURES = listOf(
            ListingEnum.Tenure.NOT_SPECIFIED,
            ListingEnum.Tenure.NINE_NINE_NINE_YEARS,
            ListingEnum.Tenure.ONE_HUNDRED_THREE_YEARS
        )

        const val EXTRA_KEY_INPUT_PROPERTY_PURPOSE = "EXTRA_KEY_INPUT_PROPERTY_PURPOSE"
        const val EXTRA_KEY_INPUT_PROPERTY_SUB_TYPES = "EXTRA_KEY_INPUT_PROPERTY_SUB_TYPES"

        const val EXTRA_KEY_INPUT_SEARCH_TEXT = "EXTRA_KEY_INPUT_SEARCH_TEXT"
        const val EXTRA_KEY_INPUT_OWNERSHIP_TYPE = "EXTRA_KEY_INPUT_OWNERSHIP_TYPE"
        const val EXTRA_KEY_INPUT_IS_TRANSACTED = "EXTRA_KEY_INPUT_IS_TRANSACTED"
        const val EXTRA_KEY_INPUT_PROJECT_LAUNCH_STATUS = "EXTRA_KEY_INPUT_PROJECT_LAUNCH_STATUS"
        const val EXTRA_KEY_INPUT_PROPERTY_AGE = "EXTRA_KEY_INPUT_PROPERTY_AGE"
        const val EXTRA_KEY_INPUT_AMENITIES_IDS = "EXTRA_KEY_INPUT_AMENITIES_IDS"
        const val EXTRA_KEY_INPUT_DISTRICT_IDS = "EXTRA_KEY_INPUT_DISTRICT_IDS"
        const val EXTRA_KEY_INPUT_HDB_TOWN_IDS = "EXTRA_KEY_INPUT_HDB_TOWN_IDS"

        const val EXTRA_KEY_PROPERTY_MAIN_TYPE = "EXTRA_KEY_PROPERTY_MAIN_TYPE"
        const val EXTRA_KEY_PROPERTY_SUB_TYPES = "EXTRA_KEY_PROPERTY_SUB_TYPES"
        const val EXTRA_KEY_BEDROOM_COUNTS = "EXTRA_KEY_BEDROOM_COUNTS"
        const val EXTRA_KEY_BATHROOM_COUNTS = "EXTRA_KEY_BATHROOM_COUNTS"

        const val EXTRA_KEY_RENTAL_TYPE = "EXTRA_KEY_RENTAL_TYPE"
        const val EXTRA_KEY_FLOORS = "EXTRA_KEY_FLOORS"
        const val EXTRA_KEY_TENURES = "EXTRA_KEY_TENURES"
        const val EXTRA_KEY_FURNISHES = "EXTRA_KEY_FURNISHES"

        const val EXTRA_KEY_X_LISTING_PRICE = "EXTRA_KEY_X_LISTING_PRICE"
        const val EXTRA_KEY_HAS_VIRTUAL_TOURS = "EXTRA_KEY_HAS_VIRTUAL_TOURS"
        const val EXTRA_KEY_HAS_DRONE_VIEWS = "EXTRA_KEY_HAS_DRONE_VIEWS"
        const val EXTRA_KEY_OWNER_CERTIFICATION = "EXTRA_KEY_OWNER_CERTIFICATION"
        const val EXTRA_KEY_EXCLUSIVE_LISTING = "EXTRA_KEY_EXCLUSIVE_LISTING"

        const val EXTRA_KEY_IS_MIN_DATE_FIRST_POSTED = "EXTRA_KEY_IS_MIN_DATE_FIRST_POSTED"

        const val EXTRA_KEY_MIN_PRICE_RANGE = "EXTRA_KEY_MIN_PRICE_RANGE"
        const val EXTRA_KEY_MAX_PRICE_RANGE = "EXTRA_KEY_MAX_PRICE_RANGE"

        const val EXTRA_KEY_MIN_PRICE_PSF = "EXTRA_KEY_MIN_PRICE_PSF"
        const val EXTRA_KEY_MAX_PRICE_PSF = "EXTRA_KEY_MAX_PRICE_PSF"

        const val EXTRA_KEY_MIN_FLOOR_AREA = "EXTRA_KEY_MIN_FLOOR_AREA"
        const val EXTRA_KEY_MAX_FLOOR_AREA = "EXTRA_KEY_MAX_FLOOR_AREA"

        const val EXTRA_KEY_MIN_LAND_SIZE = "EXTRA_KEY_MIN_LAND_SIZE"
        const val EXTRA_KEY_MAX_LAND_SIZE = "EXTRA_KEY_MAX_LAND_SIZE"

        const val EXTRA_KEY_MIN_CONSTRUCTION_YEAR = "EXTRA_KEY_MIN_CONSTRUCTION_YEAR"
        const val EXTRA_KEY_MAX_CONSTRUCTION_YEAR = "EXTRA_KEY_MAX_CONSTRUCTION_YEAR"

        const val EXTRA_KEY_INPUT_MIN_CONSTRUCTION_YEAR = "EXTRA_KEY_INPUT_MIN_CONSTRUCTION_YEAR"
        const val EXTRA_KEY_INPUT_MAX_CONSTRUCTION_YEAR = "EXTRA_KEY_INPUT_MAX_CONSTRUCTION_YEAR"

        const val EXTRA_KEY_INPUT_TENURES = "EXTRA_KEY_INPUT_TENURES"
        const val EXTRA_KEY_INPUT_BEDROOM_COUNTS = "EXTRA_KEY_INPUT_BEDROOM_COUNTS"

        const val EXTRA_KEY_INPUT_BATHROOM_COUNTS = "EXTRA_KEY_INPUT_BATHROOM_COUNTS"
        const val EXTRA_KEY_INPUT_RENTAL_TYPE = "EXTRA_KEY_INPUT_RENTAL_TYPE"
        const val EXTRA_KEY_INPUT_FLOORS = "EXTRA_KEY_INPUT_FLOORS"
        const val EXTRA_KEY_INPUT_FURNISHES = "EXTRA_KEY_INPUT_FURNISHES"
        const val EXTRA_KEY_INPUT_MIN_PRICE_RANGE = "EXTRA_KEY_INPUT_MIN_PRICE_RANGE"
        const val EXTRA_KEY_INPUT_MAX_PRICE_RANGE = "EXTRA_KEY_INPUT_MAX_PRICE_RANGE"
        const val EXTRA_KEY_INPUT_MIN_PSF = "EXTRA_KEY_INPUT_MIN_PSF"
        const val EXTRA_KEY_INPUT_MAX_PSF = "EXTRA_KEY_INPUT_MAX_PSF"
        const val EXTRA_KEY_INPUT_MIN_BUILT_SIZE = "EXTRA_KEY_INPUT_MIN_BUILT_SIZE"
        const val EXTRA_KEY_INPUT_MAX_BUILT_SIZE = "EXTRA_KEY_INPUT_MAX_BUILT_SIZE"
        const val EXTRA_KEY_INPUT_MIN_LAND_SIZE = "EXTRA_KEY_INPUT_MIN_LAND_SIZE"
        const val EXTRA_KEY_INPUT_MAX_LAND_SIZE = "EXTRA_KEY_INPUT_MAX_LAND_SIZE"
        const val EXTRA_KEY_INPUT_MIN_DATE_FIRST_POSTED = "EXTRA_KEY_INPUT_MIN_DATE_FIRST_POSTED"

        const val EXTRA_KEY_INPUT_HAS_VIRTUAL_TOURS = "EXTRA_KEY_INPUT_HAS_VIRTUAL_TOURS"
        const val EXTRA_KEY_INPUT_HAS_DRONE_VIEWS = "EXTRA_KEY_INPUT_HAS_DRONE_VIEWS"
        const val EXTRA_KEY_INPUT_OWNER_CERTIFICATION = "EXTRA_KEY_INPUT_OWNER_CERTIFICATION"
        const val EXTRA_KEY_INPUT_EXCLUSIVE_LISTING = "EXTRA_KEY_INPUT_EXCLUSIVE_LISTING"
        const val EXTRA_KEY_INPUT_X_LISTING_PRICE = "EXTRA_KEY_INPUT_X_LISTING_PRICE"

        const val EXTRA_KEY_INPUT_IS_INCLUDE_NEARBY = "EXTRA_KEY_INPUT_IS_INCLUDE_NEARBY"
        const val EXTRA_KEY_INPUT_IS_NEARBY_APPLICABLE = "EXTRA_KEY_INPUT_IS_NEARBY_APPLICABLE"

        const val PRICE_MAX_VALUE = 999_999_999
        const val PRICE_PSF_MAX_VALUE = 10_000
        const val FLOOR_AREA_MAX_VALUE = 10_000
        const val LAND_SIZE_MAX_VALUE = 999_999_999
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_filter_listing
    }

    override fun getViewModelClass(): Class<FilterListingViewModel> {
        return FilterListingViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}