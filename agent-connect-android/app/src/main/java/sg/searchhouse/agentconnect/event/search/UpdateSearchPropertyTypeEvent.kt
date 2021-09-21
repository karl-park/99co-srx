package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity.*

data class UpdateSearchPropertyTypeEvent(
    val propertyPurpose: ListingEnum.PropertyPurpose,
    val expandMode: ExpandMode?
)