package sg.searchhouse.agentconnect.view.helper.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.search.*
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

// Send RX from SearchCommonFragment to SearchActivity for launch result e.g. listings, transactions
object NotifyLaunchSearchResultHelper {
    fun notifyLaunchByQuery(
        searchResultType: SearchCommonViewModel.SearchResultType,
        query: String,
        propertyMainType: ListingEnum.PropertyMainType?
    ) {
        RxBus.publish(
            ShowQueryResultEvent(
                searchResultType,
                query,
                propertyMainType
            )
        )
    }

    fun notifyLaunchByQueryPropertySubType(
        query: String,
        propertySubType: ListingEnum.PropertySubType
    ) {
        RxBus.publish(ShowQueryPropertySubTypeResultEvent(query, propertySubType))
    }

    fun notifyLaunchByPropertyMainType(
        searchResultType: SearchCommonViewModel.SearchResultType,
        propertyMainType: ListingEnum.PropertyMainType,
        query: String
    ) {
        RxBus.publish(ShowPropertyMainTypeResultEvent(searchResultType, propertyMainType, query))
    }

    fun notifyLaunchByPropertySubType(
        searchResultType: SearchCommonViewModel.SearchResultType,
        propertySubType: ListingEnum.PropertySubType,
        query: String
    ) {
        RxBus.publish(ShowPropertySubTypeResultEvent(searchResultType, propertySubType, query))
    }

    fun notifyLaunchByDistrictId(
        searchResultType: SearchCommonViewModel.SearchResultType,
        labels: String,
        ids: String
    ) {
        RxBus.publish(
            ShowLookupResultEvent(
                ShowLookupResultEvent.LookupType.DISTRICT_ID,
                searchResultType,
                ids,
                labels
            )
        )
    }

    fun notifyLaunchByHdbTownId(
        searchResultType: SearchCommonViewModel.SearchResultType,
        labels: String,
        ids: String
    ) {
        RxBus.publish(
            ShowLookupResultEvent(
                ShowLookupResultEvent.LookupType.HDB_TOWN_ID,
                searchResultType,
                ids,
                labels
            )
        )
    }

    fun notifyLaunchByAmenityId(
        searchResultType: SearchCommonViewModel.SearchResultType,
        labels: String,
        ids: String,
        propertyMainType: ListingEnum.PropertyMainType?
    ) {
        RxBus.publish(
            ShowLookupResultEvent(
                ShowLookupResultEvent.LookupType.AMENITY_ID,
                searchResultType,
                ids,
                labels,
                propertyMainType = propertyMainType
            )
        )
    }
}