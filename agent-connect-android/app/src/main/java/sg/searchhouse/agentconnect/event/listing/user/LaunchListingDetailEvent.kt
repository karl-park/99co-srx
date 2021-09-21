package sg.searchhouse.agentconnect.event.listing.user

import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingGroup

class LaunchListingDetailEvent(
    val listingId: String,
    val listingType: String,
    val listingGroup: ListingGroup
)