package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class PortalClientGetListingsAutoRequest(
    @SerializedName("portalType")
    val portalType: Int,
    @SerializedName("rawAgentData")
    val rawAgentData: JsonObject,
    @SerializedName("rawListings")
    val rawListings: List<JsonObject>
)