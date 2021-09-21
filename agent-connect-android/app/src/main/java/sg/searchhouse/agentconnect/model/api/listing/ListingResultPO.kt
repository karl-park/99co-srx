package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class ListingResultPO(
    @SerializedName("listingPOs")
    val listingPOs: List<ListingPO>,
    @SerializedName("total")
    val total: Int
)
