package sg.searchhouse.agentconnect.model.api.watchlist

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeletePropertyCriteriaResponse(
    @SerializedName("result")
    val result: String
) : Serializable