package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListingRemoteOptionPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("videoCallInd")
    var videoCallInd: Boolean
) : Serializable