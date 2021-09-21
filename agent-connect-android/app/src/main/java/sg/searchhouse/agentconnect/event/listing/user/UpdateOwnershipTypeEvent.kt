package sg.searchhouse.agentconnect.event.listing.user

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

class UpdateOwnershipTypeEvent(
    val ownershipType: ListingEnum.OwnershipType,
    val isDraftCeaOwnershipType: Boolean = false
)