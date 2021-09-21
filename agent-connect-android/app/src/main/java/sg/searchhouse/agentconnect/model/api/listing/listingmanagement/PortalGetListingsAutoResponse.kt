package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class PortalGetListingsAutoResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("listings")
    val listings: List<PortalListingPO>,
    @SerializedName("portalAccount")
    val portalAccount: PortalAccountPO
)