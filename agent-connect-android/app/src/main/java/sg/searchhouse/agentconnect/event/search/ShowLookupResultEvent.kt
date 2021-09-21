package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

data class ShowLookupResultEvent(
    val lookupType: LookupType,
    val searchResultType: SearchCommonViewModel.SearchResultType,
    val ids: String,
    val labels: String,
    val query: String? = null,
    val propertyMainType: ListingEnum.PropertyMainType? = null
) {
    enum class LookupType { AMENITY_ID, HDB_TOWN_ID, DISTRICT_ID }
}