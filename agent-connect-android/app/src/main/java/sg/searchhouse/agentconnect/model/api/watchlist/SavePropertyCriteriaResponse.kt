package sg.searchhouse.agentconnect.model.api.watchlist

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SavePropertyCriteriaResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("watchlist")
    val watchlist: WatchlistPropertyCriteriaPO
) : Serializable