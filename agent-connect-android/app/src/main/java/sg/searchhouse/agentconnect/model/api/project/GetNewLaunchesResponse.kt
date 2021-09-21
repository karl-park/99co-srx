package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetNewLaunchesResponse(
    @SerializedName("result")
    val result: List<NewLaunchProject>,
    @SerializedName("count")
    val count: Int
) : Serializable {
    data class NewLaunchProject(
        @SerializedName("crsId")
        val crsId: Int,
        @SerializedName("launchDateTimestamp")
        val launchDateTimestamp: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("readablelaunchDateTimestamp")
        val readablelaunchDateTimestamp: String,
        @SerializedName("reportUrl")
        val reportUrl: String
    ) : Serializable
}