package sg.searchhouse.agentconnect.model.api.watchlist

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.SearchListingResultPO
import java.io.Serializable

data class GetPropertyCriteriaLatestListingsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("listings")
    val listings: SearchListingResultPO
) : Serializable