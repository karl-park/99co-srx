package sg.searchhouse.agentconnect.view.activity.transaction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_filter_transaction.*
import kotlinx.android.synthetic.main.button_filter_property_type_residential.view.*
import kotlinx.android.synthetic.main.layout_filter_transaction_commercial_property_sub_types.*
import kotlinx.android.synthetic.main.layout_filter_transaction_residential_property_main_types.*
import kotlinx.android.synthetic.main.layout_range_number_boxes.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityFilterTransactionBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.transaction.FilterAreaTypePill
import sg.searchhouse.agentconnect.view.widget.transaction.FilterRadiusPill
import sg.searchhouse.agentconnect.view.widget.transaction.FilterTenureTypePill
import sg.searchhouse.agentconnect.view.widget.transaction.FilterTransactionPropertySubTypePill
import sg.searchhouse.agentconnect.viewmodel.activity.transaction.FilterTransactionViewModel

class FilterTransactionActivity :
    ViewModelActivity<FilterTransactionViewModel, ActivityFilterTransactionBinding>() {
    private var radiusPills: List<FilterRadiusPill>? = null
    private var areaTypePills: List<FilterAreaTypePill>? = null
    private var tenureTypePills: List<FilterTenureTypePill>? = null
    private var residentialSubTypePillListings: List<FilterTransactionPropertySubTypePill>? = null

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
        populateSearchRadius()
        populateAreaType()
        populateTenureType()
        setOnClickListeners()
        setupRanges()
    }

    private fun initParams() {
        resetTextBoxesFromPreviousSession()
        viewModel.initParams()
    }

    private fun resetTextBoxesFromPreviousSession() {
        layout_age.et_min.setNumber(viewModel.previousMinAge)
        layout_age.et_max.setNumber(viewModel.previousMaxAge)

        layout_size.et_min.setNumber(viewModel.previousMinSize)
        layout_size.et_max.setNumber(viewModel.previousMaxSize)
    }

    private fun setupActionBar() {
        supportActionBar?.let {
            it.setHomeAsUpIndicator(R.drawable.ic_cancel)
            it.title = ""
        }
    }

    private fun setupRanges() {
        val sizeRangeTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                getString(R.string.number_sqft, numberString)
            } else {
                getString(R.string.label_any)
            }
        }

        layout_size.et_min.setup(
            SIZE_MAX_VALUE,
            sizeRangeTransformer
        ) {
            // Do nothing
        }
        layout_size.et_max.setup(
            SIZE_MAX_VALUE,
            sizeRangeTransformer
        ) {
            // Do nothing
        }

        val ageTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                resources.getQuantityString(R.plurals.number_year, number, numberString)
            } else {
                getString(R.string.label_any)
            }
        }

        layout_age.et_min.setup(
            AGE_MAX_VALUE,
            ageTransformer
        ) {
            // Do nothing
        }
        layout_age.et_max.setup(
            AGE_MAX_VALUE,
            ageTransformer
        ) {
            // Do nothing
        }
    }

    private fun resetParams() {
        resetTextBoxes()
        viewModel.resetParams()
    }

    private fun resetTextBoxes() {
        layout_size.et_min.clear()
        layout_size.et_max.clear()

        layout_age.et_min.clear()
        layout_age.et_max.clear()
    }

    private fun observeLiveData() {
        viewModel.propertyMainType.observe(this, { propertyMainType ->
            populatePropertySubTypes(propertyMainType)
        })

        viewModel.propertySubTypes.observe(this, { propertySubTypes ->
            if (propertySubTypes != null) {
                when (viewModel.propertyPurpose) {
                    ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                        residentialSubTypePillListings?.map { it.binding.invalidateAll() }
                    }
                    else -> {
                    }
                }
            }
        })

        viewModel.radius.observe(this, {
            radiusPills?.map { it.binding.invalidateAll() }
        })

        viewModel.areaType.observe(this, {
            areaTypePills?.map { it.binding.invalidateAll() }
        })

        viewModel.tenureType.observe(this, {
            tenureTypePills?.map { it.binding.invalidateAll() }
        })
    }

    private fun populatePropertySubTypes(propertyMainType: ListingEnum.PropertyMainType?) {
        if (propertyMainType == ListingEnum.PropertyMainType.COMMERCIAL) return

        layout_property_sub_types.removeAllViews()
        propertyMainType?.let {
            when (propertyMainType) {
                ListingEnum.PropertyMainType.RESIDENTIAL -> {
                    // For case of ALL_RESIDENTIAL
                    // Remove all sub types
                    viewModel.propertySubTypes.postValue(null)
                }
                else -> {
                    residentialSubTypePillListings =
                        propertyMainType.propertySubTypes.map { addPropertySubTypeButton(it) }

                    val previousPropertySubTypes = viewModel.previousPropertySubTypes ?: emptyList()

                    // When it is in reset mode
                    // and this main type contains the default input sub types
                    // Choose the default one, otherwise choose all

                    val hasDefaultSubTypes =
                        previousPropertySubTypes.isNotEmpty() && propertyMainType.propertySubTypes.intersect(
                            previousPropertySubTypes
                        ).isNotEmpty()

                    if (hasDefaultSubTypes) {
                        viewModel.propertySubTypes.postValue(previousPropertySubTypes)
                    } else {
                        viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
                    }
                }
            }
        }
    }

    private fun addPropertySubTypeButton(propertySubType: ListingEnum.PropertySubType): FilterTransactionPropertySubTypePill {
        val pill = FilterTransactionPropertySubTypePill(this)
        pill.binding.propertySubType = propertySubType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.togglePropertySubType(propertySubType) }
        layout_property_sub_types.addView(pill)
        return pill
    }

    private fun populateSearchRadius() {
        radiusPills = TransactionEnum.Radius.values().map { addRadiusPill(it) }
    }

    private fun addRadiusPill(radius: TransactionEnum.Radius): FilterRadiusPill {
        val pill = FilterRadiusPill(this)
        pill.binding.radius = radius
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.toggleRadius(radius)
        }
        layout_search_radius.addView(pill)
        return pill
    }

    private fun populateAreaType() {
        areaTypePills = TransactionEnum.AreaType.values().map { addAreaTypePill(it) }
    }

    private fun populateTenureType() {
        tenureTypePills = TransactionEnum.TenureType.values().map { addTenureTypePill(it) }
    }

    private fun addAreaTypePill(areaType: TransactionEnum.AreaType): FilterAreaTypePill {
        val pill = FilterAreaTypePill(this)
        pill.binding.areaType = areaType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.toggleAreaType(areaType)
        }
        layout_area_type.addView(pill)
        return pill
    }

    private fun addTenureTypePill(tenureType: TransactionEnum.TenureType): FilterTenureTypePill {
        val pill = FilterTenureTypePill(this)
        pill.binding.tenureType = tenureType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener {
            viewModel.toggleTenureType(tenureType)
        }
        layout_tenure_type.addView(pill)
        return pill
    }

    // Sub types to select when click reset button
    private fun getDefaultPropertySubTypes(
        entryType: SearchEntryType,
        propertyPurpose: ListingEnum.PropertyPurpose
    ): List<ListingEnum.PropertySubType>? {
        return if (entryType == SearchEntryType.PROPERTY_SUB_TYPE && propertyPurpose == ListingEnum.PropertyPurpose.COMMERCIAL) {
            getInputPropertySubTypes()
        } else {
            null
        }
    }

    // !! Only applicable when `entryType` = `PROPERTY_SUB_TYPE` and `propertyPurpose` = `COMMERCIAL`
    private fun getInputPropertySubTypes(): List<ListingEnum.PropertySubType> {
        val subType =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_PROPERTY_SUB_TYPE) as ListingEnum.PropertySubType?
                ?: throw IllegalArgumentException("`EXTRA_KEY_INPUT_PROPERTY_SUB_TYPE` is required in `FilterTransactionActivity` when entry type is `PROPERTY_SUB_TYPE`")
        return listOf(subType)
    }

    // Sub types to select when first enter this page
    private fun getPreviousPropertySubTypes(
        entryType: SearchEntryType,
        propertyPurpose: ListingEnum.PropertyPurpose
    ): List<ListingEnum.PropertySubType>? {
        return if (entryType == SearchEntryType.PROPERTY_SUB_TYPE && propertyPurpose == ListingEnum.PropertyPurpose.COMMERCIAL) {
            getInputPropertySubTypes()
        } else {
            intent.getStringExtra(EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES)?.split(",")
                ?.mapNotNull { type ->
                    ListingEnum.PropertySubType.values().find { it.type == NumberUtil.toInt(type) }
                }
        }
    }

    private fun populateExtrasToParams() {
        // Part 1: Below are inputs only, no change of value in this filter
        val entryType =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_ENTRY_TYPE) as SearchEntryType?
                ?: throw IllegalArgumentException("Entry type is required in FilterTransactionActivity")
        viewModel.entryType = entryType

        val propertyPurpose =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE) as ListingEnum.PropertyPurpose?
                ?: throw IllegalArgumentException("PropertyType is required in FilterTransactionActivity")
        viewModel.propertyPurpose = propertyPurpose

        val ownershipType =
            intent.getSerializableExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE) as ListingEnum.OwnershipType?
                ?: throw IllegalArgumentException("OwnershipType is required in FilterTransactionActivity")
        viewModel.ownershipType = ownershipType

        val query = intent.getStringExtra(EXTRA_KEY_INPUT_QUERY)
        viewModel.query = query

        val projectId = intent.getIntExtra(EXTRA_KEY_INPUT_PROJECT_ID, -1)
        viewModel.projectId = projectId

        // Part 2: Below have both input and output, so assign default value to normal variable, and assign to live data only when reset params
        val propertyMainType = when (propertyPurpose) {
            ListingEnum.PropertyPurpose.RESIDENTIAL -> ListingEnum.PropertyMainType.RESIDENTIAL
            ListingEnum.PropertyPurpose.COMMERCIAL -> null
        }
        viewModel.defaultPropertyMainType = propertyMainType

        viewModel.defaultPropertySubTypes = getDefaultPropertySubTypes(entryType, propertyPurpose)

        viewModel.previousPropertyMainType =
            intent.getSerializableExtra(EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                ?: if (propertyPurpose == ListingEnum.PropertyPurpose.RESIDENTIAL) {
                    ListingEnum.PropertyMainType.RESIDENTIAL
                } else {
                    null
                }

        viewModel.previousPropertySubTypes = getPreviousPropertySubTypes(entryType, propertyPurpose)

        viewModel.previousRadius =
            intent.getSerializableExtra(EXTRA_KEY_PREVIOUS_RADIUS) as TransactionEnum.Radius?
        viewModel.previousAreaType =
            intent.getSerializableExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE) as TransactionEnum.AreaType?
        viewModel.previousTenureType =
            intent.getSerializableExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE) as TransactionEnum.TenureType?

        viewModel.previousMinAge =
            NumberUtil.getIntOrNullFromExtra(intent, EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE)
        viewModel.previousMaxAge =
            NumberUtil.getIntOrNullFromExtra(intent, EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE)
        viewModel.previousMinSize =
            NumberUtil.getIntOrNullFromExtra(intent, EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE)
        viewModel.previousMaxSize =
            NumberUtil.getIntOrNullFromExtra(intent, EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE)

        viewModel.districtIds = intent.getStringExtra(EXTRA_KEY_INPUT_DISTRICTS)
        viewModel.hdbTownIds = intent.getStringExtra(EXTRA_KEY_INPUT_HDB_TOWNS)
        viewModel.amenityIds = intent.getStringExtra(EXTRA_KEY_INPUT_AMENITIES)
    }

    private fun setOnClickListeners() {
        btn_submit.setOnClickListener { submitFilter() }
        setResidentialPropertyMainTypeOnClickListeners()
        setCommercialPropertySubTypeOnClickListeners()
    }

    private fun setResidentialPropertyMainTypeOnClickListeners() {
        btn_property_main_type_hdb.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.HDB)
        }
        btn_property_main_type_condo.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.CONDO)
        }
        btn_property_main_type_landed.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.LANDED)
        }
    }

    // TODO: Try generalise on sub property type click
    private fun setCommercialPropertySubTypeOnClickListeners() {
        btn_property_sub_type_retail.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.RETAIL)
        }
        btn_property_sub_type_office.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.OFFICE)
        }
        btn_property_sub_type_factory.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.FACTORY)
        }
        btn_property_sub_type_warehouse.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.WAREHOUSE)
        }
        btn_property_sub_type_land.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.LAND)
        }
        btn_property_sub_type_hdb_shop_house.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.HDB_SHOP_HOUSE)
        }
        btn_property_sub_type_shop_house.button.setOnClickListener {
            viewModel.togglePropertySubType(ListingEnum.PropertySubType.SHOP_HOUSE)
        }
    }

    private fun submitFilter() {
        val intent = Intent()

        if (viewModel.entryType != SearchEntryType.QUERY_PROPERTY_SUB_TYPE) {
            intent.putExtra(EXTRA_KEY_OUTPUT_PROPERTY_MAIN_TYPE, viewModel.propertyMainType.value)
            val propertySubTypes =
                viewModel.propertySubTypes.value?.map { it.type }?.joinToString(",")
            intent.putExtra(EXTRA_KEY_OUTPUT_PROPERTY_SUB_TYPES, propertySubTypes)
        }

        intent.putExtra(EXTRA_KEY_OUTPUT_RADIUS, viewModel.radius.value)
        intent.putExtra(EXTRA_KEY_OUTPUT_AREA_TYPE, viewModel.areaType.value)
        intent.putExtra(EXTRA_KEY_OUTPUT_TENURE_TYPE, viewModel.tenureType.value)

        val builtSizeMin = layout_size.et_min.value
        val builtSizeMax = layout_size.et_max.value

        val propertyAgeMin = layout_age.et_min.value
        val propertyAgeMax = layout_age.et_max.value

        intent.putExtra(EXTRA_KEY_OUTPUT_MIN_BUILT_SIZE, builtSizeMin)
        intent.putExtra(EXTRA_KEY_OUTPUT_MAX_BUILT_SIZE, builtSizeMax)
        intent.putExtra(EXTRA_KEY_OUTPUT_MIN_PROPERTY_AGE, propertyAgeMin)
        intent.putExtra(EXTRA_KEY_OUTPUT_MAX_PROPERTY_AGE, propertyAgeMax)

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

    companion object {
        private const val EXTRA_KEY_INPUT_PROPERTY_PURPOSE = "EXTRA_KEY_INPUT_PROPERTY_PURPOSE"
        private const val EXTRA_KEY_INPUT_OWNERSHIP_TYPE = "EXTRA_KEY_INPUT_OWNERSHIP_TYPE"
        private const val EXTRA_KEY_INPUT_ENTRY_TYPE = "EXTRA_KEY_INPUT_ENTRY_TYPE"
        private const val EXTRA_KEY_INPUT_QUERY = "EXTRA_KEY_INPUT_QUERY"
        private const val EXTRA_KEY_INPUT_PROJECT_ID = "EXTRA_KEY_INPUT_PROJECT_ID"
        private const val EXTRA_KEY_INPUT_PROPERTY_SUB_TYPE = "EXTRA_KEY_INPUT_PROPERTY_SUB_TYPE"
        private const val EXTRA_KEY_INPUT_HDB_TOWNS = "EXTRA_KEY_INPUT_HDB_TOWNS"
        private const val EXTRA_KEY_INPUT_DISTRICTS = "EXTRA_KEY_INPUT_DISTRICTS"
        private const val EXTRA_KEY_INPUT_AMENITIES = "EXTRA_KEY_INPUT_AMENITIES"

        const val EXTRA_KEY_OUTPUT_PROPERTY_MAIN_TYPE = "EXTRA_KEY_OUTPUT_PROPERTY_MAIN_TYPE"
        const val EXTRA_KEY_OUTPUT_PROPERTY_SUB_TYPES = "EXTRA_KEY_OUTPUT_PROPERTY_SUB_TYPES"
        const val EXTRA_KEY_OUTPUT_RADIUS = "EXTRA_KEY_OUTPUT_RADIUS"
        const val EXTRA_KEY_OUTPUT_AREA_TYPE = "EXTRA_KEY_OUTPUT_AREA_TYPE"
        const val EXTRA_KEY_OUTPUT_TENURE_TYPE = "EXTRA_KEY_OUTPUT_TENURE_TYPE"
        const val EXTRA_KEY_OUTPUT_MIN_BUILT_SIZE = "EXTRA_KEY_OUTPUT_MIN_BUILT_SIZE"
        const val EXTRA_KEY_OUTPUT_MAX_BUILT_SIZE = "EXTRA_KEY_OUTPUT_MAX_BUILT_SIZE"
        const val EXTRA_KEY_OUTPUT_MIN_PROPERTY_AGE = "EXTRA_KEY_OUTPUT_MIN_PROPERTY_AGE"
        const val EXTRA_KEY_OUTPUT_MAX_PROPERTY_AGE = "EXTRA_KEY_OUTPUT_MAX_PROPERTY_AGE"

        private const val EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE =
            "EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE"
        private const val EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES =
            "EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES"
        private const val EXTRA_KEY_PREVIOUS_RADIUS = "EXTRA_KEY_PREVIOUS_RADIUS"
        private const val EXTRA_KEY_PREVIOUS_AREA_TYPE = "EXTRA_KEY_PREVIOUS_AREA_TYPE"
        private const val EXTRA_KEY_PREVIOUS_TENURE_TYPE = "EXTRA_KEY_PREVIOUS_TENURE_TYPE"
        private const val EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE = "EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE"
        private const val EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE = "EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE"
        private const val EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE =
            "EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE"
        private const val EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE =
            "EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE"

        const val SIZE_MAX_VALUE = 1_000_000
        const val AGE_MAX_VALUE = 50

        fun launchBySearchText(
            activity: BaseActivity,
            requestCode: Int,
            query: String,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousPropertyMainType: ListingEnum.PropertyMainType? = null,
            previousPropertySubTypes: String? = null,
            previousRadius: TransactionEnum.Radius? = null,
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(EXTRA_KEY_INPUT_ENTRY_TYPE, SearchEntryType.QUERY)
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_QUERY, query)

            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE, previousPropertyMainType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES, previousPropertySubTypes)
            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)
            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }

        fun launchByProjectId(
            activity: BaseActivity,
            requestCode: Int,
            projectId: Int,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousPropertyMainType: ListingEnum.PropertyMainType? = null,
            previousPropertySubTypes: String? = null,
            previousRadius: TransactionEnum.Radius? = null,

            // Below only applies for for rotation
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(EXTRA_KEY_INPUT_ENTRY_TYPE, SearchEntryType.PROJECT)
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_PROJECT_ID, projectId)

            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE, previousPropertyMainType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES, previousPropertySubTypes)
            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)

            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }

        fun launchByQueryPropertySubType(
            activity: BaseActivity,
            requestCode: Int,
            query: String,
            propertySubType: ListingEnum.PropertySubType,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousRadius: TransactionEnum.Radius? = null,
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(
                EXTRA_KEY_INPUT_ENTRY_TYPE,
                SearchEntryType.QUERY_PROPERTY_SUB_TYPE
            )
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_SUB_TYPE, propertySubType)
            intent.putExtra(EXTRA_KEY_INPUT_QUERY, query)

            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)
            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }

        fun launchByPropertySubType(
            activity: BaseActivity,
            requestCode: Int,
            propertySubType: ListingEnum.PropertySubType,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousRadius: TransactionEnum.Radius? = null,
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(
                EXTRA_KEY_INPUT_ENTRY_TYPE,
                SearchEntryType.PROPERTY_SUB_TYPE
            )
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_SUB_TYPE, propertySubType)

            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)
            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }

        fun launchByHdbTowns(
            activity: BaseActivity,
            requestCode: Int,
            hdbTownIds: String,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousPropertyMainType: ListingEnum.PropertyMainType? = null,
            previousPropertySubTypes: String? = null,
            previousRadius: TransactionEnum.Radius? = null,
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(
                EXTRA_KEY_INPUT_ENTRY_TYPE,
                SearchEntryType.HDB_TOWN
            )
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_HDB_TOWNS, hdbTownIds)

            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE, previousPropertyMainType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES, previousPropertySubTypes)
            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)
            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }

        fun launchByDistricts(
            activity: BaseActivity,
            requestCode: Int,
            districtIds: String,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousPropertyMainType: ListingEnum.PropertyMainType? = null,
            previousPropertySubTypes: String? = null,
            previousRadius: TransactionEnum.Radius? = null,
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(
                EXTRA_KEY_INPUT_ENTRY_TYPE,
                SearchEntryType.DISTRICT
            )
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_DISTRICTS, districtIds)

            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE, previousPropertyMainType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES, previousPropertySubTypes)
            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)
            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }

        fun launchByAmenities(
            activity: BaseActivity,
            requestCode: Int,
            amenityIds: String,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            previousPropertyMainType: ListingEnum.PropertyMainType? = null,
            previousPropertySubTypes: String? = null,
            previousRadius: TransactionEnum.Radius? = null,
            previousAreaType: TransactionEnum.AreaType? = null,
            previousTenureType: TransactionEnum.TenureType? = null,
            previousMinBuiltSize: Int? = null,
            previousMaxBuiltSize: Int? = null,
            previousMinPropertyAge: Int? = null,
            previousMaxPropertyAge: Int? = null
        ) {
            val intent = Intent(activity, FilterTransactionActivity::class.java)

            intent.putExtra(
                EXTRA_KEY_INPUT_ENTRY_TYPE,
                SearchEntryType.AMENITY
            )
            intent.putExtra(EXTRA_KEY_INPUT_PROPERTY_PURPOSE, propertyPurpose)
            intent.putExtra(EXTRA_KEY_INPUT_OWNERSHIP_TYPE, ownershipType)
            intent.putExtra(EXTRA_KEY_INPUT_AMENITIES, amenityIds)

            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_MAIN_TYPE, previousPropertyMainType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_PROPERTY_SUB_TYPES, previousPropertySubTypes)
            intent.putExtra(EXTRA_KEY_PREVIOUS_RADIUS, previousRadius)
            intent.putExtra(EXTRA_KEY_PREVIOUS_AREA_TYPE, previousAreaType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_TENURE_TYPE, previousTenureType)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_BUILT_SIZE, previousMinBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_BUILT_SIZE, previousMaxBuiltSize)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MIN_PROPERTY_AGE, previousMinPropertyAge)
            intent.putExtra(EXTRA_KEY_PREVIOUS_MAX_PROPERTY_AGE, previousMaxPropertyAge)

            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_filter_transaction
    }

    override fun getViewModelClass(): Class<FilterTransactionViewModel> {
        return FilterTransactionViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}