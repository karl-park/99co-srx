package sg.searchhouse.agentconnect.model.api.listing.user

import com.google.gson.annotations.SerializedName

data class ListingsGroupSummaryResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("summary")
    val summary: List<ListingGroupSummaryPO>
)