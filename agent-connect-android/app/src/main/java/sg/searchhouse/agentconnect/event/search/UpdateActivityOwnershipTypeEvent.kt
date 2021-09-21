package sg.searchhouse.agentconnect.event.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

data class UpdateActivityOwnershipTypeEvent(val ownershipType: ListingEnum.OwnershipType)