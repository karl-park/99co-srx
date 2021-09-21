package sg.searchhouse.agentconnect.view.activity.listing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_my_listings.*
import kotlinx.android.synthetic.main.dialog_text_box.view.*
import kotlinx.android.synthetic.main.layout_my_listings_fab_add_on.view.*
import kotlinx.android.synthetic.main.layout_my_listings_smart_filter.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityMyListingsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.showToast
import sg.searchhouse.agentconnect.dsl.widget.addOnOffsetChangedListener
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.listing.create.NotifyPostListingEvent
import sg.searchhouse.agentconnect.event.listing.user.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.listing.user.ListingGroupSummaryPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.cea.CeaExclusiveMainSectionsActivity
import sg.searchhouse.agentconnect.view.activity.listing.user.FeaturesCreditApplicationActivity
import sg.searchhouse.agentconnect.view.adapter.listing.user.MyListingsPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting.CertifiedListingDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.create.ListingAddressSearchDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.create.PostListingDialogFragment
import sg.searchhouse.agentconnect.view.widget.common.ClickableTextView
import sg.searchhouse.agentconnect.view.widget.listing.MyListingsFabAddOn.AddOnFab
import sg.searchhouse.agentconnect.viewmodel.activity.listing.MyListingsViewModel

//TODO: to refine this file in future
class MyListingsActivity : ViewModelActivity<MyListingsViewModel, ActivityMyListingsBinding>() {

    private lateinit var pagerAdapter: MyListingsPagerAdapter

    private var selectedListings = listOf<ListingPO>()

    //Note: listing id and type
    private var selectedListingIds = listOf<String>()
    private var selectedFormIds = arrayListOf<String>()

    val handler = Handler()
    val runnable = Runnable {
        val inputtedText = et_active_listings.edit_text.text
        if (inputtedText != null) {
            viewModel.searchText.postValue(inputtedText.toString())
        }
    }

