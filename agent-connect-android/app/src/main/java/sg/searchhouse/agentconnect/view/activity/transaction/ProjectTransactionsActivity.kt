package sg.searchhouse.agentconnect.view.activity.transaction

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_project_transactions.*
import kotlinx.android.synthetic.main.card_transaction_header.*
import kotlinx.android.synthetic.main.layout_project_rent_transactions_lists.view.*
import kotlinx.android.synthetic.main.layout_project_sale_transactions_lists.view.*
import kotlinx.android.synthetic.main.layout_project_sale_transactions_lists.view.tab_layout
import kotlinx.android.synthetic.main.layout_project_transactions_summary.*
import kotlinx.android.synthetic.main.radio_group_ownership.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityProjectTransactionsBinding
import sg.searchhouse.agentconnect.dsl.getIntExtraOrNull
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.TransactionsDisplayMode
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.transaction.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectInfoActivity
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectRentTransactionPagerAdapter
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectSaleTransactionPagerAdapter
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry
import sg.searchhouse.agentconnect.view.helper.search.TransactionsActivityEntry
import sg.searchhouse.agentconnect.viewmodel.activity.transaction.ProjectTransactionsViewModel

// Launch from SearchResultActivityEntry#launchByProject
class ProjectTransactionsActivity :
    ViewModelActivity<ProjectTransactionsViewModel, ActivityProjectTransactionsBinding>() {
    private lateinit var ownershipType: ListingEnum.OwnershipType
    private lateinit var propertyPurpose: ListingEnum.PropertyPurpose

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
        setupExtras()
        setListeners()
        setupViews()
        listenRxBuses()
        handleSavedInstanceState(savedInstanceState)
    }

    // To fix bug where non-display view pager missing after rotate from landscape back to portrait
    // e.g. rental view pager went missing if currently selected sale view pager
    // Restart activity mitigate this issue
    private fun handleSavedInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.run {
            // Entry from rotation
            val orientation = getInt(INSTANCE_STATE_ORIENTATION)
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                relaunchActivity()
            }
        } ?: run {
            // Fresh entry
            setupOptionalExtras()
        }
    }

    private fun relaunchActivity() {
        val projectId = viewModel.projectId.value ?: return
        val projectName = viewModel.projectName.value ?: return
        val projectDescription = viewModel.projectDescription.value ?: return
        val isShowTower = viewModel.isShowTower.value ?: return
        val propertySubType = viewModel.propertySubType.value ?: return
        val ownershipType = viewModel.ownershipType.value ?: return

        val defaultTabPosition = when (ownershipType) {
            ListingEnum.OwnershipType.SALE -> getSaleTabPosition()
            ListingEnum.OwnershipType.RENT -> getRentTabPosition()
            else -> 0
        }

        TransactionsActivityEntry.launchByProject(
            this,
            projectId = projectId,
            projectName = projectName,
            projectDescription = projectDescription,
            isShowTower = isShowTower,
            propertySubType = propertySubType,
            propertyPurpose = propertyPurpose,
            ownershipType = ownershipType,
            minAge = viewModel.externalMinAge,
            maxAge = viewModel.externalMaxAge,
            minSize = viewModel.externalMinSize,
            maxSize = viewModel.externalMaxSize,
            areaType = viewModel.externalAreaType,
            tenureType = viewModel.externalTenureType,
            defaultTabPosition = defaultTabPosition
        )
        finish()
    }

    private fun listenRxBuses() {
        listenRxBus(UnlockActivityScrollEvent::class.java) {
            viewModel.isLockScrollView.postValue(false)
        }
        listenRxBus(RequestLoadProjectTransactionsEvent::class.java) {
            loadingAndPerformRequest()
        }
        listenRxBus(RequestActivityLoadProjectTransactionsEvent::class.java) {
            requestLoadProjectTransactions()
        }
        listenRxBus(SendProjectTransactionsCsvEvent::class.java) {
            viewModel.exportTransactions(it.content)
        }
    }

    private fun loadingAndPerformRequest() {
        // Load transactions lists
        RxBus.publish(
            UpdateProjectTransactionsEvent(
                emptyList(),
                ApiStatus.StatusKey.LOADING,
                UpdateProjectTransactionsEvent.ProjectTransactionType.ANY
            )
        )
        viewModel.performRequest()
    }

    private fun setupViews() {
        setupOwnershipRadioButton()
        setupContainerTransition()
    }

    private fun setupContainerTransition() {
        layout_container?.layoutTransition?.enableTransitionType(
            LayoutTransition.CHANGE_DISAPPEARING
        )
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

    override fun onBackPressed() {
        when (viewModel.displayMode.value) {
            TransactionsDisplayMode.MAP -> viewModel.displayMode.postValue(
                TransactionsDisplayMode.LIST
            )
            else -> super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INSTANCE_STATE_ORIENTATION, resources.configuration.orientation)
    }

    private fun setupViewPager(projectId: Int) {
        setupSaleViewPager(projectId)
        setupRentalViewPager(projectId)
        val defaultTabPosition = intent.extras?.getInt(EXTRA_DEFAULT_TAB_POSITION) ?: 0
        when (ownershipType) {
            ListingEnum.OwnershipType.SALE -> {
                layout_sale_transactions_lists.view_pager_sale.currentItem = defaultTabPosition
            }
            ListingEnum.OwnershipType.RENT -> {
                layout_rent_transactions_lists.view_pager_rental.currentItem = defaultTabPosition
            }
            else -> {
            }
        }
    }

    private fun setupSaleViewPager(projectId: Int) {
        val propertySubType =
            viewModel.propertySubType.value ?: throw IllegalArgumentException("sub type absent!")
        layout_sale_transactions_lists.view_pager_sale.adapter =
            ProjectSaleTransactionPagerAdapter(
                this,
                projectId,
                supportFragmentManager,
                isShowTower(),
                propertySubType
            )
        if (isShowTower()) {
            // Without tower, no need to tie view pager with tabs
            layout_sale_transactions_lists.tab_layout.setupWithViewPager(
                layout_sale_transactions_lists.view_pager_sale
            ) {
                scroll_view?.run {
                    if (scrollY > 0) {
                        viewModel.isLockScrollView.postValue(true)
                    }
                }
                viewModel.tabPositionSale.postValue(layout_sale_transactions_lists.view_pager_sale.currentItem)
                setSaleTabPosition(layout_sale_transactions_lists.view_pager_sale.currentItem)
            }
        }
    }

    // Setup sale/rent toggle
    private fun setupOwnershipRadioButton() {
        radio_group_ownership_type?.rb_room_rental?.visibility =
            View.GONE // Exclude room rental as ownership type
        radio_group_ownership_type?.onSelectOwnershipListener = { ownershipType ->
            viewModel.ownershipType.postValue(ownershipType)
        }
    }

    private fun setupRentalViewPager(projectId: Int) {
        val propertySubType =
            viewModel.propertySubType.value ?: throw IllegalArgumentException("sub type absent!")
        layout_rent_transactions_lists.view_pager_rental.adapter =
            ProjectRentTransactionPagerAdapter(
                this,
                projectId,
                supportFragmentManager,
                propertySubType
            )

        layout_rent_transactions_lists.tab_layout.setupWithViewPager(
            layout_rent_transactions_lists.view_pager_rental
        ) {
            scroll_view?.run {
                if (scrollY > 0) {
                    viewModel.isLockScrollView.postValue(true)
                }
            }
            setRentTabPosition(layout_rent_transactions_lists.view_pager_rental.currentItem)
        }
    }

    private fun observeLiveData() {
        viewModel.projectId.observeNotNull(this) {
            setupViewPager(it)
        }
        viewModel.mainStatus.observe(this, { status ->
            when (status) {
                ApiStatus.StatusKey.FAIL, ApiStatus.StatusKey.ERROR -> {
                    RxBus.publish(
                        UpdateProjectTransactionsEvent(
                            emptyList(),
                            status,
                            UpdateProjectTransactionsEvent.ProjectTransactionType.ANY
                        )
                    )
                }
                else -> {
                }
            }
        })
        viewModel.isLockScrollView.observe(this, { isLockScrollView ->
            layout_project_transactions_summary?.run {
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
        })
        viewModel.ownershipType.observe(this, { ownershipType ->
            viewModel.mainResponse.postValue(null) // Minimize view artifact, i.e. sale/rent specific formatting on summary
            radio_group_ownership_type?.setValue(ownershipType)
            requestLoadProjectTransactions()
        })
        viewModel.reportExcelLocalFileName.observeNotNull(this) {
            IntentUtil.viewCsv(
                this@ProjectTransactionsActivity,
                it,
                AppConstant.DIR_TRANSACTION_REPORT
            )
        }
    }

    private fun requestLoadProjectTransactions() {
        viewModel.getProjectTransactionRequest()?.let {
            RxBus.publish(RequestLoadProjectTransactionsEvent(it))
        }
    }

    private fun animateShowSummaryLayout() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        layout_project_transactions_summary?.startAnimation(animation)
    }

    private fun setupExtras() {
        val propertySubType =
            intent.getSerializableExtra(EXTRA_KEY_PROPERTY_SUB_TYPE) as ListingEnum.PropertySubType?
                ?: throw IllegalArgumentException("Require `EXTRA_KEY_PROPERTY_SUB_TYPE` in project transactions")
        ownershipType =
            intent.getSerializableExtra(EXTRA_KEY_OWNERSHIP_TYPE) as ListingEnum.OwnershipType?
                ?: throw IllegalArgumentException("Missing ownershipType in extras")
        propertyPurpose =
            intent.getSerializableExtra(EXTRA_KEY_PROPERTY_PURPOSE) as ListingEnum.PropertyPurpose?
                ?: throw IllegalArgumentException("Missing propertyPurpose in extras")
        val projectId =
            intent.getIntExtra(SearchResultActivityEntry.EXTRA_KEY_PROJECT_ID, -1)
        require(projectId != -1) { "Missing or invalid projectId" }
        val projectName =
            intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_PROJECT_NAME)
        val projectDescription =
            intent.getStringExtra(SearchResultActivityEntry.EXTRA_KEY_PROJECT_DESCRIPTION)
        viewModel.propertySubType.postValue(propertySubType)
        if (viewModel.ownershipType.value == null) {
            viewModel.ownershipType.postValue(ownershipType)
        }
        viewModel.projectId.postValue(projectId)
        viewModel.projectName.postValue(projectName)
        viewModel.projectDescription.postValue(projectDescription)
        viewModel.isShowTower.postValue(isShowTower())
    }

    // In case launch itself with filter
    // Part of fixing rotation view pager missing bug
    private fun setupOptionalExtras() {
        viewModel.externalMinAge = intent.getIntExtraOrNull(EXTRA_MIN_AGE)
        viewModel.externalMaxAge = intent.getIntExtraOrNull(EXTRA_MAX_AGE)
        viewModel.externalMinSize = intent.getIntExtraOrNull(EXTRA_MIN_SIZE)
        viewModel.externalMaxSize = intent.getIntExtraOrNull(EXTRA_MAX_SIZE)
        viewModel.externalAreaType =
            intent.getSerializableExtra(EXTRA_AREA_TYPE) as TransactionEnum.AreaType?
        viewModel.externalTenureType =
            intent.getSerializableExtra(EXTRA_TENURE) as TransactionEnum.TenureType?
    }

    private fun isShowTower(): Boolean {
        return true
    }

    private fun setOnClickListeners() {
        btn_map?.setOnClickListener {
            viewModel.displayMode.postValue(TransactionsDisplayMode.MAP)
        }

        btn_list?.setOnClickListener {
            viewModel.displayMode.postValue(TransactionsDisplayMode.LIST)
        }

        btn_title?.setOnClickListener {
            SearchActivity.launch(
                this,
                SearchActivity.SearchType.TRANSACTIONS,
                propertyPurpose,
                viewModel.ownershipType.value,
                SearchActivity.ExpandMode.COMPACT
            )
        }
        btn_transaction_back?.setOnClickListener {
            onBackPressed()
        }
        tv_title?.setOnLongClickListener {
            ViewUtil.showMessage(tv_title.text.toString())
            true
        }
        btn_external_link?.setOnClickListener {
            val projectId = viewModel.projectId.value ?: return@setOnClickListener
            ProjectInfoActivity.launch(this, projectId, isLimitTransactionsStack = true)
        }
        btn_filter?.setOnClickListener {
            val propertySubType = viewModel.propertySubType.value ?: return@setOnClickListener
            val propertyMainType = PropertyTypeUtil.getPropertyMainType(propertySubType.type)
                ?: return@setOnClickListener

            FilterTransactionActivity.launchByProjectId(
                this,
                GroupTransactionsActivity.REQUEST_CODE_FILTER_TRANSACTIONS,
                projectId = viewModel.projectId.value!!,
                propertyPurpose = propertyPurpose,
                ownershipType = ownershipType,
                previousPropertyMainType = propertyMainType,
                previousPropertySubTypes = propertySubType.type.toString(),
                previousAreaType = viewModel.externalAreaType,
                previousTenureType = viewModel.externalTenureType,
                previousMinBuiltSize = viewModel.externalMinSize,
                previousMaxBuiltSize = viewModel.externalMaxSize,
                previousMinPropertyAge = viewModel.externalMinAge,
                previousMaxPropertyAge = viewModel.externalMaxAge
            )
        }
        btn_export_transactions?.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.TRANSACTION_SEARCH_EXPORT,
                onSuccessAccessibility = { exportTransactions() },
                onLimitedAccessibility = { exportTransactions(isLimited = true) }
            )
        }
    }

    private fun exportTransactions(isLimited: Boolean = false) {
        RxBus.publish(RequestProjectTransactionsCsvEvent(isLimited = isLimited))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_FILTER_TRANSACTIONS -> {
                if (resultCode == Activity.RESULT_OK) {
                    applyExternalFilter(data)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun applyExternalFilter(data: Intent?) {
        viewModel.externalPropertyMainType =
            data?.getSerializableExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_PROPERTY_MAIN_TYPE) as ListingEnum.PropertyMainType?
        viewModel.externalPropertySubTypes =
            data?.getStringExtra(FilterTransactionActivity.EXTRA_KEY_OUTPUT_PROPERTY_SUB_TYPES)
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

        viewModel.getProjectTransactionRequest()?.let {
            RxBus.publish(RequestLoadProjectTransactionsEvent(it))
        }
    }

    private fun setViewPagerHeight() {
        setSaleViewPagerHeight()
        setRentalViewPagerHeight()
    }

    private fun setSaleViewPagerHeight() {
        card_transaction_header?.let { header ->
            val layoutParams = layout_sale_transactions_lists.view_pager_sale.layoutParams
            val tabHeight = layout_sale_transactions_lists.tab_layout.height
            val headerHeight = header.height
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_l)
            val viewPagerHeight = when (isShowTower()) {
                true -> ViewUtil.getScreenHeight(this) - tabHeight - margin * 5 - headerHeight + bottomMargin
                false -> ViewUtil.getScreenHeight(this) - margin * 4 - headerHeight + bottomMargin
            }
            layoutParams.height = viewPagerHeight
            layout_sale_transactions_lists.view_pager_sale.layoutParams = layoutParams
        } ?: run {
            val layoutParams = layout_sale_transactions_lists.view_pager_sale.layoutParams
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_l)
            val viewPagerHeight = ViewUtil.getScreenHeight(this) - margin * 3 + bottomMargin
            layoutParams.height = viewPagerHeight
            layout_sale_transactions_lists.view_pager_sale.layoutParams = layoutParams
        }
    }

    private fun setRentalViewPagerHeight() {
        card_transaction_header?.let { header ->
            // Portrait
            val layoutParams = layout_rent_transactions_lists.view_pager_rental.layoutParams
            val tabHeight = layout_rent_transactions_lists.tab_layout.height
            val headerHeight = header.height
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_l)
            val viewPagerHeight = if (viewModel.isHdb.value != true) {
                ViewUtil.getScreenHeight(this) - tabHeight - margin * 5 - headerHeight + bottomMargin
            } else {
                ViewUtil.getScreenHeight(this) - tabHeight - margin * 4 - headerHeight + bottomMargin
            }
            layoutParams.height = viewPagerHeight
            layout_rent_transactions_lists.view_pager_rental.layoutParams = layoutParams
        } ?: run {
            // Landscape
            val layoutParams = layout_rent_transactions_lists.view_pager_rental.layoutParams
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_l)
            val viewPagerHeight = ViewUtil.getScreenHeight(this) - margin * 3 + bottomMargin
            layoutParams.height = viewPagerHeight
            layout_rent_transactions_lists.view_pager_rental.layoutParams = layoutParams
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setViewPagerHeight() // Why here? Because you get view height as 0dp else where
    }

    private fun getSaleTabPosition(): Int {
        return Prefs.getInt(PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_SALE, 0)
    }

    private fun getRentTabPosition(): Int {
        return Prefs.getInt(PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_RENT, 0)
    }

    private fun setSaleTabPosition(position: Int) {
        return Prefs.putInt(PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_SALE, position)
    }

    private fun setRentTabPosition(position: Int) {
        return Prefs.putInt(PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_RENT, position)
    }

    companion object {
        const val INSTANCE_STATE_ORIENTATION = "INSTANCE_STATE_ORIENTATION"

        private const val PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_SALE =
            "PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_SALE"
        private const val PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_RENT =
            "PREFS_PROJECT_TRANSACTIONS_TAB_POSITION_RENT"

        const val EXTRA_KEY_OWNERSHIP_TYPE = "EXTRA_KEY_OWNERSHIP_TYPE"
        const val EXTRA_KEY_PROPERTY_PURPOSE = "EXTRA_KEY_PROPERTY_PURPOSE"
        const val EXTRA_KEY_PROPERTY_SUB_TYPE = "EXTRA_KEY_PROPERTY_SUB_TYPE"

        const val EXTRA_AREA_TYPE = "EXTRA_AREA_TYPE"
        const val EXTRA_MIN_AGE = "EXTRA_MIN_AGE"
        const val EXTRA_MAX_AGE = "EXTRA_MAX_AGE"
        const val EXTRA_MIN_SIZE = "EXTRA_MIN_SIZE"
        const val EXTRA_MAX_SIZE = "EXTRA_MAX_SIZE"
        const val EXTRA_TENURE = "EXTRA_TENURE"

        const val EXTRA_DEFAULT_TAB_POSITION = "EXTRA_DEFAULT_TAB_POSITION"

        const val REQUEST_CODE_FILTER_TRANSACTIONS = 1
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_project_transactions
    }

    override fun getViewModelClass(): Class<ProjectTransactionsViewModel> {
        return ProjectTransactionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}