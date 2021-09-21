package sg.searchhouse.agentconnect.model.api.listing.user

import com.google.gson.annotations.SerializedName

data class FindMyListingsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("listingManagementGroups")
    val listingManagementGroups: List<ListingManagementGroupPO>,
    @SerializedName("total")
    val total: Int
)