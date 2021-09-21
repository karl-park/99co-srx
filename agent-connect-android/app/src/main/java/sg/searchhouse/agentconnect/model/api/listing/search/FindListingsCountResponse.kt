package sg.searchhouse.agentconnect.model.api.listing.search

import com.google.gson.annotations.SerializedName

data class FindListingsCountResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("total")
    val total: Int
)