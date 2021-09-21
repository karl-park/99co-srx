package sg.searchhouse.agentconnect.view.helper.search

import android.content.Intent
import android.text.TextUtils
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.event.search.*
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.SearchHistoryUtil
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.DistrictSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.HdbTownSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.MrtSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.SchoolSearchActivity
import sg.searchhouse.agentconnect.viewmodel.activity.listing.SearchViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

// Launch result e.g. listings, transactions from SearchActivity
class LaunchSearchResultHelper(
    private val searchActivity: SearchActivity,
    private val viewModel: SearchViewModel
) {
    fun launchFromQuery(
        showQueryResultEvent: ShowQueryResultEvent,
        globalPropertyMainType: ListingEnum.PropertyMainType? = null
    ) {
        when (showQueryResultEvent.searchResultType) {
            SearchCommonViewModel.SearchResultType.LISTINGS -> {
                launchFromQueryCore(
                    showQueryResultEvent.query,
                    showQueryResultEvent.propertyMainType,
                    showQueryResultEvent.searchResultType
                ) { _, query, propertyPurpose, ownershipType, _, _ ->
                    SearchResultActivityEntry.launchListingsBySearchText(
                        searchActivity,
                        query,
                        propertyPurpose,
                        ownershipType
                    )
                }
            }
            SearchCommonViewModel.SearchResultType.TRANSACTIONS -> {
                if (!TextUtils.isEmpty(showQueryResultEvent.query.trim())) {
                    // TODO: Refactor when free
                    launchFromQueryCore(
                        showQueryResultEvent.query,
                        showQueryResultEvent.propertyMainType,
                        showQueryResultEvent.searchResultType
                    ) { _, query, propertyPurpose, ownershipType, propertyMainType, propertySubType ->
                        val thisPropertyMainType = propertyMainType
                            ?: globalPropertyMainType
                            ?: throw IllegalArgumentException("Missing `propertyMainType` in transaction `ShowLookupResultEvent`")
                        val consistentPropertyMainType =
                            getConsistentPropertyMainType(thisPropertyMainType, propertyPurpose)
                        TransactionsActivityEntry.launchBySearchText(
                            searchActivity,
                            query,
                            propertyPurpose,
                            ownershipType,
                            consistentPropertyMainType,
                            propertySubType
                        )
                    }
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    fun launchFromQueryAndPropertySubType(
        showQueryPropertySubTypeResultEvent: ShowQueryPropertySubTypeResultEvent,
        globalPropertyMainType: ListingEnum.PropertyMainType?
    ) {
        launchFromQueryCore(
            showQueryPropertySubTypeResultEvent.query,
            null,
            SearchCommonViewModel.SearchResultType.TRANSACTIONS
        ) { _, query, propertyPurpose, ownershipType, propertyMainType, _ ->
            val thisPropertyMainType = propertyMainType
                ?: globalPropertyMainType
                ?: throw IllegalArgumentException("Missing `propertyMainType` in transaction `ShowLookupResultEvent`")
            val consistentPropertyMainType =
                getConsistentPropertyMainType(thisPropertyMainType, propertyPurpose)
            TransactionsActivityEntry.launchBySearchTextAndPropertySubType(
                searchActivity,
                query,
                showQueryPropertySubTypeResultEvent.propertySubType,
                propertyPurpose,
                ownershipType,
                consistentPropertyMainType
            )
        }
    }

    private fun launchFromQueryCore(
        query: String,
        propertyMainType: ListingEnum.PropertyMainType?,
        searchResultType: SearchCommonViewModel.SearchResultType, launchAction: (
            searchResultType: SearchCommonViewModel.SearchResultType,
            query: String,
            propertyPurpose: ListingEnum.PropertyPurpose,
            ownershipType: ListingEnum.OwnershipType,
            propertyMainType: ListingEnum.PropertyMainType?,
            propertySubType: ListingEnum.PropertySubType?
        ) -> Unit
    ) {
        // TODO Handle property main and sub type
        // Add the current query into history
        SearchHistoryUtil.maybeAddHistory(
            searchActivity,
            query,
            query,
            SearchHistoryUtil.SearchType.QUERY,
            searchResultType.sharedPreferenceKey
        )

        val propertyPurpose = viewModel.propertyPurpose.value ?: return ErrorUtil.handleError(
            "Missing property purpose"
        )
        val ownershipType = viewModel.ownershipType.value ?: return ErrorUtil.handleError(
            "Missing ownership type"
        )

        val consistentPropertyMainType = propertyMainType?.run {
            getConsistentPropertyMainType(
                this,
                propertyPurpose
            )
        }

        launchAction.invoke(
            searchResultType,
            query,
            propertyPurpose,
            ownershipType,
            consistentPropertyMainType,
            null
        )
    }

    /**
     * Override main type by purpose
     */
    private fun getConsistentPropertyMainType(
        propertyMainType: ListingEnum.PropertyMainType,
        propertyPurpose: ListingEnum.PropertyPurpose
    ): ListingEnum.PropertyMainType {
        return when (propertyPurpose) {
            ListingEnum.PropertyPurpose.RESIDENTIAL -> propertyMainType
            else -> ListingEnum.PropertyMainType.COMMERCIAL
        }
    }

    fun launchFromPropertyMainType(
        showPropertyMainTypeResultEvent: ShowPropertyMainTypeResultEvent
    ) {
        val propertyPurpose = viewModel.propertyPurpose.value ?: return ErrorUtil.handleError(
            "Missing property purpose"
        )
        val ownershipType = viewModel.ownershipType.value ?: return ErrorUtil.handleError(
            "Missing ownership type"
        )

        val propertyMainType = getConsistentPropertyMainType(
            showPropertyMainTypeResultEvent.propertyMainType,
            propertyPurpose
        )

        if (!TextUtils.isEmpty(showPropertyMainTypeResultEvent.query)) {
            // Add the current query into history
            SearchHistoryUtil.maybeAddHistory(
                searchActivity,
                showPropertyMainTypeResultEvent.query ?: "",
                propertyMainType.name,
                SearchHistoryUtil.SearchType.PROPERTY_MAIN_TYPE,
                showPropertyMainTypeResultEvent.searchResultType.sharedPreferenceKey
            )
        }

        SearchResultActivityEntry.launchByPropertyMainType(
            searchActivity,
            showPropertyMainTypeResultEvent.searchResultType.activityClass,
            showPropertyMainTypeResultEvent.query,
            propertyMainType,
            propertyPurpose,
            ownershipType
        )
    }

    fun launchFromPropertySubType(
        showPropertySubTypeResultEvent: ShowPropertySubTypeResultEvent
    ) {
        val propertyPurpose = viewModel.propertyPurpose.value ?: return ErrorUtil.handleError(
            "Missing property purpose"
        )
        val ownershipType = viewModel.ownershipType.value ?: return ErrorUtil.handleError(
            "Missing ownership type"
        )

        when (showPropertySubTypeResultEvent.searchResultType) {
            SearchCommonViewModel.SearchResultType.TRANSACTIONS -> {
                TransactionsActivityEntry.launchByPropertySubType(
                    searchActivity,
                    showPropertySubTypeResultEvent.searchResultType.activityClass,
                    showPropertySubTypeResultEvent.propertySubType,
                    propertyPurpose,
                    ownershipType
                )
            }
            else -> {
                SearchResultActivityEntry.launchByPropertySubType(
                    searchActivity,
                    showPropertySubTypeResultEvent.searchResultType.activityClass,
                    showPropertySubTypeResultEvent.propertySubType,
                    propertyPurpose,
                    ownershipType
                )
            }
        }
    }

    fun launchFromHdbTowns(
        searchResultType: SearchCommonViewModel.SearchResultType,
        data: Intent?,
        propertyMainType: ListingEnum.PropertyMainType? = null
    ) {
        val ids =
            data?.getStringExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_IDS)
                ?: return
        val names =
            data.getStringExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_NAMES)
                ?: return
        launchFromLookupResult(
            ShowLookupResultEvent(
                ShowLookupResultEvent.LookupType.HDB_TOWN_ID,
                searchResultType,
                ids,
                names,
                propertyMainType = propertyMainType
            )
        )
    }

    private fun launchFromAmenities(
        searchResultType: SearchCommonViewModel.SearchResultType,
        ids: String,
        names: String,
        propertyMainType: ListingEnum.PropertyMainType? = null
    ) {
        val event = ShowLookupResultEvent(
            ShowLookupResultEvent.LookupType.AMENITY_ID,
            searchResultType,
            ids,
            names,
            propertyMainType = propertyMainType
        )
        launchFromLookupResult(event)
    }

    fun launchListingsFromMrts(data: Intent?) {
        val mrtStationIds =
            data?.getStringExtra(MrtSearchActivity.EXTRA_MRT_STATION_IDS) ?: return
        val mrtStationNames =
            data.getStringExtra(MrtSearchActivity.EXTRA_MRT_STATION_NAMES) ?: return
        launchFromAmenities(
            SearchCommonViewModel.SearchResultType.LISTINGS,
            mrtStationIds,
            mrtStationNames
        )
    }

    fun launchTransactionsFromMrts(
        data: Intent?,
        propertyMainType: ListingEnum.PropertyMainType?
    ) {
        val mrtStationIds =
            data?.getStringExtra(MrtSearchActivity.EXTRA_MRT_STATION_IDS) ?: return
        val mrtStationNames =
            data.getStringExtra(MrtSearchActivity.EXTRA_MRT_STATION_NAMES) ?: return
        launchFromAmenities(
            SearchCommonViewModel.SearchResultType.TRANSACTIONS,
            mrtStationIds,
            mrtStationNames,
            propertyMainType
        )
    }

    fun launchListingsFromSchools(
        data: Intent?
    ) {
        val schoolIds =
            data?.getStringExtra(SchoolSearchActivity.EXTRA_SELECTED_SCHOOL_IDS) ?: return
        val schoolNames =
            data.getStringExtra(SchoolSearchActivity.EXTRA_SELECTED_SCHOOL_NAMES) ?: return
        launchFromAmenities(
            SearchCommonViewModel.SearchResultType.LISTINGS,
            schoolIds,
            schoolNames
        )
    }

    fun launchTransactionsFromSchools(
        data: Intent?,
        propertyMainType: ListingEnum.PropertyMainType?
    ) {
        val schoolIds =
            data?.getStringExtra(SchoolSearchActivity.EXTRA_SELECTED_SCHOOL_IDS) ?: return
        val schoolNames =
            data.getStringExtra(SchoolSearchActivity.EXTRA_SELECTED_SCHOOL_NAMES) ?: return
        launchFromAmenities(
            SearchCommonViewModel.SearchResultType.TRANSACTIONS,
            schoolIds,
            schoolNames,
            propertyMainType
        )
    }

    fun launchFromDistricts(
        searchResultType: SearchCommonViewModel.SearchResultType,
        data: Intent?,
        propertyMainType: ListingEnum.PropertyMainType? = null
    ) {
        val districtIds =
            data?.getStringExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_IDS)
                ?: return
        val districtNames =
            data.getStringExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_NAMES)
                ?: return
        launchFromLookupResult(
            ShowLookupResultEvent(
                ShowLookupResultEvent.LookupType.DISTRICT_ID,
                searchResultType,
                districtIds,
                districtNames,
                propertyMainType = propertyMainType
            )
        )
    }

    fun launchFromLookupResult(
        showLookupResultEvent: ShowLookupResultEvent,
        globalPropertyMainType: ListingEnum.PropertyMainType? = null
    ) {
        val searchType = when (showLookupResultEvent.lookupType) {
            ShowLookupResultEvent.LookupType.HDB_TOWN_ID -> SearchHistoryUtil.SearchType.HDB_TOWN_ID
            ShowLookupResultEvent.LookupType.DISTRICT_ID -> SearchHistoryUtil.SearchType.DISTRICT_ID
            ShowLookupResultEvent.LookupType.AMENITY_ID -> SearchHistoryUtil.SearchType.AMENITY_ID
        }

        // Add the current query into history
        SearchHistoryUtil.maybeAddHistory(
            searchActivity,
            showLookupResultEvent.labels,
            showLookupResultEvent.ids,
            searchType,
            showLookupResultEvent.searchResultType.sharedPreferenceKey
        )

        val propertyPurpose = viewModel.propertyPurpose.value
            ?: return ErrorUtil.handleError("Missing property purpose")
        val ownershipType = viewModel.ownershipType.value
            ?: return ErrorUtil.handleError("Missing ownership type")

        when (showLookupResultEvent.lookupType) {
            ShowLookupResultEvent.LookupType.HDB_TOWN_ID -> {
                when (showLookupResultEvent.searchResultType) {
                    SearchCommonViewModel.SearchResultType.TRANSACTIONS -> {
                        val propertyMainType = showLookupResultEvent.propertyMainType
                            ?: globalPropertyMainType
                            ?: throw IllegalArgumentException("Missing `propertyMainType` in transaction `ShowLookupResultEvent`")
                        TransactionsActivityEntry.launchByHdbTown(
                            searchActivity,
                            showLookupResultEvent.ids,
                            showLookupResultEvent.labels,
                            propertyPurpose,
                            ownershipType,
                            propertyMainType
                        )
                    }
                    else -> {
                        SearchResultActivityEntry.launchByHdbTown(
                            searchActivity,
                            showLookupResultEvent.searchResultType.activityClass,
                            showLookupResultEvent.ids,
                            showLookupResultEvent.labels,
                            propertyPurpose,
                            ownershipType
                        )
                    }
                }
            }
            ShowLookupResultEvent.LookupType.DISTRICT_ID -> {
                when (showLookupResultEvent.searchResultType) {
                    SearchCommonViewModel.SearchResultType.TRANSACTIONS -> {
                        val propertyMainType = showLookupResultEvent.propertyMainType
                            ?: globalPropertyMainType
                            ?: throw IllegalArgumentException("Missing `propertyMainType` in transaction `ShowLookupResultEvent`")
                        TransactionsActivityEntry.launchByDistrict(
                            searchActivity,
                            showLookupResultEvent.ids,
                            showLookupResultEvent.labels,
                            propertyPurpose,
                            ownershipType,
                            propertyMainType
                        )
                    }
                    else -> {
                        SearchResultActivityEntry.launchByDistrict(
                            searchActivity,
                            showLookupResultEvent.searchResultType.activityClass,
                            showLookupResultEvent.ids,
                            showLookupResultEvent.labels,
                            propertyPurpose,
                            ownershipType
                        )
                    }
                }
            }
            ShowLookupResultEvent.LookupType.AMENITY_ID -> {
                when (showLookupResultEvent.searchResultType) {
                    SearchCommonViewModel.SearchResultType.TRANSACTIONS -> {
                        val propertyMainType = showLookupResultEvent.propertyMainType
                            ?: globalPropertyMainType
                            ?: throw IllegalArgumentException("Missing `propertyMainType` in transaction `ShowLookupResultEvent`")
                        TransactionsActivityEntry.launchByAmenity(
                            searchActivity,
                            showLookupResultEvent.ids,
                            showLookupResultEvent.labels,
                            propertyPurpose,
                            ownershipType,
                            propertyMainType
                        )
                    }
                    else -> {
                        SearchResultActivityEntry.launchByAmenity(
                            searchActivity,
                            showLookupResultEvent.searchResultType.activityClass,
                            showLookupResultEvent.ids,
                            showLookupResultEvent.labels,
                            propertyPurpose,
                            ownershipType
                        )
                    }
                }
            }
        }
    }
}