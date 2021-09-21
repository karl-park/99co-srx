package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

data class ShowQueryPropertySubTypeResultEvent(
    val query: String,
    val propertySubType: ListingEnum.PropertySubType
)