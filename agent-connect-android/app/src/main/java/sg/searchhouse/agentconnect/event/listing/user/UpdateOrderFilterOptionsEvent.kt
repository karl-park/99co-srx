package sg.searchhouse.agentconnect.event.listing.user

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum

class UpdateOrderFilterOptionsEvent(
    val orderCriteria: ListingManagementEnum.OrderCriteria? = null,
    val propertyMainType: ListingEnum.PropertyMainType? = null,
    val propertySubTypes: ListingEnum.PropertySubType? = null,
    val propertyAge: ListingEnum.PropertyAge? = null,
    val bedrooms: ListingEnum.BedroomCount? = null,
    val tenure: ListingEnum.Tenure? = null,
    val searchText: String? = null
)