package sg.searchhouse.agentconnect.view.fragment.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.edit_text_search_white.view.*
import kotlinx.android.synthetic.main.fragment_search_common.*
import kotlinx.android.synthetic.main.layout_list.view.*
import kotlinx.android.synthetic.main.layout_search_listing_shortcut.view.*
import kotlinx.android.synthetic.main.radio_group_ownership.view.*
import kotlinx.android.synthetic.main.tab_layout_search_residential_property_type.*
import sg.searchhouse.agentconnect.databinding.FragmentSearchCommonBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnTextChangedListener
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.SearchEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.event.search.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.adapter.search.SearchAutoCompleteAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.helper.search.NotifyLaunchSearchResultHelper
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

/**
 * Search listings
 */
abstract class SearchCommonFragment :
    ViewModelFragment<SearchCommonViewModel, FragmentSearchCommonBinding>() {
    companion object {
        const val EXTRA_KEY_PROPERTY_PURPOSE = "EXTRA_KEY_PROPERTY_PURPOSE"
        const val EXTRA_KEY_SEARCH_RESULT_TYPE = "EXTRA_KEY_SEARCH_RESULT_TYPE"
    }

    private var expandMode: SearchActivity.ExpandMode? = null

    private val adapter = SearchAutoCompleteAdapter { locationEntryPO ->
        val searchResultType = viewModel.searchResultType.value ?: return@SearchAutoCompleteAdapter
        val locationEntryType: SearchEnum.LocationEntryType = try {
            SearchEnum.LocationEntryType.valueOf(locationEntryPO.type)
        } catch (e: IllegalArgumentException) {
            return@SearchAutoCompleteAdapter ErrorUtil.handleError(
                "Invalid LocationEntryType: ${locationEntryPO.type}",
                e
            )
        }
        when (locationEntryType) {
            SearchEnum.LocationEntryType.DISTRICT -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByDistrictId(
                    searchResultType,
                    locationEntryPO.displayText,
                    locationEntryPO.id.toString()
                )
            }
            SearchEnum.LocationEntryType.HDB_ESTATE -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByHdbTownId(
                    searchResultType,
                    locationEntryPO.displayText,
                    locationEntryPO.id.toString()
                )
            }
            SearchEnum.LocationEntryType.MRT -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByAmenityId(
                    searchResultType,
                    locationEntryPO.displayText,
                    locationEntryPO.id.toString(),
                    propertyMainType = getGlobalPropertyMainType()
                )
            }
            SearchEnum.LocationEntryType.HDB -> {
                notifyLaunchByPropertyMainType(
                    ListingEnum.PropertyMainType.HDB,
                    searchResultType,
                    locationEntryPO
                )
            }
            SearchEnum.LocationEntryType.CONDO -> {
                notifyLaunchByPropertyMainType(
                    ListingEnum.PropertyMainType.CONDO,
                    searchResultType,
                    locationEntryPO
                )
            }
            SearchEnum.LocationEntryType.LANDED -> {
                notifyLaunchByPropertyMainType(
                    ListingEnum.PropertyMainType.LANDED,
                    searchResultType,
                    locationEntryPO
                )
            }
            SearchEnum.LocationEntryType.COMMERCIAL -> {
                notifyLaunchByPropertyMainType(
                    ListingEnum.PropertyMainType.COMMERCIAL,
                    searchResultType,
                    locationEntryPO
                )
            }
            else -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByQuery(
                    searchResultType,
                    locationEntryPO.displayText,
                    getGlobalPropertyMainType()
                )
            }
        }
    }

    private fun notifyLaunchByPropertyMainType(
        propertyMainType: ListingEnum.PropertyMainType,
        searchResultType: SearchCommonViewModel.SearchResultType,
        locationEntryPO: LocationEntryPO
    ) {
        when (searchResultType) {
            SearchCommonViewModel.SearchResultType.TRANSACTIONS -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByPropertyMainType(
                    searchResultType,
                    propertyMainType,
                    locationEntryPO.displayText
                )
            }
            SearchCommonViewModel.SearchResultType.LISTINGS -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByPropertyMainType(
                    searchResultType,
                    propertyMainType,
                    locationEntryPO.displayText
                )
            }
            SearchCommonViewModel.SearchResultType.PROJECTS -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByPropertyMainType(
                    searchResultType,
                    propertyMainType,
                    locationEntryPO.displayText
                )
            }
            SearchCommonViewModel.SearchResultType.NEW_LAUNCHES_REPORTS -> {
                NotifyLaunchSearchResultHelper.notifyLaunchByPropertyMainType(
                    searchResultType,
                    propertyMainType,
                    locationEntryPO.displayText
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchCommonViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArguments()
        setupViews()
        listenSearchInput()
        listenRxBuses()
        listenLiveData()
        setupOnClickListeners()
    }

    private fun setupViews() {
        setupList()
        setupOwnershipToggle()
    }

    // Setup sale/rent toggle
    private fun setupOwnershipToggle() {
        radio_group_ownership_type.rb_room_rental.visibility =
            View.GONE // Exclude room rental as ownership type
        radio_group_ownership_type.onSelectOwnershipListener = { ownershipType ->
            // Update activity ownership type
            publishRxBus(UpdateActivityOwnershipTypeEvent(ownershipType))
        }
    }

    private fun setupList() {
        layout_list.list.layoutManager = LinearLayoutManager(context)
        layout_list.list.adapter = adapter
    }

    // Get property main type from tab
    private fun getGlobalPropertyMainType(): ListingEnum.PropertyMainType? {
        val propertyType = viewModel.propertyType.value ?: return null

        val isResidential =
            viewModel.propertyPurpose.value == ListingEnum.PropertyPurpose.RESIDENTIAL
        val isTransaction =
            viewModel.searchResultType.value == SearchCommonViewModel.SearchResultType.TRANSACTIONS

        return if (isResidential && isTransaction) {
            propertyType
        } else {
            null
        }
    }

    private fun listenLiveData() {
        viewModel.currentLocation.observe(viewLifecycleOwner, Observer { currentLocation ->
            if (currentLocation != null) {
                layout_search_listing_shortcut.binding.isSearchingLocation = false
                val searchResultType = viewModel.searchResultType.value ?: return@Observer
                NotifyLaunchSearchResultHelper.notifyLaunchByQuery(
                    searchResultType,
                    currentLocation.address,
                    getGlobalPropertyMainType()
                )
                viewModel.currentLocation.postValue(null)
            }
        })

        viewModel.getCurrentLocationStatus.observe(viewLifecycleOwner, Observer { statusKey ->
            // Proceed anyway if get non-success response
            if (statusKey != null && statusKey != ApiStatus.StatusKey.SUCCESS && viewModel.currentLocation.value == null) {
                layout_search_listing_shortcut.binding.isSearchingLocation = false
                val searchResultType = viewModel.searchResultType.value ?: return@Observer
                NotifyLaunchSearchResultHelper.notifyLaunchByQuery(
                    searchResultType,
                    "",
                    getGlobalPropertyMainType()
                )
            }
        })
        viewModel.displayMode.observe(viewLifecycleOwner, Observer {
            when (it) {
                SearchCommonViewModel.DisplayMode.SEARCH -> {
                    closeSearchPanel(expandMode == SearchActivity.ExpandMode.COMPACT)
                    showHideAppBar(false)
                    et_search.requestFocus()
                    ViewUtil.showKeyboard(et_search.edit_text)
                }
                SearchCommonViewModel.DisplayMode.DEFAULT -> {
                    closeSearchPanel(true)
                    et_search.edit_text.setText("")
                    showHideAppBar(true)
                    ViewUtil.hideKeyboard(et_search.edit_text)
                }
                else -> {
                }
            }
        })

        viewModel.searchText.observe(viewLifecycleOwner, Observer {
            if (!TextUtils.isEmpty(it?.trim())) {
                viewModel.maybePerformRequest()
            }
        })

        viewModel.suggestions.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                val list = arrayListOf<Any>()
                it.map { pair ->
                    list.add(SearchAutoCompleteAdapter.EntryType(pair.first))
                    list.addAll(pair.second)
                }
                adapter.items = list
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.searchResultType.observe(viewLifecycleOwner, Observer {
            // Load list if it is not loaded during on resume due to absence of searchResultType
            if (layout_search_listing_shortcut.layout_search_history.childCount == 0) {
                layout_search_listing_shortcut.populateSearchHistory(it)
            }

            when (it) {
                SearchCommonViewModel.SearchResultType.PROJECTS,
                SearchCommonViewModel.SearchResultType.NEW_LAUNCHES_REPORTS -> {
                    //Note: by requirement - show directly search mode
                    viewModel.displayMode.postValue(SearchCommonViewModel.DisplayMode.SEARCH)
                }
                else -> {
                    //Note: source is from dashboard  -> show default mode
                    //source is from search result -> show search mode
                    when (expandMode) {
                        SearchActivity.ExpandMode.COMPACT -> viewModel.displayMode.postValue(
                            SearchCommonViewModel.DisplayMode.SEARCH
                        )
                        else -> viewModel.displayMode.postValue(SearchCommonViewModel.DisplayMode.DEFAULT)
                    }
                }
            }
        })
        viewModel.propertyPurpose.observe(viewLifecycleOwner, Observer { propertyPurpose ->
            propertyPurpose?.run {
                viewModel.searchResultType.value?.let { searchResultType ->
                    resetPropertyType(this, searchResultType)
                }
                // TODO Maybe move `populatePropertyMainTypes()` somewhere else
                // it been here because there was `propertyPurpose` param
                populatePropertyMainTypes()
            }
        })
        viewModel.propertyType.observe(viewLifecycleOwner, Observer {
            RxBus.publish(UpdateSearchFragmentParamEvent(it))
        })
    }

    private fun showHideAppBar(isShow: Boolean) {
        RxBus.publish(DisplaySearchAppBarEvent(isShow))
    }

    private fun closeSearchPanel(isClose: Boolean) {
        RxBus.publish(CloseSearchPanelEvent(isClose))
    }

    private fun listenSearchInput() {
        et_search.edit_text.setOnKeyListener { _, keyCode, keyEvent ->
            when (keyEvent.action) {
                KeyEvent.ACTION_UP -> {
                    when (keyCode) {
                        KeyEvent.KEYCODE_ENTER -> {
                            val searchResultType =
                                viewModel.searchResultType.value ?: return@setOnKeyListener true
                            NotifyLaunchSearchResultHelper.notifyLaunchByQuery(
                                searchResultType,
                                getSearchBoxQuery(),
                                getGlobalPropertyMainType()
                            )
                            true
                        }
                        else -> false
                    }
                }
                else -> false
            }
        }

        et_search.edit_text.setOnTextChangedListener {
            viewModel.searchText.postValue(it)
        }
    }

    private fun setupOnClickListeners() {
        val mActivity = activity ?: return
        layout_search_listing_shortcut.setupOnClickListeners(mActivity) {
            viewModel.findCurrentLocation(it)
        }
        btn_search.setOnClickListener {
            layout_search_listing_shortcut.checkListingAndTransactionAccessibility(
                searchResultType = viewModel.searchResultType.value,
                onSuccessAccessibility = {
                    viewModel.displayMode.postValue(
                        SearchCommonViewModel.DisplayMode.SEARCH
                    )
                })
        }

        btn_cancel.setOnClickListener {
            updateDisplayModeBySearchResultType(isCancel = true)
        }
        layout_list.list.setOnTouchListener { _, _ ->
            ViewUtil.hideKeyboard(et_search.edit_text)
            false
        }
        scroll_view.setOnTouchListener { _, _ ->
            ViewUtil.hideKeyboard(et_search.edit_text)
            false
        }

        btn_tab_hdb.setOnClickListener { viewModel.propertyType.postValue(ListingEnum.PropertyMainType.HDB) }
        btn_tab_condo.setOnClickListener { viewModel.propertyType.postValue(ListingEnum.PropertyMainType.CONDO) }
        btn_tab_landed.setOnClickListener { viewModel.propertyType.postValue(ListingEnum.PropertyMainType.LANDED) }
    }

    //TODO: in future revamp.
    private fun updateDisplayModeBySearchResultType(isCancel: Boolean = false) {
        when (viewModel.searchResultType.value ?: return) {
            SearchCommonViewModel.SearchResultType.PROJECTS,
            SearchCommonViewModel.SearchResultType.NEW_LAUNCHES_REPORTS -> {
                if (isCancel) {
                    RxBus.publish(NotifyToCloseSearchPanelEvent())
                }
            }
            else -> {
                if (isCancel && expandMode == SearchActivity.ExpandMode.COMPACT) {
                    RxBus.publish(NotifyToCloseSearchPanelEvent())
                } else if (expandMode == SearchActivity.ExpandMode.COMPACT) {
                    viewModel.displayMode.postValue(SearchCommonViewModel.DisplayMode.SEARCH)
                } else {
                    viewModel.displayMode.postValue(SearchCommonViewModel.DisplayMode.DEFAULT)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateDisplayModeBySearchResultType()
        viewModel.searchResultType.value?.run {
            layout_search_listing_shortcut.populateSearchHistory(
                this
            )
        }
        publishRxBus(RequestOwnershipTypeEvent())
    }

    private fun getCurrentLocation() {
        layout_search_listing_shortcut.getCurrentLocation { viewModel.findCurrentLocation(it) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_LOCATION -> {
                val fineLocationIndex = permissions.indexOfFirst {
                    TextUtils.equals(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
                val coarseLocationIndex = permissions.indexOfFirst {
                    TextUtils.equals(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }
                if (fineLocationIndex != -1 && coarseLocationIndex != -1) {
                    if (grantResults[fineLocationIndex] == PackageManager.PERMISSION_GRANTED
                        && grantResults[coarseLocationIndex] == PackageManager.PERMISSION_GRANTED
                    ) {
                        getCurrentLocation()
                    }
                }
            }
        }
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateSearchPropertyTypeEvent::class.java) {
            expandMode = it.expandMode
            viewModel.propertyPurpose.postValue(it.propertyPurpose)
        }
        listenRxBus(CancelSearchModeEvent::class.java) {
            updateDisplayModeBySearchResultType()
        }
        listenRxBus(UpdateFragmentOwnershipTypeEvent::class.java) {
            radio_group_ownership_type?.setValue(it.ownershipType)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun setupArguments() {
        val propertyPurpose =
            arguments?.getSerializable(EXTRA_KEY_PROPERTY_PURPOSE) as ListingEnum.PropertyPurpose?
                ?: throw IllegalArgumentException("Must pass property purpose into instance")
        viewModel.propertyPurpose.postValue(propertyPurpose)

        val searchResultType =
            arguments?.getSerializable(EXTRA_KEY_SEARCH_RESULT_TYPE) as SearchCommonViewModel.SearchResultType?
                ?: throw IllegalArgumentException("Must pass search result type into instance")
        viewModel.searchResultType.postValue(searchResultType)
        resetPropertyType(propertyPurpose, searchResultType)
    }

    private fun resetPropertyType(
        propertyPurpose: ListingEnum.PropertyPurpose,
        searchResultType: SearchCommonViewModel.SearchResultType
    ) {
        val isResidentialTransactions =
            propertyPurpose == ListingEnum.PropertyPurpose.RESIDENTIAL && searchResultType == SearchCommonViewModel.SearchResultType.TRANSACTIONS
        viewModel.propertyType.postValue(
            if (isResidentialTransactions) {
                ListingEnum.PropertyMainType.CONDO
            } else {
                ListingEnum.PropertyMainType.COMMERCIAL
            }
        )
    }

    private fun populatePropertyMainTypes() {
        layout_search_listing_shortcut.setupPropertyMainTypes(
            enterListingsByPropertyMainType = { propertyMainType ->
                viewModel.searchResultType.value?.run {
                    layout_search_listing_shortcut.checkListingAndTransactionAccessibility(
                        searchResultType = this
                    ) {
                        NotifyLaunchSearchResultHelper.notifyLaunchByPropertyMainType(
                            this,
                            propertyMainType,
                            ""
                        )
                    }
                }
            },
            enterListingsByPropertySubType = { propertySubType ->
                viewModel.searchResultType.value?.run {
                    layout_search_listing_shortcut.checkListingAndTransactionAccessibility(
                        searchResultType = this
                    ) {
                        NotifyLaunchSearchResultHelper.notifyLaunchByPropertySubType(
                            this,
                            propertySubType,
                            ""
                        )
                    }
                }
            },
            enterTransactionsByPropertySubType = { propertySubType ->
                viewModel.searchResultType.value?.run {
                    layout_search_listing_shortcut.checkListingAndTransactionAccessibility(
                        searchResultType = this
                    ) {
                        NotifyLaunchSearchResultHelper.notifyLaunchByPropertySubType(
                            this,
                            propertySubType,
                            ""
                        )
                    }
                }
            })
    }

    private fun getSearchBoxQuery(): String {
        return et_search.edit_text.text.toString()
    }
}