    private var dialogProgress: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableToolBarTitle()
        setupViewPager()
        setOnClickListeners()
        observeLiveData()
        listenRxBuses()
        setupExtras()
    }

    private fun disableToolBarTitle() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupExtras() {
        val ownershipType =
            intent.extras?.getSerializable(EXTRA_KEY_OWNERSHIP_TYPE) as ListingEnum.OwnershipType?
        viewModel.ownershipType.postValue(ownershipType ?: ListingEnum.OwnershipType.SALE)

        intent.extras?.getInt(EXTRA_KEY_PROPERTY_PRIMARY_SUB_TYPE)?.run {
            if (this > 0) {
                viewModel.handlePropertyPrimarySubType(primarySubType = this)
            }
        }
    }

    private fun listenRxBuses() {
        listenRxBus(LaunchListingDetailEvent::class.java) {
            ListingDetailsActivity.launch(
                activity = this,
                listingId = it.listingId,
                listingType = it.listingType,
                launchMode = ListingDetailsActivity.LaunchMode.EDIT,
                listingGroup = it.listingGroup
            )
        }

        listenRxBus(LaunchEditListingEvent::class.java) { showPostListingDialog(it.listingId) }

        listenRxBus(LaunchCeaExclusiveForm::class.java) { event ->
            val formType = CeaExclusiveEnum.CeaFormType.values().find { it.value == event.formType }
                ?: return@listenRxBus
            CeaExclusiveMainSectionsActivity.launch(this, formType, event.formId)
        }

        listenRxBus(IsSelectionApplicableEvent::class.java) {
            viewModel.isSelectionApplicable.postValue(it.isApplicable)
        }

        listenRxBus(LaunchCreateListingEvent::class.java) {
            if (it.isFromMyListing) {
                launchCreateListing()
            }
        }

        listenRxBus(UpdateDraftCeaFormsEvent::class.java) { viewModel.performRequest() }

        listenRxBus(NotifyPostListingEvent::class.java) { viewModel.performRequest() }

        listenRxBus(UpdateSelectionOptionEvent::class.java) {
            viewModel.selectAllAction.postValue(
                Pair(
                    it.option,
                    it.isUpdateSelectedLists
                )
            )
        }

        listenRxBus(SelectedCeaFormIdsToRemove::class.java) {
            selectedFormIds.clear()
            selectedFormIds.addAll(it.formIds)
            viewModel.showAddOn.value = selectedFormIds.isNotEmpty()
        }

        listenRxBus(NotifySelectedListingsEvent::class.java) { event ->
            selectedListings = event.listings
            selectedListingIds = event.listings.map { it.getListingIdType() }
            handleAddOnOptions()
        }

        listenRxBus(SelectFeatureAddOnOptionEvent::class.java) { setupFeatureAddOnOptions(it.option) }
    }

    private fun setupFeatureAddOnOptions(option: AddOnFab) {
        //Hide overlay
        binding.layoutMyListingAddOn.showSubAddOnFloatingActionButtons(false)
        when (option) {
            AddOnFab.CERTIFIED_LISTING -> {
                AuthUtil.checkModuleAccessibility(
                    module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                    function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_CERTIFIED_LISTINGS,
                    onSuccessAccessibility = { showCertifiedListing() }
                )
            }
            AddOnFab.FEATURED_LISTING -> {
                AuthUtil.checkModuleAccessibility(
                    module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                    function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_FEATURE_LISTINGS,
                    onSuccessAccessibility = {
                        launchAddFeatures(ListingManagementEnum.SrxCreditMainType.FEATURED_LISTING)
                    })
            }
            AddOnFab.V360 -> {
                AuthUtil.checkModuleAccessibility(
                    module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                    function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_V360,
                    onSuccessAccessibility = {
                        launchAddFeatures(creditType = ListingManagementEnum.SrxCreditMainType.V360)
                    })
            }
            AddOnFab.X_DRONE -> {
                AuthUtil.checkModuleAccessibility(
                    module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                    function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_X_DRONE,
                    onSuccessAccessibility = {
                        launchAddFeatures(creditType = ListingManagementEnum.SrxCreditMainType.DRONE)
                    })

            }
            AddOnFab.REFRESH, AddOnFab.REPOST -> {
                onClickRefreshOrRepost()
            }
            AddOnFab.TAKE_DOWN -> {
                showDialogToTakeDownListing()
            }
            AddOnFab.DELETE -> {
                if (viewModel.draftMode.value == ListingManagementEnum.ListingDraftMode.CEA_FORMS &&
                    selectedFormIds.isNotEmpty()
                ) {
                    showDialogToDeleteDraftCeaForms()
                } else if (selectedListingIds.isNotEmpty()) {
                    showDialogToDeleteListing()
                }
            }
            AddOnFab.COPY -> {
                val listingId =
                    viewModel.getSelectedListingIds(selectedListingIds).firstOrNull() ?: return
                dialogProgress =
                    dialogUtil.showProgressDialog(R.string.dialog_message_copying_listing)
                viewModel.getListingManagementListing(listingId)
            }
            AddOnFab.COMMUNITY_POST -> maybeVisitSponsorListing()
        }
    }

    private fun onClickRefreshOrRepost() {
        when (SessionUtil.getSubscriptionConfig().srx99CombinedSub) {
            true -> {
                // TODO Maybe show progress bar
                val listingIds = selectedListings.map { it.id }
                viewModel.performGetPostingsCredits(listingIds, onSuccess = {
                    showDialogToRepostListing(getConfirmRefreshListingMessage(it))
                }, onFail = {
                    R.string.toast_get_refresh_cost_failed.showToast()
                })
            }
            false -> showDialogToRepostListing(getConfirmRepostListingMessage())
        }
    }

    private fun maybeVisitSponsorListing() {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
            onSuccessAccessibility = {
                val listingId =
                    viewModel.getSelectedListingIds(selectedListingIds).firstOrNull()?.toIntOrNull()
                        ?: return@checkModuleAccessibility
                SponsorListingActivity.launchForResult(this, listingId, REQUEST_CODE_SPONSOR)
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SPONSOR -> {
                RxBus.publish(NotifyPostListingEvent())
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.searchText.observe(this) { searchText ->
            if (searchText != null) {
                updateListingsByOrderCriteriaAndFilters(
                    orderCriteria = viewModel.orderCriteria.value,
                    propertyMainType = viewModel.propertyMainType.value,
                    propertySubType = viewModel.propertySubType.value,
                    propertyAge = viewModel.propertyAge.value,
                    bedrooms = viewModel.propertyBedRoom.value,
                    tenure = viewModel.propertyTenure.value,
                    searchText = searchText
                )
            } else {
                et_active_listings.clearEditText()
            }
        }

        viewModel.draftMode.observeNotNull(this) {
            viewModel.isShowSmartFilter.postValue(it != ListingManagementEnum.ListingDraftMode.CEA_FORMS)
            RxBus.publish(UpdateDraftModeEvent(it))
        }

        viewModel.ceaOwnershipType.observeNotNull(this) {
            RxBus.publish(UpdateOwnershipTypeEvent(it, isDraftCeaOwnershipType = true))
        }

        viewModel.ownershipType.observeNotNull(this) {
            RxBus.publish(UpdateOwnershipTypeEvent(it))
            if (viewModel.listingGroups.value != null) {
                //reset search text
                viewModel.searchText.value = null
                viewModel.setupSmartFilterOptions()
            }
        }

        viewModel.orderCriteria.observeNotNull(this) {
            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = it,
                propertyMainType = viewModel.propertyMainType.value,
                propertySubType = viewModel.propertySubType.value,
                propertyAge = viewModel.propertyAge.value,
                bedrooms = viewModel.propertyBedRoom.value,
                tenure = viewModel.propertyTenure.value,
                searchText = viewModel.searchText.value
            )
        }

        viewModel.summary.observeNotNull(this) {
            updateOwnershipRadioGroup(it, tab_layout_listing_group.selectedTabPosition)

            //update cea sale and rent count
            val ceaListingGroup =
                it.find { group -> group.id == ListingManagementEnum.ListingGroup.CEA_FORM.value }
                    ?: return@observeNotNull
            updateCeaOwnershipRadioGroup(ceaListingGroup)

            //update draft mode tab layout
            updateDraftModeTabLayout(it, tab_layout_listing_group.selectedTabPosition)
        }

        viewModel.selectAllAction.observeNotNull(this) {
            //Note: First => select all option Second => Flag need to whether need to notify MyListingSelectAllEvent or not
            btn_select_all.setText(
                when (it.first) {
                    MyListingSelectAllEvent.Option.SELECT_ALL -> R.string.button_unselect_all
                    else -> R.string.button_select_all
                }
            )
            if (it.second) {
                RxBus.publish(MyListingSelectAllEvent(it.first))
            }
        }

        viewModel.apiCallStatus.observeNotNull(this) { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val response = apiStatus.body ?: return@observeNotNull
                    if (viewModel.draftMode.value == ListingManagementEnum.ListingDraftMode.CEA_FORMS) {
                        RxBus.publish(UpdateDraftCeaFormsEvent())
                    } else {
                        //can use post listing event since it's also repost listings
                        RxBus.publish(NotifyPostListingEvent())
                    }

                    //reset unselect all listings and update selection lists
                    viewModel.selectAllAction.postValue(
                        Pair(
                            MyListingSelectAllEvent.Option.UNSELECT_ALL,
                            true
                        )
                    )
                    ViewUtil.showMessage(response.result)
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        }

        //SMART FILTER OPTIONS
        viewModel.listingGroups.observeNotNull(this) {
            //reset search text
            viewModel.searchText.value = null
            viewModel.setupSmartFilterOptions()
        }

        viewModel.propertyMainType.observeNotNull(this) {
            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = viewModel.orderCriteria.value,
                propertyMainType = it,
                propertySubType = viewModel.propertySubType.value,
                propertyAge = viewModel.propertyAge.value,
                bedrooms = viewModel.propertyBedRoom.value,
                tenure = viewModel.propertyTenure.value,
                searchText = viewModel.searchText.value
            )
            viewModel.handleSecondLevelFilterOptions()
        }

        viewModel.propertySubType.observeNotNull(this) {
            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = viewModel.orderCriteria.value,
                propertyMainType = viewModel.propertyMainType.value,
                propertySubType = it,
                propertyAge = viewModel.propertyAge.value,
                bedrooms = viewModel.propertyBedRoom.value,
                tenure = viewModel.propertyTenure.value,
                searchText = viewModel.searchText.value
            )
            when (viewModel.propertyMainType.value) {
                ListingEnum.PropertyMainType.COMMERCIAL -> {
                    layout_filter_option_container.removeAllViews()
                }
                else -> {
                    viewModel.handleThirdLevelFilterOptions()
                }
            }
        }

        viewModel.propertyAge.observeNotNull(this) {
            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = viewModel.orderCriteria.value,
                propertyMainType = viewModel.propertyMainType.value,
                propertySubType = viewModel.propertySubType.value,
                propertyAge = it,
                bedrooms = viewModel.propertyBedRoom.value,
                tenure = viewModel.propertyTenure.value,
                searchText = viewModel.searchText.value
            )
            when (viewModel.propertyMainType.value) {
                ListingEnum.PropertyMainType.LANDED -> {
                    viewModel.findLandedTenureCounts()
                }
                else -> {
                    layout_filter_option_container.removeAllViews()
                }
            }
        }

        viewModel.propertyBedRoom.observeNotNull(this) {
            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = viewModel.orderCriteria.value,
                propertyMainType = viewModel.propertyMainType.value,
                propertySubType = viewModel.propertySubType.value,
                propertyAge = viewModel.propertyAge.value,
                bedrooms = it,
                tenure = viewModel.propertyTenure.value,
                searchText = viewModel.searchText.value
            )
            viewModel.handleThirdLevelFilterOptions()
        }

        viewModel.propertyTenure.observeNotNull(this) {
            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = viewModel.orderCriteria.value,
                propertyMainType = viewModel.propertyMainType.value,
                propertySubType = viewModel.propertySubType.value,
                propertyAge = viewModel.propertyAge.value,
                bedrooms = viewModel.propertyBedRoom.value,
                tenure = it,
                searchText = viewModel.searchText.value
            )
            layout_filter_option_container.removeAllViews()
        }

        viewModel.propertyMainTypeCounts.observeNotNull(this) {
            if (viewModel.tempPropertyMainType.value == null) {
                //no need to show property main types if select specific main type from dashboard
                populatePropertyMainTypes(it)
            }
        }
        viewModel.propertySubTypeCounts.observeNotNull(this) { populatePropertySubTypes(it) }
        viewModel.builtYearCounts.observeNotNull(this) { populateBuiltYears(it) }
        viewModel.condoBedroomCounts.observeNotNull(this) { populateCondoBedRooms(it) }
        viewModel.condoBedroomTenureCounts.observeNotNull(this) { populateTenures(it) }
        viewModel.landedTenureCounts.observeNotNull(this) { populateTenures(it) }

        viewModel.clearFilterOptions.observeNotNull(this) { filterOption ->
            var tempPropertyMainType = viewModel.propertyMainType.value
            var tempPropertySubType = viewModel.propertySubType.value
            var tempPropertyAge = viewModel.propertyAge.value
            var tempBedRoomCount = viewModel.propertyBedRoom.value
            var tempTenure = viewModel.propertyTenure.value

            when (filterOption) {
                is ListingEnum.PropertyMainType -> tempPropertyMainType = null
                is ListingEnum.PropertySubType -> tempPropertySubType = null
                is ListingEnum.PropertyAge -> tempPropertyAge = null
                is ListingEnum.BedroomCount -> tempBedRoomCount = null
                is ListingEnum.Tenure -> tempTenure = null
            }

            updateListingsByOrderCriteriaAndFilters(
                orderCriteria = viewModel.orderCriteria.value,
                propertyMainType = tempPropertyMainType,
                propertySubType = tempPropertySubType,
                propertyAge = tempPropertyAge,
                bedrooms = tempBedRoomCount,
                tenure = tempTenure,
                searchText = viewModel.searchText.value
            )
        }

        viewModel.getListingResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    dialogProgress?.dismiss()
                    ViewUtil.showMessage(it.error?.error)
                }
                ApiStatus.StatusKey.ERROR -> {
                    dialogProgress?.dismiss()
                }
                else -> {
                    //Do nothing
                }
            }
        }

        viewModel.copyListingResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    dialogProgress?.dismiss()
                    val listingId = it.body?.listingEditPO?.id?.toString() ?: return@observeNotNull
                    showPostListingDialog(listingId)
                }
                ApiStatus.StatusKey.FAIL -> {
                    dialogProgress?.dismiss()
                    ViewUtil.showMessage(it.error?.error)
                }
                ApiStatus.StatusKey.ERROR -> {
                    dialogProgress?.dismiss()
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun populatePropertyMainTypes(mainTypeCounts: Map<ListingEnum.PropertyMainType, Int>) {
        layout_filter_option_container.removeAllViews()
        //Note: by requirement, show all lists even count is zero for first level
        val occupiedLists = mainTypeCounts.toSortedMap()
        if (occupiedLists.isNotEmpty()) {
            occupiedLists.map { item ->
                val propertyMainType = item.key
                val clickableTextView =
                    AppCompatTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text =
                    getString(propertyMainType.labelCount, NumberUtil.formatThousand(item.value))
                clickableTextView.setCompoundDrawablesWithIntrinsicBounds(
                    propertyMainType.iconResId,
                    0,
                    0,
                    0
                )
                clickableTextView.setOnClickListener {
                    viewModel.propertyMainType.postValue(
                        propertyMainType
                    )
                }
                layout_filter_option_container.addView(clickableTextView)
            }
        }
    }

    private fun populatePropertySubTypes(propertySubTypeCounts: Map<ListingEnum.PropertySubType, Int>) {
        layout_filter_option_container.removeAllViews()
        val occupiedList = propertySubTypeCounts.filter { it.value > 0 }.toSortedMap()
        if (occupiedList.isNotEmpty()) {
            occupiedList.map { item ->
                val propertySubType = item.key
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text =
                    getString(propertySubType.labelWithCount, NumberUtil.formatThousand(item.value))
                clickableTextView.setOnClickListener {
                    viewModel.propertySubType.postValue(propertySubType)
                }
                layout_filter_option_container.addView(clickableTextView)
            }
        }
    }

    private fun populateBuiltYears(builtYearCounts: Map<ListingEnum.PropertyAge, Int>) {
        layout_filter_option_container.removeAllViews()
        val occupiedList = builtYearCounts.filter { it.value > 0 }.toSortedMap()
        if (occupiedList.isNotEmpty()) {
            occupiedList.map { item ->
                val propertyAge = item.key
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text =
                    getString(propertyAge.labelWithCount, NumberUtil.formatThousand(item.value))
                clickableTextView.setOnClickListener {
                    viewModel.propertyAge.postValue(propertyAge)
                }
                layout_filter_option_container.addView(clickableTextView)
            }
        }
    }

    private fun populateCondoBedRooms(condoBedRooms: Map<ListingEnum.BedroomCount, Int>) {
        layout_filter_option_container.removeAllViews()
        val occupiedList = condoBedRooms.filter { it.value > 0 }.toSortedMap()
        if (occupiedList.isNotEmpty()) {
            occupiedList.map { item ->
                val bedroomCount = item.key
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text = when {
                    bedroomCount.value < 6 -> getString(
                        R.string.bed_room_count_with_count,
                        bedroomCount.value,
                        NumberUtil.formatThousand(item.value)
                    )
                    else -> getString(
                        R.string.bed_room_count_more_than_with_count,
                        bedroomCount.value,
                        NumberUtil.formatThousand(item.value)
                    )
                }
                clickableTextView.setOnClickListener {
                    viewModel.propertyBedRoom.postValue(bedroomCount)
                }
                layout_filter_option_container.addView(clickableTextView)
            }
        }
    }

    private fun populateTenures(tenures: Map<ListingEnum.Tenure, Int>) {
        layout_filter_option_container.removeAllViews()
        val occupiedList = tenures.filter { it.value > 0 }.toSortedMap()
        if (occupiedList.isNotEmpty()) {
            occupiedList.map { item ->
                val tenure = item.key
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text =
                    getString(tenure.labelWithCount, NumberUtil.formatThousand(item.value))
                clickableTextView.setOnClickListener {
                    viewModel.propertyTenure.postValue(tenure)
                }
                layout_filter_option_container.addView(clickableTextView)
            }
        }
    }

    private fun updateListingsByOrderCriteriaAndFilters(
        orderCriteria: ListingManagementEnum.OrderCriteria?,
        propertyMainType: ListingEnum.PropertyMainType?,
        propertySubType: ListingEnum.PropertySubType?,
        propertyAge: ListingEnum.PropertyAge?,
        bedrooms: ListingEnum.BedroomCount?,
        tenure: ListingEnum.Tenure?,
        searchText: String?
    ) {
        RxBus.publish(
            UpdateOrderFilterOptionsEvent(
                orderCriteria = orderCriteria,
                propertyMainType = propertyMainType,
                propertySubTypes = propertySubType,
                propertyAge = propertyAge,
                bedrooms = bedrooms,
                tenure = tenure,
                searchText = searchText
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_listings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_item_create -> {
                launchCreateListing()
                true
            }
            R.id.menu_item_search -> {
                viewModel.showSearchEditText.postValue(true)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun launchCreateListing() {
        // Bug fix: Caused by java.lang.IllegalStateException, Can not perform this action after onSaveInstanceState
        // https://console.firebase.google.com/u/0/project/sg-srx-agentconnect/crashlytics/app/android:sg.searchhouse.agentconnect/issues/db8f1fa0dcd5625e1561b4249621cb46
        // Reference: https://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
        // The method `launchCreateListing()` is launched from listenRxBus, which sometimes called after activity destroyed, which causes crash
        // Not 100% sure, hence gotta observe if this check would fix the issue
        // TODO Generalize if it worked
        if (LifecycleUtil.isLaunchFragmentSafe(lifecycle)) {
            ListingAddressSearchDialogFragment.launch(
                fragmentManager = supportFragmentManager,
                addressSearchSource = ListingManagementEnum.AddressSearchSource.MAIN_SCREEN,
                ownershipType = viewModel.ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                createListingTracker = CreateListingTrackerPO(DateTimeUtil.getCurrentTimeInMillis())
            )
        }
    }

    private fun showCertifiedListing() {
        if (LifecycleUtil.isLaunchFragmentSafe(lifecycle) && selectedListingIds.size == 1) {
            CertifiedListingDialogFragment.launch(
                fragmentManager = supportFragmentManager,
                listingIdType = selectedListingIds.first()
            )
        }
    }

    private fun getConfirmRepostListingMessage(): String {
        val listingCountString = this.resources.getQuantityString(
            R.plurals.label_listings_count,
            selectedListingIds.count(),
            selectedListingIds.size.toString()
        )
        val creditString = this.resources.getQuantityString(
            R.plurals.label_credit_count,
            selectedListingIds.count(),
            selectedListingIds.size.toString()
        )
        return getString(
            R.string.msg_confirmation_repost_listings,
            listingCountString,
            creditString
        )
    }

    private fun getConfirmRefreshListingMessage(cost: Int): String {
        val listingCountString = this.resources.getQuantityString(
            R.plurals.label_listings_count,
            selectedListingIds.count(),
            selectedListingIds.size.toString()
        )
        val creditString = this.resources.getQuantityString(
            R.plurals.label_credit_count,
            cost,
            cost.toString()
        )
        return getString(
            R.string.msg_confirmation_refresh_listings,
            listingCountString,
            creditString
        )
    }

    private fun showDialogToRepostListing(message: String) {
        dialogUtil.showActionDialog(message) {
            viewModel.repostListings(selectedListingIds)
        }
    }

    private fun showDialogToTakeDownListing() {
        dialogUtil.showActionDialog(
            R.string.msg_take_down_listings
        ) {
            viewModel.takeDownListings(selectedListingIds)
        }
    }

    private fun showDialogToDeleteListing() {
        dialogUtil.showActionDialog(
            R.string.msg_delete_listings
        ) {
            viewModel.deleteListings(selectedListingIds)
        }
    }

    private fun showDialogToDeleteDraftCeaForms() {
        dialogUtil.showActionDialog(R.string.msg_delete_draft_cea_forms) {
            viewModel.removeDraftCeaForms(selectedFormIds)
        }
    }

    private fun launchAddFeatures(creditType: ListingManagementEnum.SrxCreditMainType) {
        //TODO: to refine this and refine feature credit application
        //convert list to array list since feature credit application activity accept array list
        val tempListingIds = arrayListOf<String>()
        tempListingIds.addAll(selectedListingIds)
        //direct to another screen
        FeaturesCreditApplicationActivity.launch(
            this,
            creditType,
            tempListingIds
        )
    }

    private fun updateOwnershipRadioGroup(
        listingGroupSummaryPOs: List<ListingGroupSummaryPO>,
        position: Int
    ) {
        val tab = pagerAdapter.tabs[position]
        viewModel.selectedTab.value = pagerAdapter.tabs[position]

        val thisSummaryPOs = tab.listingGroups.mapNotNull { group ->
            listingGroupSummaryPOs.find {
                it.id == group.value
            }
        }

        val rentTotal = thisSummaryPOs.sumBy { it.rentTotal }
        val saleTotal = thisSummaryPOs.sumBy { it.saleTotal }

        radio_group_listings_ownership.updateTitleCounts(saleTotal, rentTotal)

        //reset search text when tab changes
        viewModel.searchText.postValue(null)

        binding.layoutMyListingAddOn.populateAddOnOptions(
            tab = tab,
            selectedListingSize = selectedListingIds.size
        )
    }

    private fun updateCeaOwnershipRadioGroup(ceaGroup: ListingGroupSummaryPO) {
        binding.radioGroupCeaOwnership.updateTitleCounts(ceaGroup.saleTotal, ceaGroup.rentTotal)
    }

    private fun updateDraftModeTabLayout(
        listingGroupSummaryPOs: List<ListingGroupSummaryPO>,
        position: Int
    ) {
        if (position == MyListingsPagerAdapter.MyListingsTab.DRAFT.position) {

            val draftListingSummaryPO = listingGroupSummaryPOs.find {
                it.id == ListingManagementEnum.ListingGroup.DRAFTS.value
            } ?: return

            val draftCeaSummaryPO = listingGroupSummaryPOs.find {
                it.id == ListingManagementEnum.ListingGroup.CEA_FORM.value
            } ?: return

            binding.tabLayoutDraft.updateDraftModeCount(
                draftListingSummaryPO.saleTotal + draftListingSummaryPO.rentTotal,
                draftCeaSummaryPO.saleTotal + draftCeaSummaryPO.rentTotal
            )
        } else {
            viewModel.draftMode.value = ListingManagementEnum.ListingDraftMode.LISTING
        }
    }

    private fun setupViewPager() {
        pagerAdapter = MyListingsPagerAdapter(
            this,
            supportFragmentManager
        )
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                ViewPager.SCROLL_STATE_IDLE
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val listingGroupSummaryPOs = viewModel.summary.value ?: return
                updateOwnershipRadioGroup(listingGroupSummaryPOs, position)
                updateDraftModeTabLayout(listingGroupSummaryPOs, position)
            }
        })
        view_pager.adapter = pagerAdapter
        tab_layout_listing_group.setupWithViewPager(view_pager)
    }

    private fun setOnClickListeners() {
        //Cea form
        binding.tabLayoutDraft.onTabClickListener = { viewModel.draftMode.value = it }

        binding.radioGroupCeaOwnership.onTabClickListener =
            { viewModel.ceaOwnershipType.value = it }

        //listings
        binding.radioGroupListingsOwnership.onTabClickListener =
            { viewModel.ownershipType.value = it }

        binding.btnSort.setOnClickListener { showSortDialog() }

        binding.ibSort.setOnClickListener {
            val orderCriteria = viewModel.orderCriteria.value ?: return@setOnClickListener
            val oppositeOrderCriteria =
                ListingManagementEnum.getOppositeOrderCriteria(orderCriteria)
                    ?: return@setOnClickListener
            viewModel.orderCriteria.postValue(oppositeOrderCriteria)
        }

        binding.btnSelectAll.setOnClickListener {
            val option = when (viewModel.selectAllAction.value?.first) {
                MyListingSelectAllEvent.Option.SELECT_ALL -> MyListingSelectAllEvent.Option.UNSELECT_ALL
                else -> MyListingSelectAllEvent.Option.SELECT_ALL
            }
            viewModel.selectAllAction.postValue(Pair(option, true))
        }

        tv_smart_filter_clear.setOnClickListener { clearSmartFilterOptions() }

        et_active_listings.edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                handler.postDelayed(runnable, 1000L)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(runnable)
            }

        })

        et_active_listings.onClearBtnClickLister = { viewModel.showSearchEditText.postValue(false) }

        appBarLayout.addOnOffsetChangedListener(
            onCollapse = {
                viewModel.showSearchBarLayout.postValue(false)
            }, onExpand = {
                viewModel.showSearchBarLayout.postValue(true)
            })
    }

    private fun handleAddOnOptions() {
        //Note: show certified listing add on option depend on selected listing counts
        //don't want to update add on options every times select listings
        //only update add on options for selected listing size zero or one or two
        if (selectedListingIds.size < 3) {
            when {
                selectedListingIds.size == 1 && selectedListings.size == 1 -> {
                    val tempListing = selectedListings.first()
                    binding.layoutMyListingAddOn.populateAddOnOptions(
                        tab = viewModel.selectedTab.value,
                        selectedListingSize = selectedListingIds.size,
                        isCommercial = tempListing.isCommercialListing()
                    )
                }
                else -> {
                    binding.layoutMyListingAddOn.populateAddOnOptions(
                        tab = viewModel.selectedTab.value,
                        selectedListingSize = selectedListingIds.size
                    )
                }
            }
        }
        viewModel.showAddOn.value = selectedListingIds.isNotEmpty()
    }

    private fun clearSmartFilterOptions() {
        when (viewModel.propertyMainType.value) {
            ListingEnum.PropertyMainType.HDB -> {
                when {
                    viewModel.propertyAge.value != null -> {
                        viewModel.propertyAge.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyAge.value)
                        populateBuiltYears(viewModel.builtYearCounts.value ?: mapOf())
                    }
                    viewModel.propertySubType.value != null -> {
                        viewModel.propertySubType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertySubType.value)
                        populatePropertySubTypes(
                            viewModel.propertySubTypeCounts.value ?: mapOf()
                        )
                    }
                    viewModel.propertyMainType.value != null -> {
                        viewModel.propertyMainType.postValue(null)
                        viewModel.tempPropertyMainType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyMainType.value)
                        populatePropertyMainTypes(viewModel.propertyMainTypeCounts.value ?: mapOf())
                    }
                }
            }
            ListingEnum.PropertyMainType.CONDO -> {
                when {
                    viewModel.propertyTenure.value != null -> {
                        viewModel.propertyTenure.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyTenure.value)
                        populateTenures(
                            viewModel.condoBedroomTenureCounts.value ?: mapOf()
                        )
                    }
                    viewModel.propertyBedRoom.value != null -> {
                        viewModel.propertyBedRoom.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyBedRoom.value)
                        populateCondoBedRooms(viewModel.condoBedroomCounts.value ?: mapOf())
                    }
                    viewModel.propertyMainType.value != null -> {
                        viewModel.propertyMainType.postValue(null)
                        viewModel.tempPropertyMainType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyMainType.value)
                        populatePropertyMainTypes(viewModel.propertyMainTypeCounts.value ?: mapOf())
                    }
                }
            }
            ListingEnum.PropertyMainType.LANDED -> {
                when {
                    viewModel.propertyTenure.value != null -> {
                        viewModel.propertyTenure.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyTenure.value)
                        populateTenures(viewModel.landedTenureCounts.value ?: mapOf())
                    }
                    viewModel.propertyAge.value != null -> {
                        viewModel.propertyAge.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyAge.value)
                        populateBuiltYears(viewModel.builtYearCounts.value ?: mapOf())
                    }
                    viewModel.propertySubType.value != null -> {
                        viewModel.propertySubType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertySubType.value)
                        populatePropertySubTypes(viewModel.propertySubTypeCounts.value ?: mapOf())
                    }
                    viewModel.propertyMainType.value != null -> {
                        viewModel.propertyMainType.postValue(null)
                        viewModel.tempPropertyMainType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyMainType.value)
                        populatePropertyMainTypes(viewModel.propertyMainTypeCounts.value ?: mapOf())
                    }
                }
            }
            ListingEnum.PropertyMainType.COMMERCIAL -> {
                when {
                    viewModel.propertySubType.value != null -> {
                        viewModel.propertySubType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertySubType.value)
                        populatePropertySubTypes(viewModel.propertySubTypeCounts.value ?: mapOf())
                    }
                    viewModel.propertyMainType.value != null -> {
                        viewModel.propertyMainType.postValue(null)
                        viewModel.tempPropertyMainType.postValue(null)
                        viewModel.clearFilterOptions.postValue(viewModel.propertyMainType.value)
                        populatePropertyMainTypes(viewModel.propertyMainTypeCounts.value ?: mapOf())
                    }
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun showSortDialog() {
        val orderCriteriaGroup = ListingManagementEnum.OrderCriteria.values()
        val orderCriteriaLabels = orderCriteriaGroup.map { it.label }
        dialogUtil.showListDialog(orderCriteriaLabels, { _, position ->
            viewModel.orderCriteria.postValue(orderCriteriaGroup[position])
        }, R.string.dialog_title_listing_sort_order)
    }

    private fun showPostListingDialog(listingId: String) {
        PostListingDialogFragment.launch(supportFragmentManager, listingId)
    }

    override fun onBackPressed() {
        when {
            layout_my_listing_add_on.layout_sub_add_on.visibility == View.VISIBLE -> {
                layout_my_listing_add_on.showSubAddOnFloatingActionButtons(false)
            }
            view_pager.currentItem != 0 -> {
                view_pager.setCurrentItem(0, true)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    companion object {
        private const val EXTRA_KEY_OWNERSHIP_TYPE = "EXTRA_KEY_OWNERSHIP_TYPE"
        const val EXTRA_KEY_PROPERTY_PRIMARY_SUB_TYPE = "EXTRA_KEY_PROPERTY_PRIMARY_SUB_TYPE"

        private const val REQUEST_CODE_SPONSOR = 1

        fun launch(
            activity: BaseActivity,
            ownershipType: ListingEnum.OwnershipType? = null,
            propertyMainType: ListingEnum.PropertyMainType? = null
        ) {
            val extras = Bundle()
            ownershipType?.run { extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, this) }
            propertyMainType?.run {
                extras.putInt(
                    EXTRA_KEY_PROPERTY_PRIMARY_SUB_TYPE,
                    this.primarySubType.type
                )
            }
            activity.launchActivity(MyListingsActivity::class.java, extras)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_my_listings
    }

    override fun getViewModelClass(): Class<MyListingsViewModel> {
        return MyListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}
