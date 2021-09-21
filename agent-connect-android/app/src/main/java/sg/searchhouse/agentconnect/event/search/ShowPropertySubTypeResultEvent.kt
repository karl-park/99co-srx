package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

data class ShowPropertySubTypeResultEvent(
    val searchResultType: SearchCommonViewModel.SearchResultType,
    val propertySubType: ListingEnum.PropertySubType,
    val query: String? = null
)