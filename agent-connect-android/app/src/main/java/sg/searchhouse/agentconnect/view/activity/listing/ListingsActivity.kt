package sg.searchhouse.agentconnect.view.activity.listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_listings.*
import kotlinx.android.synthetic.main.card_listings_header.view.*
import kotlinx.android.synthetic.main.card_listings_sub_header.*
import kotlinx.android.synthetic.main.card_listings_sub_header.view.*
import kotlinx.android.synthetic.main.layout_action_listings.*
import kotlinx.android.synthetic.main.layout_action_listings.view.*
import kotlinx.android.synthetic.main.layout_display_mode_toggle.view.*
import kotlinx.android.synthetic.main.tab_layout_listing_include_nearby.view.*
import kotlinx.android.synthetic.main.tab_layout_listing_new_projects.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.constant.AppConstant.DIR_LISTING_REPORT
import sg.searchhouse.agentconnect.constant.AppConstant.MAX_LIMIT_EXPORT_LISTINGS
import sg.searchhouse.agentconnect.databinding.ActivityListingsBinding
import sg.searchhouse.agentconnect.dsl.getIntExtraOrNull
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.*
import sg.searchhouse.agentconnect.enumeration.app.CobrokeDialogOption
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.listing.search.ExportListingsEvent
import sg.searchhouse.agentconnect.event.listing.search.SelectListingEvent
import sg.searchhouse.agentconnect.exception.ExceedMaxLimitException
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.view.adapter.listing.search.ListingGridAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.search.ListingListAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.search.ExportListingsDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.search.SelectedListingsDialogFragment
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry
import sg.searchhouse.agentconnect.view.widget.common.ClickableTextView
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingsViewModel

