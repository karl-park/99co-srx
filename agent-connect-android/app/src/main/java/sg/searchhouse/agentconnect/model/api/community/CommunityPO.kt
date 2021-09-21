package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1140719619/Communities+Data+Structure
 */
data class CommunityPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("parentCommunity")
    val parentCommunity: CommunityPO?,
    @SerializedName("membersTotal")
    val membersTotal: Int
) : Serializable