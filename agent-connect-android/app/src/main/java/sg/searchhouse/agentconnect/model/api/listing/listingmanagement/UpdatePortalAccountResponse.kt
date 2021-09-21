package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class UpdatePortalAccountResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("portalAccount")
    val portalAccount: PortalAccountPO
)