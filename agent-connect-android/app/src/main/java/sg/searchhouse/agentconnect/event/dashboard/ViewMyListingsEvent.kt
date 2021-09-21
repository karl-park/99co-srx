package sg.searchhouse.agentconnect.event.dashboard

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

class ViewMyListingsEvent(val ownershipType: ListingEnum.OwnershipType, val propertyMainType: ListingEnum.PropertyMainType? = null)