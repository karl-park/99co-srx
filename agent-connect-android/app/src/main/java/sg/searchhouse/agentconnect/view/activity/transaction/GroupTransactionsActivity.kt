package sg.searchhouse.agentconnect.view.activity.transaction

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_group_transactions.*
import kotlinx.android.synthetic.main.card_transaction_header.*
import kotlinx.android.synthetic.main.layout_group_transactions_lists.*
import kotlinx.android.synthetic.main.layout_group_transactions_lists.view.*
import kotlinx.android.synthetic.main.layout_group_transactions_summary.*
import kotlinx.android.synthetic.main.radio_group_ownership.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityGroupTransactionsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.enumeration.app.TransactionsDisplayMode
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchCriteriaV2VO
import sg.searchhouse.agentconnect.event.transaction.*
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.adapter.transaction.group.GroupTransactionPagerAdapter
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry
import sg.searchhouse.agentconnect.view.helper.search.TransactionsActivityEntry
import sg.searchhouse.agentconnect.viewmodel.activity.transaction.GroupTransactionsViewModel

class GroupTransactionsActivity :
    ViewModelActivity<GroupTransactionsViewModel, ActivityGroupTransactionsBinding>() {
    private var entryType: SearchEntryType? = null
    private var query: String? = null
    private lateinit var defaultOwnershipType: ListingEnum.OwnershipType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
        setupExtras()
        setupViews()
        setListeners()
        listenRxBuses()
    }

    private fun setListeners() {
        setScrollListener()
        setOnClickListeners()
    }

    private fun setScrollListener() {
        scroll_view?.run {
            ViewUtil.listenVerticalScrollEnd(this, reachBottom = {
                viewModel.isLockScrollView.postValue(true)
            })
        }
    }

    private fun listenRxBuses() {
        listenRxBus(GetProjectTransactionsEvent::class.java) {
            val projectId =
                NumberUtil.toInt(it.projectId) ?: return@listenRxBus ErrorUtil.handleError(
                    "The transaction project ID ${it.projectId} should be an integer."
                )
            val ownershipType =
                viewModel.ownershipType.value ?: return@listenRxBus ErrorUtil.handleError(
                    "Ownership type should present."
                )
            val propertyPurpose =
                viewModel.propertyPurpose.value ?: return@listenRxBus ErrorUtil.handleError(
                    "Property purpose should present."
                )

            TransactionsActivityEntry.launchByProject(
                this,
                projectId,
                it.projectName,
                it.projectDescription,
                it.isShowTower(),
                it.getPropertySubType(),
                propertyPurpose,
                ownershipType
            )
        }
        listenRxBus(UnlockActivityScrollEvent::class.java) {
            viewModel.isLockScrollView.postValue(false)
        }
        listenRxBus(RequestLoadGroupTransactionsEvent::class.java) {
            loadingAndPerformRequest()
        }
        listenRxBus(SendGroupTransactionsCsvEvent::class.java) {
            viewModel.exportTransactions(it.content)
        }
    }

    override fun onBackPressed() {
        when (viewModel.displayMode.value) {
            TransactionsDisplayMode.MAP -> viewModel.displayMode.postValue(
                TransactionsDisplayMode.LIST
            )
            else -> super.onBackPressed()
        }
    }

    private fun setupViews() {
        setupOwnershipRadioButton()
        setupViewPager()
        setupScrollViewAnimation()
    }

    private fun setupScrollViewAnimation() {
        layout_container?.layoutTransition?.enableTransitionType(
            LayoutTransition.CHANGE_DISAPPEARING
        )
    }

    private fun setupViewPager() {
        layout_group_transactions_lists.view_pager.adapter =
            GroupTransactionPagerAdapter(
                this,
                supportFragmentManager,
                defaultOwnershipType,
                isShowProjects()
            )
        if (isShowProjects()) {
            layout_group_transactions_lists.tab_layout.setupWithViewPager(
                layout_group_transactions_lists.view_pager
            ) {
                scroll_view?.run {
                    if (scrollY > 0) {
                        viewModel.isLockScrollView.postValue(true)
                    }
                }
                viewModel.tabPosition.postValue(view_pager.currentItem)
            }
        }
    }

    private fun isShowProjects(): Boolean {
        // Hide projects fragment if is empty query
        return !(entryType == SearchEntryType.QUERY && TextUtils.isEmpty(query))
    }

    private fun observeLiveData() {
        viewModel.entryType.observe(this) {
            requestLoadGroupTransactions()
        }

        viewModel.ownershipType.observeNotNull(this) { ownershipType ->
            viewModel.mainResponse.postValue(null) // Minimize view artifact, i.e. sale/rent specific formatting on summary
            radio_group_ownership_type?.setValue(ownershipType)
            requestLoadGroupTransactions()
        }

        viewModel.isLockScrollView.observeNotNull(this) { isLockScrollView ->
            layout_group_transactions_summary?.run {
                // Portrait
                RxBus.publish(LockFragmentScrollEvent(!isLockScrollView))
                setViewPagerHeight() // Reset view pager height, to fix bug of its height overshoot sometimes
                if (!isLockScrollView) {
                    animateShowSummaryLayout()
                }
            } ?: run {
                // Landscape
                RxBus.publish(LockFragmentScrollEvent(true))
                setViewPagerHeight() // Reset view pager height, to fix bug of its height overshoot sometimes
            }
        }

        viewModel.summary.observe(this) { summary ->
            if (summary != null) {
                if (summary.isSingleProject) {
                    val ownershipType =
                        viewModel.ownershipType.value ?: throw IllegalArgumentException(
                            "Ownership type should present."
                        )

                    val propertyPurpose =
                        viewModel.propertyPurpose.value ?: throw IllegalArgumentException(
                            "Property purpose should present."
                        )

                    val projectDescription = ""
                    TransactionsActivityEntry.launchByProject(
                        this,
                        summary.projectId,
                        getNextStackProjectName(summary.header),
                        projectDescription,
                        summary.isShowTower(),
                        summary.getPropertySubType(),
                        propertyPurpose,
                        ownershipType
                    )
                    finish()
                }
            }
        }

        viewModel.reportExcelLocalFileName.observeNotNull(this) {
            IntentUtil.viewCsv(
                this@GroupTransactionsActivity,
                it,
                AppConstant.DIR_TRANSACTION_REPORT
            )
        }
    }

    private fun getNextStackProjectName(summaryHeader: String?): String {
        val queryLabel = viewModel.queryLabel.value
        return when {
            !StringUtil.isEmpty(summaryHeader) -> summaryHeader
            !StringUtil.isEmpty(queryLabel) -> queryLabel
            else -> ""
        } ?: ""
    }

    private fun animateShowSummaryLayout() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        layout_group_transactions_lists.startAnimation(animation)
        layout_group_transactions_summary?.startAnimation(animation)
    }

    private fun loadingAndPerformRequest() {
        // Load projects
        RxBus.publish(UpdateGroupProjectsEvent(getProjectsRequestBody()))

        // Load transactions
        RxBus.publish(
            UpdateGroupTransactionsEvent(
                viewModel.ownershipType.value ?: defaultOwnershipType,
                viewModel.propertyMainType.value,
                getTransactionsRequestBody(),
                isEnableTransactionsPagination()
            )
        )
        viewModel.performRequest()
    }

    private fun isEnableTransactionsPagination(): Boolean {
        return entryType != SearchEntryType.PROPERTY_SUB_TYPE
    }

    private fun getTransactionsRequestBody(): TransactionSearchCriteriaV2VO {
        val propertyTypes = viewModel.propertySubTypes.value?.map {
            it.type
        } ?: run {
            when (viewModel.propertyPurpose.value) {
                ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                    ListingEnum.PropertyMainType.RESIDENTIAL.propertySubTypes.map { it.type }
                }
                ListingEnum.PropertyPurpose.COMMERCIAL -> {
                    ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes.map { it.type }
                }
                else -> {
                    throw IllegalArgumentException("Missing propertyPurpose input")
                }
            }
        }

        val ownershipType = viewModel.ownershipType.value?.value
            ?: throw IllegalArgumentException("Ownership type must be present here!")

        return TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType,
            ageFrom = viewModel.externalMinAge,
            ageTo = viewModel.externalMaxAge,
            districts = viewModel.districtIds.value?.let { StringUtil.getIntListFromString(it) },
            hdbTowns = viewModel.hdbTownIds.value?.let { StringUtil.getIntListFromString(it) },
            amenities = viewModel.amenityIds.value?.let { StringUtil.getIntListFromString(it) },
            isFreeholdTenure = getTenureTypeBoolean(),
            limit = getTransactionsPageLimit(),
            orderByParam = TransactionEnum.SortType.DEFAULT.value,
            page = 1,
            propertyTypes = propertyTypes,
            radius = viewModel.externalRadius?.radiusValue,
            typeOfArea = viewModel.externalAreaType?.value,
            sizeFrom = viewModel.externalMinSize,
            sizeTo = viewModel.externalMaxSize,
            text = viewModel.query.value
        )
    }

    private fun getTransactionsPageLimit(): Int {
        return when (entryType) {
            SearchEntryType.PROPERTY_SUB_TYPE -> BATCH_SIZE_GLOBAL_FIRST_FIFTY
            else -> AppConstant.BATCH_SIZE_TRANSACTIONS
        }
    }

    private fun getProjectsRequestBody(): TransactionSearchCriteriaV2VO {
        val propertyTypes = when (entryType) {
            SearchEntryType.QUERY_PROPERTY_SUB_TYPE -> {
                val propertySubType = viewModel.propertySubTypes.value?.getOrNull(0)
                    ?: throw IllegalArgumentException("Missing propertySubType!")
                listOf(propertySubType.type)
            }
            else -> {
                // External sub types
                viewModel.getPropertySubTypes().split(",").mapNotNull {
                    NumberUtil.toInt(it)
                }
            }
        }

        val ownershipType = viewModel.ownershipType.value?.value
            ?: throw IllegalArgumentException("Ownership type must be present here!")

        // NOTE: No pagination for projects
        return TransactionSearchCriteriaV2VO(
            saleOrRent = ownershipType,
            ageFrom = viewModel.externalMinAge,
            ageTo = viewModel.externalMaxAge,
            districts = viewModel.districtIds.value?.let { StringUtil.getIntListFromString(it) },
            hdbTowns = viewModel.hdbTownIds.value?.let { StringUtil.getIntListFromString(it) },
            amenities = viewModel.amenityIds.value?.let { StringUtil.getIntListFromString(it) },
            isFreeholdTenure = getTenureTypeBoolean(),
            orderByParam = TransactionEnum.SortType.DEFAULT.value,
            propertyTypes = propertyTypes,
            radius = viewModel.externalRadius?.radiusValue,
            typeOfArea = viewModel.externalAreaType?.value,
            sizeFrom = viewModel.externalMinSize,
            sizeTo = viewModel.externalMaxSize,
            text = viewModel.query.value
        )
    }

    private fun getTenureTypeBoolean(): Boolean? {
        return viewModel.externalTenureType?.run { this == TransactionEnum.TenureType.FREEHOLD }
    }

    private fun setupExtras() {
        entryType =
            intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_ENTRY_TYPE) as SearchEntryType?
        when (entryType) {
            SearchEntryType.DISTRICT -> {
                val districtIds =
                    intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_DISTRICT_IDS)
                val districtNames =
                    intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_DISTRICT_NAMES)
                val propertyMainType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_MAIN_TYPE` in `GroupTransactionsActivity`")
                viewModel.districtIds.postValue(districtIds)
                viewModel.districtNames.postValue(districtNames)
                viewModel.propertyMainType.postValue(propertyMainType)
                viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
            }
            SearchEntryType.HDB_TOWN -> {
                val hdbTownIds =
                    intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_HDB_TOWN_IDS)
                val hdbTownNames =
                    intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_HDB_TOWN_NAMES)
                val propertyMainType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_MAIN_TYPE` in `GroupTransactionsActivity`")
                viewModel.hdbTownIds.postValue(hdbTownIds)
                viewModel.hdbTownNames.postValue(hdbTownNames)
                viewModel.propertyMainType.postValue(propertyMainType)
                viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
            }
            SearchEntryType.AMENITY -> {
                val amenityIds =
                    intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_AMENITY_IDS)
                val amenityNames =
                    intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_AMENITY_NAMES)
                val propertyMainType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_MAIN_TYPE` in `GroupTransactionsActivity`")
                viewModel.amenityIds.postValue(amenityIds)
                viewModel.amenityNames.postValue(amenityNames)
                viewModel.propertyMainType.postValue(propertyMainType)
                viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
            }
            SearchEntryType.QUERY -> {
                query = intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_QUERY)
                val propertyMainType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_MAIN_TYPE` in `GroupTransactionsActivity`")
                viewModel.query.postValue(query)
                viewModel.propertyMainType.postValue(propertyMainType)
                viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
            }
            SearchEntryType.QUERY_PROPERTY_SUB_TYPE -> {
                // From search result
                val query = intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_QUERY)
                val propertySubType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_SUB_TYPE) as ListingEnum.PropertySubType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_SUB_TYPE` for `QUERY_PROPERTY_SUB_TYPE` entry type")
                val propertyMainType = PropertyTypeUtil.getPropertyMainType(propertySubType.type)
                    ?: throw IllegalArgumentException("Invalid `EXTRA_KEY_PROPERTY_SUB_TYPE` with value ${propertySubType.type} in `GroupTransactionsActivity`")
                viewModel.query.postValue(query)
                viewModel.propertyMainType.postValue(propertyMainType)
                viewModel.propertySubTypes.postValue(
                    when {
                        PropertyTypeUtil.isPrimarySubType(propertySubType.type) -> propertyMainType.propertySubTypes
                        else -> listOf(propertySubType)
                    }
                )
            }
            SearchEntryType.PROPERTY_SUB_TYPE -> {
                // From commercial buttons of `first 50 transactions`
                val propertySubType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_SUB_TYPE) as ListingEnum.PropertySubType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_SUB_TYPE` for `QUERY_PROPERTY_SUB_TYPE` entry type")
                if (!PropertyTypeUtil.isCommercial(propertySubType.type)) {
                    throw IllegalArgumentException("Property sub type must be commercial for `EXTRA_KEY_PROPERTY_SUB_TYPE` of `QUERY_PROPERTY_SUB_TYPE` entry type")
                }
                viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.COMMERCIAL)
                viewModel.propertySubTypes.postValue(listOf(propertySubType))
            }
            SearchEntryType.PROPERTY_MAIN_TYPE -> {
                query = intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_QUERY)
                val propertyMainType =
                    intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
                        ?: throw IllegalArgumentException("Missing `EXTRA_KEY_PROPERTY_MAIN_TYPE` in `GroupTransactionsActivity`")
                viewModel.query.postValue(query)
                viewModel.propertyMainType.postValue(propertyMainType)
                viewModel.propertySubTypes.postValue(propertyMainType.propertySubTypes)
            }
            else -> {
                throw IllegalArgumentException("Missing/invalid entry type for TransactionActivity: `${entryType?.name}`")
            }
        }

        defaultOwnershipType =
            intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_OWNERSHIP_TYPE) as ListingEnum.OwnershipType
        val propertyPurpose =
            intent.getSerializableExtra(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_PURPOSE) as ListingEnum.PropertyPurpose

        viewModel.propertyPurpose.postValue(propertyPurpose)
        if (viewModel.ownershipType.value == null) {
            viewModel.ownershipType.postValue(defaultOwnershipType)
        }
        viewModel.entryType.postValue(entryType)
        viewModel.isShowProjects.postValue(isShowProjects())
    }

    private fun setOnClickListeners() {
        btn_map?.setOnClickListener {
            viewModel.displayMode.postValue(TransactionsDisplayMode.MAP)
        }

        btn_list?.setOnClickListener {
            viewModel.displayMode.postValue(TransactionsDisplayMode.LIST)
        }

        btn_transaction_back?.setOnClickListener { onBackPressed() }

        btn_title?.setOnClickListener {
            SearchActivity.launch(
                this,
                SearchActivity.SearchType.TRANSACTIONS,
                viewModel.propertyPurpose.value,
                viewModel.ownershipType.value,
                SearchActivity.ExpandMode.COMPACT,
                REQUEST_CODE_SEARCH
            )
        }

        tv_title?.setOnLongClickListener {
            ViewUtil.showMessage(tv_title.text.toString())
            true
        }

        btn_filter?.setOnClickListener { startFilter() }

        btn_export_transactions?.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.TRANSACTION_SEARCH_EXPORT,
                onSuccessAccessibility = { exportTransactions() },
                onLimitedAccessibility = {
                    ViewUtil.showMessage(R.string.toast_limited_transactions_export)
                    exportTransactions(isLimited = true)
                }
            )
        }
    }

    private fun exportTransactions(isLimited: Boolean = false) {
        RxBus.publish(RequestGroupTransactionsCsvEvent(isLimited = isLimited))
    }

    private fun startFilter() {
        when (viewModel.entryType.value) {
            SearchEntryType.QUERY, SearchEntryType.PROPERTY_MAIN_TYPE -> {
                val query = viewModel.query.value
                    ?: throw java.lang.IllegalArgumentException("query must present here")
                val propertyPurpose = viewModel.propertyPurpose.value
                    ?: throw java.lang.IllegalArgumentException("propertyPurpose must present here")
                val ownershipType = viewModel.ownershipType.value
                    ?: throw java.lang.IllegalArgumentException("ownershipType must present here")

                FilterTransactionActivity.launchBySearchText(
                    this,
                    REQUEST_CODE_FILTER_TRANSACTIONS,
                    query = query,
                    propertyPurpose = propertyPurpose,
                    ownershipType = ownershipType,
                    previousPropertyMainType = viewModel.propertyMainType.value,
                    previousPropertySubTypes = viewModel.getPropertySubTypesString(),
                    previousRadius = viewModel.externalRadius,
                    previousAreaType = viewModel.externalAreaType,
                    previousTenureType = viewModel.externalTenureType,
                    previousMinBuiltSize = viewModel.externalMinSize,
                    previousMaxBuiltSize = viewModel.externalMaxSize,
                    previousMinPropertyAge = viewModel.externalMinAge,
                    previousMaxPropertyAge = viewModel.externalMaxAge
                )
            }
            SearchEntryType.QUERY_PROPERTY_SUB_TYPE -> {
                val query = viewModel.query.value
                    ?: throw java.lang.IllegalArgumentException("query must present here")
                val propertySubType = viewModel.propertySubTypes.value?.getOrNull(0)
                    ?: throw java.lang.IllegalArgumentException("propertySubType must present here")
                val propertyPurpose = viewModel.propertyPurpose.value
                    ?: throw java.lang.IllegalArgumentException("propertyPurpose must present here")
                val ownershipType = viewModel.ownershipType.value
                    ?: throw java.lang.IllegalArgumentException("ownershipType must present here")

                FilterTransactionActivity.launchByQueryPropertySubType(
                    this,
                    REQUEST_CODE_FILTER_TRANSACTIONS,
                    query = query,
                    propertySubType = propertySubType,
                    propertyPurpose = propertyPurpose,
                    ownershipType = ownershipType,
                    previousRadius = viewModel.externalRadius,
                    previousAreaType = viewModel.externalAreaType,
                    previousTenureType = viewModel.externalTenureType,
                    previousMinBuiltSize = viewModel.externalMinSize,
                    previousMaxBuiltSize = viewModel.externalMaxSize,
                    previousMinPropertyAge = viewModel.externalMinAge,
                    previousMaxPropertyAge = viewModel.externalMaxAge
                )
            }
            SearchEntryType.HDB_TOWN -> {
                val hdbTownIds = viewModel.hdbTownIds.value
                    ?: throw java.lang.IllegalArgumentException("hdbTownIds must present here")
                val propertyPurpose = viewModel.propertyPurpose.value
                    ?: throw java.lang.IllegalArgumentException("propertyPurpose must present here")
                val ownershipType = viewModel.ownershipType.value
                    ?: throw java.lang.IllegalArgumentException("ownershipType must present here")

                FilterTransactionActivity.launchByHdbTowns(
                    this,
                    REQUEST_CODE_FILTER_TRANSACTIONS,
                    hdbTownIds = hdbTownIds,
                    propertyPurpose = propertyPurpose,
                    ownershipType = ownershipType,
                    previousPropertyMainType = viewModel.propertyMainType.value,
                    previousPropertySubTypes = viewModel.getPropertySubTypesString(),
                    previousRadius = viewModel.externalRadius,
                    previousAreaType = viewModel.externalAreaType,
                    previousTenureType = viewModel.externalTenureType,
                    previousMinBuiltSize = viewModel.externalMinSize,
                    previousMaxBuiltSize = viewModel.externalMaxSize,
                    previousMinPropertyAge = viewModel.externalMinAge,
                    previousMaxPropertyAge = viewModel.externalMaxAge
                )
            }
            SearchEntryType.DISTRICT -> {
                val districtIds = viewModel.districtIds.value
                    ?: throw java.lang.IllegalArgumentException("districtIds must present here")
                val propertyPurpose = viewModel.propertyPurpose.value
                    ?: throw java.lang.IllegalArgumentException("propertyPurpose must present here")
                val ownershipType = viewModel.ownershipType.value
                    ?: throw java.lang.IllegalArgumentException("ownershipType must present here")

                FilterTransactionActivity.launchByDistricts(
                    this,
                    REQUEST_CODE_FILTER_TRANSACTIONS,
                    districtIds = districtIds,
                    propertyPurpose = propertyPurpose,
                    ownershipType = ownershipType,
                    previousPropertyMainType = viewModel.propertyMainType.value,
                    previousPropertySubTypes = viewModel.getPropertySubTypesString(),
                    previousRadius = viewModel.externalRadius,
                    previousAreaType = viewModel.externalAreaType,
                    previousTenureType = viewModel.externalTenureType,
                    previousMinBuiltSize = viewModel.externalMinSize,
                    previousMaxBuiltSize = viewModel.externalMaxSize,
                    previousMinPropertyAge = viewModel.externalMinAge,
                    previousMaxPropertyAge = viewModel.externalMaxAge
                )
            }
            SearchEntryType.AMENITY -> {
                val amenityIds = viewModel.amenityIds.value
                    ?: throw java.lang.IllegalArgumentException("amenityIds must present here")
                val propertyPurpose = viewModel.propertyPurpose.value
                    ?: throw java.lang.IllegalArgumentException("propertyPurpose must present here")
                val ownershipType = viewModel.ownershipType.value
                    ?: throw java.lang.IllegalArgumentException("ownershipType must present here")

                FilterTransactionActivity.launchByAmenities(
                    this,
                    REQUEST_CODE_FILTER_TRANSACTIONS,
                    amenityIds = amenityIds,
                    propertyPurpose = propertyPurpose,
                    ownershipType = ownershipType,
                    previousPropertyMainType = viewModel.propertyMainType.value,
                    previousPropertySubTypes = viewModel.getPropertySubTypesString(),
                    previousRadius = viewModel.externalRadius,
                    previousAreaType = viewModel.externalAreaType,
                    previousTenureType = viewModel.externalTenureType,
                    previousMinBuiltSize = viewModel.externalMinSize,
                    previousMaxBuiltSize = viewModel.externalMaxSize,
                    previousMinPropertyAge = viewModel.externalMinAge,
                    previousMaxPropertyAge = viewModel.externalMaxAge
                )
            }
            SearchEntryType.PROPERTY_SUB_TYPE -> {
                // For commercial sub type first 50
                val propertySubType = viewModel.propertySubTypes.value?.getOrNull(0)
                    ?: throw java.lang.IllegalArgumentException("propertySubType must present here")
                val propertyPurpose = viewModel.propertyPurpose.value
                    ?: throw java.lang.IllegalArgumentException("propertyPurpose must present here")
                val ownershipType = viewModel.ownershipType.value
                    ?: throw java.lang.IllegalArgumentException("ownershipType must present here")

                FilterTransactionActivity.launchByPropertySubType(
                    this,
                    REQUEST_CODE_FILTER_TRANSACTIONS,
                    propertySubType = propertySubType,
                    propertyPurpose = propertyPurpose,
                    ownershipType = ownershipType,
                    previousRadius = viewModel.externalRadius,
                    previousAreaType = viewModel.externalAreaType,
                    previousTenureType = viewModel.externalTenureType,
                    previousMinBuiltSize = viewModel.externalMinSize,
                    previousMaxBuiltSize = viewModel.externalMaxSize,
                    previousMinPropertyAge = viewModel.externalMinAge,
                    previousMaxPropertyAge = viewModel.externalMaxAge
                )
            }
            else -> {
                ViewUtil.showMessage("Not implemented yet")
            }
        }
    }

    // Setup sale/rent toggle
    private fun setupOwnershipRadioButton() {
        radio_group_ownership_type?.run {
            this.rb_room_rental?.visibility =
                View.GONE // Exclude room rental as ownership type
            this.onSelectOwnershipListener = { ownershipType ->
                viewModel.ownershipType.postValue(ownershipType)
            }
        }
    }

    private fun setViewPagerHeight() {
        card_transaction_header?.let { header ->
            val layoutParams = layout_group_transactions_lists.view_pager.layoutParams
            val tabHeight = layout_group_transactions_lists.tab_layout.height
            val headerHeight = header.height
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_l)
            val viewPagerHeight = when (isShowProjects()) {
                true -> ViewUtil.getScreenHeight(this) - tabHeight - margin * 5 - headerHeight + bottomMargin
                false -> ViewUtil.getScreenHeight(this) - margin * 4 - headerHeight + bottomMargin
            }
            layoutParams.height = viewPagerHeight
            layout_group_transactions_lists.view_pager.layoutParams = layoutParams
        } ?: run {
            val layoutParams = layout_group_transactions_lists.view_pager.layoutParams
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_l)
            val viewPagerHeight = ViewUtil.getScreenHeight(this) - margin * 3 + bottomMargin
            layoutParams.height = viewPagerHeight
            layout_group_transactions_lists.view_pager.layoutParams = layoutParams
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setViewPagerHeight() // Why here? Because you get view height as 0dp else where
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_FILTER_TRANSACTIONS -> {
                if (resultCode == Activity.RESULT_OK) {
                    applyExternalFilter(data)
                }
            }
            REQUEST_CODE_SEARCH -> {
                if (resultCode == Activity.RESULT_OK) {
                    finish()
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun applyExternalFilter(data: Intent?) {
        if (viewModel.entryType.value != SearchEntryType.QUERY_PROPERTY_SUB_TYPE) {
            val propertyMainType =
                data?.getSerializableExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
            propertyMainType?.run { viewModel.propertyMainType.postValue(this) }
            val propertySubTypesString =
                data?.getStringExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_PROPERTY_SUB_TYPES)
            val propertySubTypes =
                PropertyTypeUtil.getPropertySubTypes(propertySubTypesString ?: "")
            viewModel.propertySubTypes.postValue(propertySubTypes)
        }
        viewModel.externalRadius =
            data?.getSerializableExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_RADIUS) as TransactionEnum.Radius?
        viewModel.externalAreaType =
            data?.getSerializableExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_AREA_TYPE) as TransactionEnum.AreaType?
        viewModel.externalTenureType =
            data?.getSerializableExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_TENURE_TYPE) as TransactionEnum.TenureType?
        viewModel.externalMinSize = NumberUtil.getIntOrNullFromExtra(
            data,
            FilterTransactionActivity.EXTRA_KEY_OUTPUT_MIN_BUILT_SIZE
        )
        viewModel.externalMaxSize = NumberUtil.getIntOrNullFromExtra(
            data,
            FilterTransactionActivity.EXTRA_KEY_OUTPUT_MAX_BUILT_SIZE
        )
        viewModel.externalMinAge = NumberUtil.getIntOrNullFromExtra(
            data,
            FilterTransactionActivity.EXTRA_KEY_OUTPUT_MIN_PROPERTY_AGE
        )
        viewModel.externalMaxAge = NumberUtil.getIntOrNullFromExtra(
            data,
            FilterTransactionActivity.EXTRA_KEY_OUTPUT_MAX_PROPERTY_AGE
        )
        requestLoadGroupTransactions()
    }

    private fun requestLoadGroupTransactions() {
        RxBus.publish(RequestLoadGroupTransactionsEvent())
    }

    companion object {
        const val REQUEST_CODE_FILTER_TRANSACTIONS = 1
        const val REQUEST_CODE_SEARCH = 2
        const val BATCH_SIZE_GLOBAL_FIRST_FIFTY = 50
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_group_transactions
    }

    override fun getViewModelClass(): Class<GroupTransactionsViewModel> {
        return GroupTransactionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}