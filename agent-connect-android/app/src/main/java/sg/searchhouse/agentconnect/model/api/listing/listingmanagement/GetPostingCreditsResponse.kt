package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class GetPostingCreditsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("postingCost")
    val postingCost: Int,
    @SerializedName("balance")
    val balance: Int
)