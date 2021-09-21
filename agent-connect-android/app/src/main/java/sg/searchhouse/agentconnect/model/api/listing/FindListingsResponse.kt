package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class FindListingsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("listings")
    val listings: SearchListingResultPO,
    @SerializedName("seed")
    val seed: String
)