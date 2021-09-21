package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

data class ShowQueryResultEvent(
    val searchResultType: SearchCommonViewModel.SearchResultType,
    val query: String,
    val propertyMainType: ListingEnum.PropertyMainType?
)