package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1140719619/Communities+Data+Structure
 */
data class CommunityPostReactionPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("iconUrl")
    val iconUrl: String,
    @SerializedName("reacted")
    val reacted: Boolean
) : Serializable