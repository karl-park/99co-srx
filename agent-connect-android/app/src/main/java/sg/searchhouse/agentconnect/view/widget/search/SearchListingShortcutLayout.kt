package sg.searchhouse.agentconnect.view.widget.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.layout_buttons_property_type_listings_commercial.view.*
import kotlinx.android.synthetic.main.layout_buttons_property_type_listings_residential.view.*
import kotlinx.android.synthetic.main.layout_buttons_property_type_transactions_commercial.view.*
import kotlinx.android.synthetic.main.layout_project_directory_shortcut.view.*
import kotlinx.android.synthetic.main.layout_search_listing_shortcut.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutSearchListingShortcutBinding
import sg.searchhouse.agentconnect.dependency.component.DaggerViewComponent
import sg.searchhouse.agentconnect.dependency.module.ViewModule
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectDirectoryActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.DistrictSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.HdbTownSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.MrtSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.SchoolSearchActivity
import sg.searchhouse.agentconnect.view.helper.search.NotifyLaunchSearchResultHelper
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel
import javax.inject.Inject

class SearchListingShortcutLayout constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var binding: LayoutSearchListingShortcutBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_search_listing_shortcut,
        this,
        true
    )

    @Inject
    lateinit var locationUtil: LocationUtil

    @Inject
    lateinit var dialogUtil: DialogUtil

    init {
        val viewComponent =
            DaggerViewComponent.builder().viewModule(ViewModule(context)).build()
        viewComponent.inject(this)
    }

    fun setupPropertyMainTypes(
        enterListingsByPropertyMainType: (ListingEnum.PropertyMainType) -> Unit,
        enterListingsByPropertySubType: (ListingEnum.PropertySubType) -> Unit,
        enterTransactionsByPropertySubType: (ListingEnum.PropertySubType) -> Unit
    ) {
        setResidentialListingsOnClickListeners(enterListingsByPropertyMainType)
        setCommercialListingsOnClickListeners(enterListingsByPropertySubType)
        setCommercialTransactionsOnClickListeners(enterTransactionsByPropertySubType)
    }

    private fun setResidentialListingsOnClickListeners(enterListingsByPropertyMainType: (ListingEnum.PropertyMainType) -> Unit) {
        layout_buttons_property_type_listings_residential.run {
            btn_property_type_all_residential.setOnClickListener {
                enterListingsByPropertyMainType.invoke(
                    ListingEnum.PropertyMainType.RESIDENTIAL
                )
            }
            btn_property_type_hdb.setOnClickListener {
                enterListingsByPropertyMainType.invoke(
                    ListingEnum.PropertyMainType.HDB
                )
            }
            btn_property_type_condo.setOnClickListener {
                enterListingsByPropertyMainType.invoke(
                    ListingEnum.PropertyMainType.CONDO
                )
            }
            btn_property_type_landed.setOnClickListener {
                enterListingsByPropertyMainType.invoke(
                    ListingEnum.PropertyMainType.LANDED
                )
            }
        }
    }

    private fun setCommercialListingsOnClickListeners(enterListingsByPropertySubType: (ListingEnum.PropertySubType) -> Unit) {
        layout_buttons_property_type_listings_commercial.run {
            btn_listings_sub_type_all_commercial.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.ALL_COMMERCIAL
                )
            }
            btn_listings_sub_type_retail.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.RETAIL
                )
            }
            btn_listings_sub_type_office.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.OFFICE
                )
            }
            btn_listings_sub_type_factory.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.FACTORY
                )
            }
            btn_listings_sub_type_warehouse.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.WAREHOUSE
                )
            }
            btn_listings_sub_type_land.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.LAND
                )
            }
            btn_listings_sub_type_hdb_shop_house.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.HDB_SHOP_HOUSE
                )
            }
            btn_listings_sub_type_shop_house.setOnClickListener {
                enterListingsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.SHOP_HOUSE
                )
            }
        }
    }

    private fun setCommercialTransactionsOnClickListeners(enterTransactionsByPropertySubType: (ListingEnum.PropertySubType) -> Unit) {
        layout_buttons_property_type_transactions.run {
            btn_transactions_sub_type_retail.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.RETAIL
                )
            }
            btn_transactions_sub_type_office.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.OFFICE
                )
            }
            btn_transactions_sub_type_factory.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.FACTORY
                )
            }
            btn_transactions_sub_type_warehouse.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.WAREHOUSE
                )
            }
            btn_transactions_sub_type_land.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.LAND
                )
            }
            btn_transactions_sub_type_hdb_shop_house.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.HDB_SHOP_HOUSE
                )
            }
            btn_transactions_sub_type_shop_house.setOnClickListener {
                enterTransactionsByPropertySubType.invoke(
                    ListingEnum.PropertySubType.SHOP_HOUSE
                )
            }
        }
    }

    fun populateSearchHistory(searchResultType: SearchCommonViewModel.SearchResultType) {
        // TODO: Make this binded recyclerview
        try {
            val searchHistory = SearchHistoryUtil.getHistory(searchResultType.sharedPreferenceKey)
            layout_search_history.removeAllViews()
            searchHistory.map { entry ->
                val searchHistoryListItem = SearchHistoryListItem(context)
                searchHistoryListItem.populate(entry) { label, value, searchType ->
                    when (searchType) {
                        SearchHistoryUtil.SearchType.AMENITY_ID -> {
                            // TODO Get global property type from `SearchCommonFragment`
                            NotifyLaunchSearchResultHelper.notifyLaunchByAmenityId(
                                searchResultType,
                                label,
                                value,
                                null
                            )
                        }
                        SearchHistoryUtil.SearchType.HDB_TOWN_ID -> NotifyLaunchSearchResultHelper.notifyLaunchByHdbTownId(
                            searchResultType,
                            label,
                            value
                        )
                        SearchHistoryUtil.SearchType.DISTRICT_ID -> NotifyLaunchSearchResultHelper.notifyLaunchByDistrictId(
                            searchResultType,
                            label,
                            value
                        )
                        SearchHistoryUtil.SearchType.QUERY -> {
                            // TODO Get global property type from `SearchCommonFragment`
                            NotifyLaunchSearchResultHelper.notifyLaunchByQuery(
                                searchResultType,
                                value,
                                null
                            )
                        }
                        SearchHistoryUtil.SearchType.PROPERTY_MAIN_TYPE -> {
                            NotifyLaunchSearchResultHelper.notifyLaunchByPropertyMainType(
                                searchResultType,
                                ListingEnum.PropertyMainType.valueOf(value),
                                label
                            )
                        }
                    }
                }
                layout_search_history.addView(searchHistoryListItem)
            }
        } catch (e: IllegalArgumentException) {
            ErrorUtil.handleError(context, "Error get search history", e)
        }
    }

    fun getCurrentLocation(getLocationName: (Location) -> Unit) {
        if (locationUtil.getCurrentLocation(getLocationName)) {
            binding.isSearchingLocation = true
        }
    }

    fun setupOnClickListeners(activity: Activity, getLocationName: (Location) -> Unit) {
        btn_current_location.setOnClickListener {
            checkListingAndTransactionAccessibility {
                maybeGetCurrentLocation(
                    activity,
                    getLocationName
                )
            }
        }
        btn_search_by_mrt.setOnClickListener {
            checkListingAndTransactionAccessibility {
                launchLookupActivity(
                    activity,
                    MrtSearchActivity::class.java,
                    SearchActivity.REQUEST_CODE_SEARCH_LISTINGS_BY_MRT,
                    SearchActivity.REQUEST_CODE_SEARCH_TRANSACTIONS_BY_MRT,
                    ProjectSearchActivity.REQUEST_CODE_SEARCH_PROJECTS_BY_MRT
                )
            }
        }
        btn_search_by_districts.setOnClickListener {
            checkListingAndTransactionAccessibility {
                launchLookupActivity(
                    activity,
                    DistrictSearchActivity::class.java,
                    SearchActivity.REQUEST_CODE_SEARCH_LISTINGS_BY_DISTRICT,
                    SearchActivity.REQUEST_CODE_SEARCH_TRANSACTIONS_BY_DISTRICT,
                    ProjectSearchActivity.REQUEST_CODE_SEARCH_PROJECTS_BY_DISTRICT
                )
            }
        }
        btn_search_by_areas.setOnClickListener {
            checkListingAndTransactionAccessibility {
                launchLookupActivity(
                    activity,
                    HdbTownSearchActivity::class.java,
                    SearchActivity.REQUEST_CODE_SEARCH_LISTINGS_BY_HDB_TOWN,
                    SearchActivity.REQUEST_CODE_SEARCH_TRANSACTIONS_BY_HDB_TOWN,
                    ProjectSearchActivity.REQUEST_CODE_SEARCH_PROJECTS_BY_HDB_TOWN
                )
            }
        }
        btn_search_by_schools.setOnClickListener {
            checkListingAndTransactionAccessibility {
                launchLookupActivity(
                    activity,
                    SchoolSearchActivity::class.java,
                    SearchActivity.REQUEST_CODE_SEARCH_LISTINGS_BY_SCHOOL,
                    SearchActivity.REQUEST_CODE_SEARCH_TRANSACTIONS_BY_SCHOOL,
                    ProjectSearchActivity.REQUEST_CODE_SEARCH_PROJECTS_BY_SCHOOL
                )
            }
        }

        layout_info_shortcut.setOnClickListener {
            val propertyPurpose =
                binding.propertyPurpose ?: ListingEnum.PropertyPurpose.RESIDENTIAL

            val module = when (propertyPurpose) {
                ListingEnum.PropertyPurpose.RESIDENTIAL -> AccessibilityEnum.AdvisorModule.PROJECT_INFO
                ListingEnum.PropertyPurpose.COMMERCIAL -> AccessibilityEnum.AdvisorModule.PROJECT_INFO_COMMERCIAL
            }

            //Note: to project directory
            AuthUtil.checkModuleAccessibility(
                module = module,
                onSuccessAccessibility = {
                    ProjectDirectoryActivity.launch(
                        activity,
                        ProjectDirectoryActivity.Source.SEARCH_SHORTCUT,
                        propertyPurpose
                    )
                }
            )
        }
    }

    fun checkListingAndTransactionAccessibility(
        searchResultType: SearchCommonViewModel.SearchResultType? = null,
        onSuccessAccessibility: (() -> Unit)
    ) {
        val module = when (searchResultType ?: binding.searchResultType) {
            SearchCommonViewModel.SearchResultType.LISTINGS -> AccessibilityEnum.AdvisorModule.LISTING_SEARCH
            SearchCommonViewModel.SearchResultType.TRANSACTIONS -> when (binding.propertyPurpose) {
                ListingEnum.PropertyPurpose.COMMERCIAL -> AccessibilityEnum.AdvisorModule.TRANSACTION_SEARCH_COMMERCIAL
                else -> AccessibilityEnum.AdvisorModule.TRANSACTION_SEARCH
            }
            SearchCommonViewModel.SearchResultType.PROJECTS -> when (binding.propertyPurpose) {
                ListingEnum.PropertyPurpose.COMMERCIAL -> AccessibilityEnum.AdvisorModule.PROJECT_INFO_COMMERCIAL
                else -> AccessibilityEnum.AdvisorModule.PROJECT_INFO
            }
            SearchCommonViewModel.SearchResultType.NEW_LAUNCHES_REPORTS -> AccessibilityEnum.AdvisorModule.NEW_PROJECT
            else -> null
        } ?: return

        AuthUtil.checkModuleAccessibility(
            module = module,
            onSuccessAccessibility = { onSuccessAccessibility.invoke() })
    }

    private fun launchLookupActivity(
        activity: Activity,
        classy: Class<*>,
        listingsRequestCode: Int,
        transactionsRequestCode: Int,
        projectRequestCode: Int
    ) {
        when (binding.searchResultType) {
            SearchCommonViewModel.SearchResultType.LISTINGS -> listingsRequestCode
            SearchCommonViewModel.SearchResultType.TRANSACTIONS -> transactionsRequestCode
            SearchCommonViewModel.SearchResultType.PROJECTS -> projectRequestCode
            SearchCommonViewModel.SearchResultType.NEW_LAUNCHES_REPORTS -> projectRequestCode
            else -> null
        }?.let {
            val intent = Intent(activity, classy)
            activity.startActivityForResult(intent, it)
        }
    }

    private fun maybeGetCurrentLocation(activity: Activity, getLocationName: (Location) -> Unit) {
        val isPermissionGranted = PermissionUtil.maybeRequestLocationPermissions(activity)
        if (isPermissionGranted) {
            getCurrentLocation(getLocationName)
        }
    }
}