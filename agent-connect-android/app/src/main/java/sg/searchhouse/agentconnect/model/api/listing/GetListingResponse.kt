package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class GetListingResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("fullListing")
    val fullListing: FullListingPO
)