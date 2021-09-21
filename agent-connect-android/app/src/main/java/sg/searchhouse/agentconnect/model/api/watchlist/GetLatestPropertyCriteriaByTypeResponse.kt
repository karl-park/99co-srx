package sg.searchhouse.agentconnect.model.api.watchlist

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetLatestPropertyCriteriaByTypeResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("watchlists")
    val watchlists: List<WatchlistPropertyCriteriaPO>,
    @SerializedName("total")
    val total: Int
) : Serializable