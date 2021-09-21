package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CeaFormTermPO(
    @SerializedName("id")
    var id: Int,
    @SerializedName("term")
    var term: String
) : Serializable