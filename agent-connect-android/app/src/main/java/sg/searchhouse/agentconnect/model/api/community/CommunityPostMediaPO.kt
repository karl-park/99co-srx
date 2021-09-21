package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1140719619/Communities+Data+Structure
 */
data class CommunityPostMediaPO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("type")
    val type: Int,
    @SerializedName("mediaType")
    val mediaType: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlThumbnail")
    val urlThumbnail: String,
    @SerializedName("dateCreate")
    val dateCreate: String
) : Serializable