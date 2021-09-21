package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class ClientLoginResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("portalAccount")
    val portalAccount: PortalAccountPO
)