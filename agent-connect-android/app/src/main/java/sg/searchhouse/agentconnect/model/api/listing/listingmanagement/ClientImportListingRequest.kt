package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

data class ClientImportListingRequest(
    val portalType: Int,
    val rawListing: Any,
    val accountId: Int?
)