class ListingsActivity :
    ViewModelActivity<ListingsViewModel, ActivityListingsBinding>(isSliding = true) {
    private lateinit var ownershipType: OwnershipType
    private var listingListAdapter =
        ListingListAdapter(onClickListener = { listingId, listingType ->
            when (viewModel.listMode.value) {
                ListingsViewModel.ListMode.DEFAULT -> {
                    viewListingDetails(
                        listingId,
                        listingType
                    )
                }
                ListingsViewModel.ListMode.SELECTION -> {
                    toggleSelectedListItems(listingId, listingType)
                }
            }
        }, onCheckListener = { listingId, listingType ->
            if (viewModel.selectMode.value == null) {
                viewModel.selectMode.postValue(ListingsViewModel.SelectMode.COBROKE)
            }
            toggleSelectedListItems(listingId, listingType)
        })

    private var listingGridAdapter =
        ListingGridAdapter(onClickListener = { listingId, listingType ->
            when (viewModel.listMode.value) {
                ListingsViewModel.ListMode.DEFAULT -> {
                    viewListingDetails(
                        listingId,
                        listingType
                    )
                }
                ListingsViewModel.ListMode.SELECTION -> {
                    toggleSelectedListItems(listingId, listingType)
                }
            }
        }, onCheckListener = { listingId, listingType ->
            if (viewModel.selectMode.value == null) {
                viewModel.selectMode.postValue(ListingsViewModel.SelectMode.COBROKE)
            }
            toggleSelectedListItems(listingId, listingType)
        })

    private fun toggleSelectedListItems(listingId: String, listingType: String) {
        try {
            viewModel.toggleSelectedListItems(listingId, listingType)
        } catch (e: ExceedMaxLimitException) {
            showExceedExportListingsMaxLimitToast()
        }
    }

    private fun showExceedExportListingsMaxLimitToast() {
        ViewUtil.showMessage(
            getString(
                R.string.toast_selected_listings_exceed_max,
                NumberUtil.formatThousand(MAX_LIMIT_EXPORT_LISTINGS)
            )
        )
    }

    private var dialogProgressExportListings: AlertDialog? = null
    private var dialogProgressLoadReport: AlertDialog? = null

    private var dialogCobroke: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateViewModelParams()
        observeLiveData()
        listenRxBuses()
        setupInitiateRequest()
        setOnClickListeners()
        setLayoutManagers()
        setScrollListener()
    }

    private fun listenRxBuses() {
        listenRxBus(ExportListingsEvent::class.java) {
            viewModel.exportListings(it.exportOption, it.withAgentContact, it.withPhoto)
        }
        listenRxBus(SelectListingEvent::class.java) {
            viewModel.toggleSelectedListItems(it.listingId, it.listingType)
        }
    }

    private fun setLayoutManagers() {
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = listingListAdapter

        val gridLayoutManager = GridLayoutManager(this, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Set column size of each grid items
                // The number is span size, e.g. 2 means span 2 columns, i.e. full width
                return when (listingGridAdapter.items[position]) {
                    is ListingPO -> 1
                    else -> 2
                }
            }
        }

        grid.layoutManager = gridLayoutManager
        grid.adapter = listingGridAdapter
    }

    private fun viewListingDetails(listingId: String, listingType: String) {
        val intent = Intent(this, ListingDetailsActivity::class.java)
        intent.putExtra(ListingDetailsActivity.EXTRA_KEY_LISTING_ID, listingId)
        intent.putExtra(ListingDetailsActivity.EXTRA_KEY_LISTING_TYPE, listingType)
        startActivity(intent)
    }

    private fun setScrollListener() {
        ViewUtil.listenVerticalScrollEnd(list, reachBottom = {
            if (viewModel.canLoadNext()) {
                listingListAdapter.items.add(Loading())
                listingListAdapter.notifyDataSetChanged()
                viewModel.maybeLoadNext()
            }
        })

        grid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(
                        1
                    )
                    && viewModel.canLoadNext()
                ) {
                    listingGridAdapter.items.add(Loading())
                    listingGridAdapter.notifyDataSetChanged()
                    viewModel.maybeLoadNext()
                }
            }
        })
    }

    // Populate second level items if counts already available
    private fun maybePopulateSecondLevelItems(propertyMainType: PropertyMainType?) {
        if (propertyMainType == PropertyMainType.HDB && viewModel.hdbPropertySubTypeCounts.value != null) {
            populateSubTypes(viewModel.hdbPropertySubTypeCounts.value)
        } else if (propertyMainType == PropertyMainType.CONDO && viewModel.condoBedRoomCountCounts.value != null) {
            populateBedRoomCounts(viewModel.condoBedRoomCountCounts.value)
        } else if (propertyMainType == PropertyMainType.LANDED && viewModel.landedPropertySubTypeCounts.value != null) {
            populateSubTypes(viewModel.landedPropertySubTypeCounts.value)
        }
    }

    // Populate property age with count if already available
    private fun maybePopulatePropertyAges(propertySubType: PropertySubType?) {
        val subTypeAges = viewModel.subTypePropertyAgeCounts.value ?: mutableMapOf()
        if (subTypeAges.contains(propertySubType)) {
            populatePropertyAges(subTypeAges[propertySubType])
        }
    }

    private fun maybePopulateTenures(bedroomCount: BedroomCount?) {
        val bedRoomTenureCounts = viewModel.condoBedRoomTenureCounts.value ?: mutableMapOf()
        if (bedRoomTenureCounts.contains(bedroomCount)) {
            populateTenures(bedRoomTenureCounts[bedroomCount])
        }
    }

    private fun observeLiveData() {
        viewModel.displayMode.observe(this) {
            when (it) {
                ListingsViewModel.DisplayMode.LIST, ListingsViewModel.DisplayMode.GRID -> {
                    viewModel.maybeRefreshListing()
                }
                ListingsViewModel.DisplayMode.MAP -> { /* TODO: Populate map */
                }
                else -> { /* Do nothing */
                }
            }
        }

        viewModel.orderCriteria.observe(this) {
            viewModel.maybeRefreshListing()
        }

        viewModel.isIncludeNearBy.observe(this) {
            internalFilterRefreshListing()
        }

        viewModel.projectLaunchStatus.observe(this) {
            internalFilterRefreshListing()
        }

        viewModel.propertyType.observe(this) {
            maybePopulateSecondLevelItems(it)
            maybeInternalFilterRefreshListing()
        }

        viewModel.propertySubType.observe(this) {
            maybePopulatePropertyAges(it)
            maybeInternalFilterRefreshListing()
        }

        viewModel.bedroomCount.observe(this) {
            maybePopulateTenures(it)
            maybeInternalFilterRefreshListing()
        }

        viewModel.propertyAge.observe(this) {
            maybeInternalFilterRefreshListing()
        }

        viewModel.tenure.observe(this) {
            maybeInternalFilterRefreshListing()
        }

        viewModel.listItems.observe(this) { listingPOs ->
            val arrayList = arrayListOf<Any>()
            arrayList.addAll(listingPOs ?: arrayListOf())

            when (viewModel.displayMode.value) {
                ListingsViewModel.DisplayMode.LIST -> {
                    listingListAdapter.items = arrayList
                    listingListAdapter.selectedItems =
                        viewModel.selectedListItems.value ?: arrayListOf()
                    listingListAdapter.notifyDataSetChanged()
                }
                ListingsViewModel.DisplayMode.GRID -> {
                    listingGridAdapter.items = arrayList
                    listingGridAdapter.selectedItems =
                        viewModel.selectedListItems.value ?: arrayListOf()
                    listingGridAdapter.notifyDataSetChanged()
                }
                else -> {
                }
            }
        }

        viewModel.selectedListItems.observe(this) {
            when (viewModel.displayMode.value) {
                ListingsViewModel.DisplayMode.LIST -> {
                    listingListAdapter.selectedItems = it ?: arrayListOf()
                    listingListAdapter.notifyDataSetChanged()
                }
                ListingsViewModel.DisplayMode.GRID -> {
                    listingGridAdapter.selectedItems = it ?: arrayListOf()
                    listingGridAdapter.notifyDataSetChanged()
                }
                ListingsViewModel.DisplayMode.MAP -> {
                }
            }
        }

        viewModel.exportListingsResponse.observeNotNull(this) {
            dialogProgressLoadReport =
                dialogUtil.showProgressDialog(R.string.dialog_message_load_listing_report)
            viewModel.downloadReport(it.url)
        }

        viewModel.reportPdfLocalFileName.observeNotNull(this) {
            dialogProgressLoadReport?.dismiss()
            IntentUtil.viewPdf(this@ListingsActivity, it, DIR_LISTING_REPORT)
        }

        viewModel.reportExcelLocalFileName.observeNotNull(this) {
            dialogProgressLoadReport?.dismiss()
            IntentUtil.viewCsv(this@ListingsActivity, it, DIR_LISTING_REPORT)
        }

        viewModel.exportListingsStatus.observe(this) {
            when (it) {
                ApiStatus.StatusKey.LOADING -> {
                    dialogProgressExportListings =
                        dialogUtil.showProgressDialog(R.string.dialog_message_generate_listing_report)
                }
                ApiStatus.StatusKey.SUCCESS -> {
                    dialogProgressExportListings?.dismiss()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(R.string.fail_export_listings)
                    dialogProgressExportListings?.dismiss()
                }
                ApiStatus.StatusKey.ERROR -> {
                    ViewUtil.showMessage(R.string.error_export_listings)
                    dialogProgressExportListings?.dismiss()
                }
                else -> {
                }
            }
        }

        viewModel.propertyMainTypeCounts.observe(this) { propertyMainTypeCounts ->
            if (propertyMainTypeCounts != null) {
                populatePropertyMainTypeCounts(propertyMainTypeCounts)
            }
            layout_property_types.visibility = View.VISIBLE
        }

        viewModel.hdbPropertySubTypeCounts.observe(this) { hdbPropertySubTypeCounts ->
            populateSubTypes(hdbPropertySubTypeCounts)
        }

        viewModel.condoBedRoomCountCounts.observe(this) { condoBedroomCountCounts ->
            populateBedRoomCounts(condoBedroomCountCounts)
        }

        viewModel.landedPropertySubTypeCounts.observe(this) { landedPropertySubTypeCounts ->
            populateSubTypes(landedPropertySubTypeCounts)
        }

        viewModel.subTypePropertyAgeCounts.observe(this) { hdbPropertyAgeCounts ->
            if (hdbPropertyAgeCounts != null) {
                populatePropertyAges(hdbPropertyAgeCounts[viewModel.propertySubType.value])
            }
        }

        viewModel.condoBedRoomTenureCounts.observe(this) { condoBedRoomTenureCounts ->
            if (condoBedRoomTenureCounts != null) {
                populateTenures(condoBedRoomTenureCounts[viewModel.bedroomCount.value])
            }
        }

        viewModel.landedPropertyAgeTenureCounts.observe(this) { landedPropertyAgeTenureCounts ->
            if (landedPropertyAgeTenureCounts != null) {
                populateTenures(landedPropertyAgeTenureCounts[viewModel.propertyAge.value])
            }
        }

        viewModel.selectMode.observe(this) {
            viewModel.listMode.postValue(
                if (it != null) {
                    ListingsViewModel.ListMode.SELECTION
                } else {
                    ListingsViewModel.ListMode.DEFAULT
                }
            )
        }

        viewModel.listMode.observe(this) {
            when (viewModel.displayMode.value) {
                ListingsViewModel.DisplayMode.LIST -> {
                    listingListAdapter.isSelectMode = it == ListingsViewModel.ListMode.SELECTION
                    if (it == ListingsViewModel.ListMode.DEFAULT) {
                        viewModel.selectedListItems.postValue(arrayListOf())
                    }
                    listingListAdapter.notifyDataSetChanged()
                }
                ListingsViewModel.DisplayMode.GRID -> {
                    listingGridAdapter.isSelectMode = it == ListingsViewModel.ListMode.SELECTION
                    if (it == ListingsViewModel.ListMode.DEFAULT) {
                        viewModel.selectedListItems.postValue(arrayListOf())
                    }
                    listingGridAdapter.notifyDataSetChanged()
                }
                ListingsViewModel.DisplayMode.MAP -> {
                }
            }
        }
    }

    private fun populateTenures(tenureCounts: Map<Tenure, Int>?) {
        resetFilterItems()
        val occupied = tenureCounts?.filter { it.value > 0 }?.toSortedMap()
        if (occupied != null && occupied.isNotEmpty()) {
            occupied.map { tenureCount ->
                val tenure = tenureCount.key
                val count = NumberUtil.formatThousand(tenureCount.value)
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text = getString(tenure.labelWithCount, count)
                clickableTextView.setOnClickListener {
                    viewModel.tenure.postValue(tenure)
                    resetFilterItems()
                }
                layout_property_types.addView(clickableTextView)
            }
        } else {
            resetFilterItems()
        }
    }

    private fun populatePropertyAges(hdbPropertyAgeCounts: Map<PropertyAge, Int>?) {
        resetFilterItems()
        val occupied = hdbPropertyAgeCounts?.filter { it.value > 0 }?.toSortedMap()
        if (occupied != null && occupied.isNotEmpty()) {
            occupied.map { propertyAgeCount ->
                val propertyAge = propertyAgeCount.key
                val count = propertyAgeCount.value
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))

                val countString = NumberUtil.formatThousand(count)
                clickableTextView.text = getString(propertyAge.labelWithCount, countString)

                clickableTextView.setOnClickListener {
                    viewModel.propertyAge.postValue(propertyAge)
                    resetFilterItems()
                }
                layout_property_types.addView(clickableTextView)
            }
        }
    }

    private fun populateBedRoomCounts(condoBedroomCountCounts: Map<BedroomCount, Int>?) {
        resetFilterItems()
        val occupied = condoBedroomCountCounts?.filter { it.value > 0 }?.toSortedMap()
        if (occupied != null && occupied.isNotEmpty()) {
            occupied.map { condoBedroomCountCount ->
                val bedroomCount = condoBedroomCountCount.key
                val count = condoBedroomCountCount.value
                val countString = NumberUtil.formatThousand(count)

                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))

                clickableTextView.text = when {
                    bedroomCount.value < 6 -> getString(
                        R.string.bed_room_count_with_count,
                        bedroomCount.value,
                        countString
                    )
                    else -> getString(
                        R.string.bed_room_count_more_than_with_count,
                        bedroomCount.value,
                        countString
                    )
                }

                clickableTextView.setOnClickListener {
                    viewModel.bedroomCount.postValue(bedroomCount)
                    resetFilterItems()
                }
                layout_property_types.addView(clickableTextView)
            }
        } else {
            populateBedRoomCounts()
        }
    }

    private fun populateSubTypes(propertySubTypeCounts: Map<PropertySubType, Int>?) {
        resetFilterItems()
        val occupied = propertySubTypeCounts?.filter { it.value > 0 }?.toSortedMap()
        if (occupied != null && occupied.isNotEmpty()) {
            occupied.map { propertySubTypeCount ->
                val propertySubType = propertySubTypeCount.key
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
                clickableTextView.text =
                    this.getString(
                        propertySubType.labelWithCount,
                        NumberUtil.formatThousand(propertySubTypeCount.value)
                    )
                clickableTextView.setOnClickListener {
                    viewModel.propertySubType.postValue(propertySubType)
                    resetFilterItems()
                }
                layout_property_types.addView(clickableTextView)
            }
        } else {
            resetFilterItems()
        }
        layout_property_types.visibility = View.VISIBLE
    }

    private fun maybeInternalFilterRefreshListing() {
        if (viewModel.hasExternalFilter.value != true) {
            internalFilterRefreshListing()
        }
    }

    private fun internalFilterRefreshListing() {
        viewModel.maybeFindListingsCounts()
        viewModel.maybeRefreshListing()
    }

    // Do first request when basic params were ready
    // TODO: This is temp fix.
    // TODO: The proper way is to listen to ListingsViewModels#isReadyForRequest that observe query, ownership type, and property purpose
    private fun setupInitiateRequest() {
        // query, as the last param for default settings, serves as a hook to trigger refresh
        viewModel.query.observe(this) {
            maybeInternalFilterRefreshListing()
        }

        // This applies when query is null
        viewModel.amenitiesIds.observe(this) {
            internalFilterRefreshListing()
        }

        viewModel.districtIds.observe(this) {
            internalFilterRefreshListing()
        }

        viewModel.hdbTownIds.observe(this) {
            internalFilterRefreshListing()
        }

        // externalFilterCount, as the last param to update external filter, serves as a hook to trigger refresh
        viewModel.externalFilterCount.observe(this) {
            viewModel.maybeRefreshListing()
        }
    }

    private fun populatePropertyMainTypeCounts(propertyMainTypeCounts: Map<PropertyMainType, Int>) {
        propertyMainTypeCounts.map { propertyMainTypeCount ->
            val count = NumberUtil.formatThousand(propertyMainTypeCount.value)
            when (propertyMainTypeCount.key) {
                PropertyMainType.HDB -> btn_property_type_hdb.text =
                    getString(R.string.property_type_with_count_hdb, count)
                PropertyMainType.CONDO ->
                    btn_property_type_condo.text =
                        getString(R.string.property_type_with_count_condo, count)
                PropertyMainType.LANDED -> btn_property_type_landed.text =
                    getString(R.string.property_type_with_count_landed, count)
                else -> {
                }
            }
        }
    }

    private fun setOnClickListeners() {
        layout_search.btn_sort.setOnClickListener {
            showSortDialog()
        }
        layout_search.btn_header.setOnClickListener {
            SearchActivity.launch(
                this,
                SearchActivity.SearchType.LISTINGS,
                viewModel.propertyPurpose.value,
                viewModel.ownershipType.value,
                SearchActivity.ExpandMode.COMPACT,
                REQUEST_CODE_SEARCH
            )
        }
        layout_search.btn_back.setOnClickListener {
            onBackPressed()
        }
        layout_search.btn_select_all.setOnClickListener {
            if (viewModel.isAllSelected.value == true) {
                viewModel.unselectAll()
            } else {
                try {
                    viewModel.selectAll()
                } catch (e: ExceedMaxLimitException) {
                    showExceedExportListingsMaxLimitToast()
                }
            }
        }
        layout_sub_header.btn_property_type_clear.setOnClickListener {
            clearSmartFilter()
        }
        layout_sub_header.btn_property_type_hdb.setOnClickListener {
            viewModel.propertyType.postValue(PropertyMainType.HDB)
        }
        layout_sub_header.btn_property_type_condo.setOnClickListener {
            viewModel.propertyType.postValue(PropertyMainType.CONDO)
        }
        layout_sub_header.btn_property_type_landed.setOnClickListener {
            viewModel.propertyType.postValue(PropertyMainType.LANDED)
        }
        layout_sub_header.tabs_include_nearby.btn_tab_include_nearby.setOnClickListener {
            if (viewModel.isIncludeNearBy.value != true) {
                viewModel.isIncludeNearBy.postValue(true)
            }
        }
        layout_sub_header.tabs_include_nearby.btn_tab_project_only.setOnClickListener {
            if (viewModel.isIncludeNearBy.value != false) {
                viewModel.isIncludeNearBy.postValue(false)
            }
        }
        layout_sub_header.tabs_listing_new_projects.btn_tab_show_all.setOnClickListener {
            if (viewModel.projectLaunchStatus.value != null) {
                viewModel.projectLaunchStatus.postValue(null)
            }
        }
        layout_sub_header.tabs_listing_new_projects.btn_tab_new_projects.setOnClickListener {
            if (viewModel.projectLaunchStatus.value != ProjectLaunchStatus.NEW_LAUNCH) {
                viewModel.projectLaunchStatus.postValue(ProjectLaunchStatus.NEW_LAUNCH)
            }
        }
        layout_search.btn_filter_result.setOnClickListener {
            startExternalFilter()
        }
        layout_view_mode_toggle.btn_view_mode_list.setOnClickListener {
            viewModel.displayMode.postValue(ListingsViewModel.DisplayMode.LIST)
        }
        layout_view_mode_toggle.btn_view_mode_grid.setOnClickListener {
            viewModel.displayMode.postValue(ListingsViewModel.DisplayMode.GRID)
        }
        layout_view_mode_toggle.btn_view_mode_map.setOnClickListener {
            viewModel.displayMode.postValue(ListingsViewModel.DisplayMode.MAP)
        }
        layout_action_listings.btn_submit_export.setOnClickListener {
            val listingCount = viewModel.selectedListItemsSize.value ?: 0
            ExportListingsDialogFragment.newInstance(listingCount)
                .show(
                    supportFragmentManager,
                    ExportListingsDialogFragment.TAG
                )
        }
        layout_action_listings.btn_submit_cobroke.setOnClickListener {
            showCobrokeDialog()
        }
        layout_sub_header.btn_export_listings.setOnClickListener {
            viewModel.selectMode.postValue(ListingsViewModel.SelectMode.EXPORT_LISTINGS)
        }
        layout_sub_header.tv_listing_total.setOnClickListener {
            ViewUtil.showMessage(layout_sub_header.tv_listing_total.text.toString())
        }
        tv_listings_count.setOnClickListener {
            if ((viewModel.selectedListItemsSize.value ?: 0) > 0) {
                SelectedListingsDialogFragment.newInstance(getSelectedListings())
                    .show(supportFragmentManager, SelectedListingsDialogFragment.TAG)
            }
        }
    }

    private fun showCobrokeDialog() {
        val list = CobrokeDialogOption.values().map { it.label }
        dialogCobroke = dialogUtil.showListDialog(list, { dialogInterface, position ->
            when (position) {
                CobrokeDialogOption.PHONE_SMS.position -> {
                    showPhoneSmsCobrokeDisclaimer()
                }
                CobrokeDialogOption.SRX_CHAT.position -> {
                    if (!isExceedChatMaxLimit()) {
                        performSsmCobroke()
                    } else {
                        ViewUtil.showMessage(
                            getString(
                                R.string.toast_selected_listings_srx_chat_exceed_max,
                                NumberUtil.formatThousand(AppConstant.MAX_LIMIT_SEND_SRX_CHAT)
                            )
                        )
                    }
                }
                CobrokeDialogOption.SRX_SMS_BLAST.position -> {
                    launchSmsBlastActivity()
                }
            }
            dialogInterface.dismiss()
        })
    }

    private fun isExceedChatMaxLimit(): Boolean {
        val selectedSize = viewModel.selectedListItemsSize.value ?: 0
        return selectedSize > AppConstant.MAX_LIMIT_SEND_SRX_CHAT
    }

    private fun launchSmsBlastActivity() {
        try {
            SmsBlastActivity.launch(this, viewModel.getSelectedCobrokeListings())
        } catch (e: IllegalArgumentException) {
            ViewUtil.showMessage(R.string.toast_error_no_listings)
        }
    }

    private fun showPhoneSmsCobrokeDisclaimer() {
        dialogUtil.showInformationDialog(R.string.dialog_message_warning_sms_telco) {
            val phoneNumbers = getSelectedAgents().joinToString(",") { it.mobile }
            IntentUtil.sendSms(this, phoneNumbers)
        }
    }

    private fun getSelectedAgents(): List<AgentPO> {
        val selectedListingTypeIds = viewModel.selectedListItems.value?.map {
            "${it.second}${it.first}"
        } ?: emptyList()

        return viewModel.getCurrentListingPOs().filter {
            selectedListingTypeIds.contains(it.getListingTypeId())
        }.distinctBy {
            it.agentPO?.userId ?: ""
        }.mapNotNull {
            it.agentPO
        }
    }

    private fun getSelectedListings(): List<ListingPO> {
        val selectedListingTypeIds = viewModel.selectedListItems.value?.map {
            "${it.second}${it.first}"
        } ?: emptyList()
        return viewModel.getCurrentListingPOs().filter {
            selectedListingTypeIds.contains(it.getListingTypeId())
        }
    }

    private fun performSsmCobroke() {
        val selectedListings = viewModel.selectedListItems.value ?: return

        when (selectedListings.size) {
            0 -> {
                // Do nothing
            }
            1 -> {
                val listing = selectedListings[0]
                val listingId = listing.first
                val listingType = listing.second
                val agentPO = viewModel.getAgentPO(listingId, listingType)
                agentPO?.run {
                    ChatMessagingActivity.launch(
                        this@ListingsActivity,
                        agent = agentPO,
                        listingTypeId = "$listingType,$listingId"
                    )
                } ?: SrxChatActivity.launch(activity = this, listingIds = selectedListings)
            }
            else -> {
                SrxChatActivity.launch(activity = this, listingIds = selectedListings)
            }
        }
    }

    private fun getPropertyAgeCount(propertySubType: PropertySubType): Map<PropertyAge, Int>? {
        val propertySubTypeAgeMap = viewModel.subTypePropertyAgeCounts.value ?: mutableMapOf()
        return propertySubTypeAgeMap[propertySubType]
    }

    private fun clearSmartFilter() {
        when {
            viewModel.tenure.value != null -> {
                viewModel.tenure.postValue(null)
                resetFilterItems()
                val bedroomCount = viewModel.bedroomCount.value
                val propertyAge = viewModel.propertyAge.value
                if (viewModel.propertyType.value == PropertyMainType.CONDO && bedroomCount != null) {
                    val tenureCounts = viewModel.condoBedRoomTenureCounts.value ?: mutableMapOf()
                    populateTenures(tenureCounts[bedroomCount])
                } else if (viewModel.propertyType.value == PropertyMainType.LANDED && propertyAge != null) {
                    val tenureCounts =
                        viewModel.landedPropertyAgeTenureCounts.value ?: mutableMapOf()
                    populateTenures(tenureCounts[propertyAge])
                } else {
                    resetFilterItems()
                }
            }
            viewModel.propertyAge.value != null -> {
                viewModel.propertyAge.postValue(null)
                resetFilterItems()
                viewModel.propertySubType.value?.run {
                    val propertyAgeCount = getPropertyAgeCount(this)
                    populatePropertyAges(propertyAgeCount)
                } ?: run {
                    populatePropertyAges()
                }
            }
            viewModel.bedroomCount.value != null -> {
                viewModel.bedroomCount.postValue(null)
                resetFilterItems()
                populateBedRoomCounts(viewModel.condoBedRoomCountCounts.value)
            }
            viewModel.propertySubType.value != null -> {
                viewModel.propertySubType.postValue(null)
                resetFilterItems()
                when (val propertyType = viewModel.propertyType.value) {
                    PropertyMainType.HDB -> {
                        populateSubTypes(viewModel.hdbPropertySubTypeCounts.value)
                    }
                    PropertyMainType.LANDED -> {
                        populateSubTypes(viewModel.landedPropertySubTypeCounts.value)
                    }
                    else -> {
                        propertyType?.run { populateSubTypes(this) }
                    }
                }
            }
            else -> {
                viewModel.propertyType.postValue(null)
                resetFilterItems()
            }
        }
    }

    private fun startExternalFilter() {
        var intent = Intent(this, FilterListingActivity::class.java)
        intent = getFilterIntentWithInternalParams(intent)
        intent = getFilterIntentWithExternalParams(intent)
        startActivityForResult(intent, REQUEST_CODE_FILTER_LISTINGS)
    }

    // Some e.g. bedroom count, override internal params
    private fun getFilterIntentWithExternalParams(intent: Intent): Intent {
        viewModel.bedroomCounts.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_BEDROOM_COUNTS,
                this
            )
        }
        viewModel.propertySubTypes.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_PROPERTY_SUB_TYPES,
                this
            )
        }
        viewModel.tenures.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_TENURES,
                this
            )
        }
        viewModel.minBuiltYear.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_CONSTRUCTION_YEAR,
                this
            )
        }
        viewModel.maxBuiltYear.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MAX_CONSTRUCTION_YEAR,
                this
            )
        }
        viewModel.bathroomCounts.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_BATHROOM_COUNTS,
                this
            )
        }
        viewModel.rentalType.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_RENTAL_TYPE,
                this
            )
        }
        viewModel.floors.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_FLOORS,
                this
            )
        }
        viewModel.furnishes.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_FURNISHES,
                this
            )
        }
        viewModel.minPrice.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_PRICE_RANGE,
                this
            )
        }
        viewModel.maxPrice.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MAX_PRICE_RANGE,
                this
            )
        }
        viewModel.minPSF.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_PSF,
                this
            )
        }
        viewModel.maxPSF.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MAX_PSF,
                this
            )
        }
        viewModel.minBuiltSize.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_BUILT_SIZE,
                this
            )
        }
        viewModel.maxBuiltSize.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MAX_BUILT_SIZE,
                this
            )
        }
        viewModel.minLandSize.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_LAND_SIZE,
                this
            )
        }
        viewModel.maxLandSize.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MAX_LAND_SIZE,
                this
            )
        }
        viewModel.minDateFirstPosted.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_DATE_FIRST_POSTED,
                this
            )
        }
        viewModel.xListingPrice.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_X_LISTING_PRICE,
                this
            )
        }
        viewModel.hasVirtualTours.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_HAS_VIRTUAL_TOURS,
                this
            )
        }
        viewModel.hasDroneViews.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_HAS_DRONE_VIEWS,
                this
            )
        }
        viewModel.ownerCertification.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_OWNER_CERTIFICATION,
                this
            )
        }
        viewModel.exclusiveListing.value?.run {
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_EXCLUSIVE_LISTING,
                this
            )
        }

        return intent
    }

    private fun getFilterIntentWithInternalParams(intent: Intent): Intent {
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_PROPERTY_PURPOSE,
            viewModel.propertyPurpose.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_PROPERTY_MAIN_TYPE,
            viewModel.propertyType.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_PROPERTY_SUB_TYPES,
            viewModel.propertySubType.value?.type.toString()
        )

        viewModel.propertyAge.value?.from?.let { from ->
            val maxConstructionYear = DateTimeUtil.getCurrentYear() - from
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MAX_CONSTRUCTION_YEAR,
                maxConstructionYear
            )
        }

        viewModel.propertyAge.value?.to?.let { to ->
            val minConstructionYear = DateTimeUtil.getCurrentYear() - to
            intent.putExtra(
                FilterListingActivity.EXTRA_KEY_INPUT_MIN_CONSTRUCTION_YEAR,
                minConstructionYear
            )
        }

        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_TENURES,
            viewModel.tenure.value?.value?.toString()
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_BEDROOM_COUNTS,
            viewModel.bedroomCount.value?.toString()
        )

        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_SEARCH_TEXT,
            viewModel.query.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_OWNERSHIP_TYPE,
            viewModel.ownershipType.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_IS_TRANSACTED,
            viewModel.isTransacted.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_PROJECT_LAUNCH_STATUS,
            viewModel.projectLaunchStatus.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_IS_INCLUDE_NEARBY,
            viewModel.isIncludeNearBy.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_IS_NEARBY_APPLICABLE,
            viewModel.isApplyNearByToggle.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_PROPERTY_AGE,
            viewModel.propertyAge.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_AMENITIES_IDS,
            viewModel.amenitiesIds.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_DISTRICT_IDS,
            viewModel.districtIds.value
        )
        intent.putExtra(
            FilterListingActivity.EXTRA_KEY_INPUT_HDB_TOWN_IDS,
            viewModel.hdbTownIds.value
        )
        return intent
    }

    private fun populateBedRoomCounts() {
        BedroomCount.values().filter { it != BedroomCount.ANY }
            .map { bedroomCount ->
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))

                clickableTextView.text = when {
                    bedroomCount.value < 6 -> getString(R.string.bed_room_count, bedroomCount.value)
                    else -> getString(R.string.bed_room_count_more_than, bedroomCount.value)
                }

                clickableTextView.setOnClickListener {
                    viewModel.bedroomCount.postValue(bedroomCount)
                    resetFilterItems()
                    populateTenures()
                }
                layout_property_types.addView(clickableTextView)
            }
    }

    // TODO: Maybe refactor
    private fun populateSubTypes(propertyMainType: PropertyMainType) {
        propertyMainType.propertySubTypes.map { propertySubType ->
            val clickableTextView =
                ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))
            clickableTextView.text = this.getString(propertySubType.label)
            clickableTextView.setOnClickListener {
                viewModel.propertySubType.postValue(propertySubType)
                resetFilterItems()
                populatePropertyAges()
            }
            layout_property_types.addView(clickableTextView)
        }
    }

    private fun populatePropertyAges() {
        PropertyAge.values().map { propertyAge ->
            val clickableTextView =
                ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))

            clickableTextView.text =
                this.getString(propertyAge.label)

            clickableTextView.setOnClickListener {
                viewModel.propertyAge.postValue(propertyAge)
                resetFilterItems()
                if (viewModel.propertyType.value == PropertyMainType.LANDED) {
                    populateTenures()
                }
            }
            layout_property_types.addView(clickableTextView)
        }
    }

    private fun populateTenures() {
        Tenure.values().filter { it != Tenure.NOT_SPECIFIED && it != Tenure.NINE_NINE_NINE_YEARS }
            .map { tenure ->
                val clickableTextView =
                    ClickableTextView(ContextThemeWrapper(this, R.style.PropertyTypeButton))

                clickableTextView.text = getString(tenure.label)

                clickableTextView.setOnClickListener {
                    viewModel.tenure.postValue(tenure)
                    resetFilterItems()
                }
                layout_property_types.addView(clickableTextView)
            }
    }

    private fun resetFilterItems() {
        layout_property_types.removeViews(
            DEFAULT_PROPERTY_TYPE_BUTTONS_COUNT,
            layout_property_types.childCount - DEFAULT_PROPERTY_TYPE_BUTTONS_COUNT
        )
    }

    private fun showSortDialog() {
        val orderCriteriaGroup = if (shouldEnableDistanceSort()) {
            OrderCriteria.values()
        } else {
            OrderCriteria.values().filter {
                it != OrderCriteria.DISTANCE_ASC && it != OrderCriteria.DISTANCE_DESC
            }.toTypedArray()
        }

        val orderCriteriaLabels = orderCriteriaGroup.map { it.label }
        dialogUtil.showListDialog(orderCriteriaLabels, { _, position ->
            viewModel.orderCriteria.postValue(orderCriteriaGroup[position])
        }, R.string.dialog_title_listing_sort_order)
    }

    private fun shouldEnableDistanceSort(): Boolean {
        return !TextUtils.isEmpty(viewModel.query.value) || !TextUtils.isEmpty(viewModel.amenitiesIds.value)
    }

    private fun populateViewModelParams() {
        if (intent?.extras?.get(SearchResultActivityEntry.EXTRA_KEY_OWNERSHIP_TYPE) is OwnershipType) {
            ownershipType =
                intent?.extras?.get(SearchResultActivityEntry.EXTRA_KEY_OWNERSHIP_TYPE) as OwnershipType
            viewModel.ownershipType.postValue(ownershipType)
        }

        if (intent?.extras?.get(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_PURPOSE) is PropertyPurpose) {
            viewModel.propertyPurpose.postValue(intent?.extras?.get(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_PURPOSE) as PropertyPurpose)
        }

        if (intent?.extras?.get(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) is PropertyMainType) {
            val propertyMainType =
                intent?.extras?.get(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_MAIN_TYPE) as PropertyMainType
            // For case of PropertyMainType.Residential, just assume property type is empty because property purpose already cover that
            if (propertyMainType != PropertyMainType.RESIDENTIAL) {
                viewModel.propertyType.postValue(propertyMainType)
            }
        }

        val propertySubType =
            intent?.extras?.getSerializable(SearchResultActivityEntry.EXTRA_KEY_PROPERTY_SUB_TYPE) as PropertySubType?
        propertySubType?.let { viewModel.propertySubType.postValue(it) }

        // Initiate load listings, allow one only
        intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_AMENITY_IDS)?.let {
            val amenityNames =
                intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_AMENITY_NAMES)
            amenityNames?.let { viewModel.amenityNames.postValue(amenityNames) }

            viewModel.amenitiesIds.postValue(it)
            return@populateViewModelParams
        }
        intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_DISTRICT_IDS)?.let {
            val districtNames =
                intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_DISTRICT_NAMES)
            districtNames?.let { viewModel.districtNames.postValue(districtNames) }

            viewModel.districtIds.postValue(it)
            return@populateViewModelParams
        }
        intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_HDB_TOWN_IDS)?.let {
            val hdbTownNames =
                intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_HDB_TOWN_NAMES)
            hdbTownNames?.let { viewModel.hdbTownNames.postValue(hdbTownNames) }

            viewModel.hdbTownIds.postValue(it)
            return@populateViewModelParams
        }

        val query = intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_QUERY)
        query?.let { viewModel.query.postValue(it) }

        val title = intent?.extras?.getString(SearchResultActivityEntry.EXTRA_KEY_TITLE)
        title?.let { viewModel.optionalTitle.postValue(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_FILTER_LISTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    applyExternalFilter(data)
                }
            }
            REQUEST_CODE_SEARCH -> {
                if (resultCode == Activity.RESULT_OK) {
                    finish()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun applyExternalFilter(data: Intent?) {
        val thisData = data ?: return

        viewModel.hasExternalFilter.postValue(true)

        // Number to be displayed on the right of Filter result button
        var externalFilterCount =
            1 // No matter the change of property types, their count is always one

        // Get params from extras start
        val bedroomCounts = thisData.getStringExtra(FilterListingActivity.EXTRA_KEY_BEDROOM_COUNTS)
        val bathroomCounts =
            thisData.getStringExtra(FilterListingActivity.EXTRA_KEY_BATHROOM_COUNTS)

        val propertyMainType =
            thisData.getSerializableExtra(FilterListingActivity.EXTRA_KEY_PROPERTY_MAIN_TYPE) as PropertyMainType?
        val propertySubTypes =
            thisData.getStringExtra(FilterListingActivity.EXTRA_KEY_PROPERTY_SUB_TYPES)

        val rentalType =
            thisData.getSerializableExtra(FilterListingActivity.EXTRA_KEY_RENTAL_TYPE) as RentalType?

        val floors = thisData.getStringExtra(FilterListingActivity.EXTRA_KEY_FLOORS)
        val tenures = thisData.getStringExtra(FilterListingActivity.EXTRA_KEY_TENURES)
        val furnishes = thisData.getStringExtra(FilterListingActivity.EXTRA_KEY_FURNISHES)

        val minPriceRange: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MIN_PRICE_RANGE)
        val maxPriceRange: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MAX_PRICE_RANGE)
        val minPricePsf: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MIN_PRICE_PSF)
        val maxPricePsf: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MAX_PRICE_PSF)
        val minFloorArea: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MIN_FLOOR_AREA)
        val maxFloorArea: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MAX_FLOOR_AREA)
        val minLandSize: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MIN_LAND_SIZE)
        val maxLandSize: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MAX_LAND_SIZE)
        val minConstructionYear: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MIN_CONSTRUCTION_YEAR)
        val maxConstructionYear: Int? =
            thisData.getIntExtraOrNull(FilterListingActivity.EXTRA_KEY_MAX_CONSTRUCTION_YEAR)

        val xListingPrice = when (thisData.getBooleanExtra(
            FilterListingActivity.EXTRA_KEY_X_LISTING_PRICE,
            false
        )) {
            true -> true
            else -> null
        }
        val hasVirtualTours = when (thisData.getBooleanExtra(
            FilterListingActivity.EXTRA_KEY_HAS_VIRTUAL_TOURS,
            false
        )) {
            true -> true
            else -> null
        }
        val hasDroneViews = when (thisData.getBooleanExtra(
            FilterListingActivity.EXTRA_KEY_HAS_DRONE_VIEWS,
            false
        )) {
            true -> true
            else -> null
        }

        val ownerCertification = when (thisData.getBooleanExtra(
            FilterListingActivity.EXTRA_KEY_OWNER_CERTIFICATION,
            false
        )) {
            true -> true
            else -> null
        }

        val exclusiveListing = when (thisData.getBooleanExtra(
            FilterListingActivity.EXTRA_KEY_EXCLUSIVE_LISTING,
            false
        )) {
            true -> true
            else -> null
        }

        val minDateFirstPosted =
            thisData.getSerializableExtra(FilterListingActivity.EXTRA_KEY_IS_MIN_DATE_FIRST_POSTED) as MinDateFirstPosted?
        // Get params from extras end

        // Calculate filter count start
        if (!isMultiSelectDefault(bedroomCounts)) {
            externalFilterCount++
        }
        if (!isMultiSelectDefault(bathroomCounts)) {
            externalFilterCount++
        }
        if (!isMultiSelectDefault(floors)) {
            externalFilterCount++
        }
        if (!isMultiSelectDefault(tenures)) {
            externalFilterCount++
        }
        if (!isMultiSelectDefault(furnishes)) {
            externalFilterCount++
        }

        if (minPriceRange != null || maxPriceRange != null) {
            externalFilterCount++
        }
        if (minPricePsf != null || maxPricePsf != null) {
            externalFilterCount++
        }
        if (minFloorArea != null || maxFloorArea != null) {
            externalFilterCount++
        }
        if (minLandSize != null || maxLandSize != null) {
            externalFilterCount++
        }
        if (minConstructionYear != null || maxConstructionYear != null) {
            externalFilterCount++
        }

        if (xListingPrice != null) {
            externalFilterCount++
        }
        if (hasVirtualTours != null) {
            externalFilterCount++
        }
        if (hasDroneViews != null) {
            externalFilterCount++
        }
        if (ownerCertification != null) {
            externalFilterCount++
        }
        if (exclusiveListing != null) {
            externalFilterCount++
        }

        if (minDateFirstPosted != null && minDateFirstPosted != MinDateFirstPosted.ANY) {
            externalFilterCount++
        }
        // Calculate filter count end

        // Post params into view model
        viewModel.bedroomCounts.postValue(bedroomCounts)
        viewModel.bathroomCounts.postValue(bathroomCounts)

        viewModel.propertyType.postValue(propertyMainType)
        viewModel.propertySubTypes.postValue(propertySubTypes)
        viewModel.rentalType.postValue(rentalType)
        viewModel.floors.postValue(floors)
        viewModel.tenures.postValue(tenures)
        viewModel.furnishes.postValue(furnishes)

        viewModel.minPrice.postValue(minPriceRange)
        viewModel.maxPrice.postValue(maxPriceRange)
        viewModel.minPSF.postValue(minPricePsf)
        viewModel.maxPSF.postValue(maxPricePsf)
        viewModel.minBuiltSize.postValue(minFloorArea)
        viewModel.maxBuiltSize.postValue(maxFloorArea)
        viewModel.minLandSize.postValue(minLandSize)
        viewModel.maxLandSize.postValue(maxLandSize)
        viewModel.minBuiltYear.postValue(minConstructionYear)
        viewModel.maxBuiltYear.postValue(maxConstructionYear)

        viewModel.minDateFirstPosted.postValue(minDateFirstPosted)

        viewModel.xListingPrice.postValue(xListingPrice)
        viewModel.hasVirtualTours.postValue(hasVirtualTours)
        viewModel.hasDroneViews.postValue(hasDroneViews)
        viewModel.ownerCertification.postValue(ownerCertification)
        viewModel.exclusiveListing.postValue(exclusiveListing)

        // The change of this attribute trigger the refresh
        viewModel.externalFilterCount.postValue(externalFilterCount.toString())
    }

    private fun isMultiSelectDefault(param: String?): Boolean {
        return param == null || param.isEmpty() || TextUtils.equals(param, "-1")
    }

    override fun onBackPressed() {
        if (viewModel.listMode.value == ListingsViewModel.ListMode.SELECTION) {
            if (viewModel.selectedListItems.value?.isNotEmpty() == true) {
                confirmQuitSelectMode()
            } else {
                viewModel.selectMode.postValue(null)
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun confirmQuitSelectMode() {
        dialogUtil.showActionDialog(R.string.dialog_message_confirm_quit_select_mode) {
            viewModel.selectMode.postValue(null)
        }
    }

    companion object {
        const val DEFAULT_PROPERTY_TYPE_BUTTONS_COUNT = 3

        const val REQUEST_CODE_FILTER_LISTINGS = 1
        const val REQUEST_CODE_SEARCH = 2
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_listings
    }

    override fun getViewModelClass(): Class<ListingsViewModel> {
        return ListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}