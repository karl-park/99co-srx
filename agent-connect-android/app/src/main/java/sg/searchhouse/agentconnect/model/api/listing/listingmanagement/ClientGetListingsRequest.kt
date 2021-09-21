package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.JsonObject

data class ClientGetListingsRequest(
    val portalType: Int,
    val rawListings: List<JsonObject>
)