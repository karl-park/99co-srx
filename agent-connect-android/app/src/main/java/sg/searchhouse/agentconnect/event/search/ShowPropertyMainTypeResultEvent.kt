package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

data class ShowPropertyMainTypeResultEvent(
    val searchResultType: SearchCommonViewModel.SearchResultType,
    val propertyMainType: ListingEnum.PropertyMainType,
    val query: String? = null
)