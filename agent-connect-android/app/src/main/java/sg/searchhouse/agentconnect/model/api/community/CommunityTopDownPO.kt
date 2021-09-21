package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1140719619/Communities+Data+Structure
 */
data class CommunityTopDownPO(
    @SerializedName("community")
    val community: CommunityPO,
    @SerializedName("ptUserId")
    val ptUserId: Int,
    @SerializedName("children")
    val children: List<CommunityTopDownPO>
) : Serializable {
    fun getCommunityId(): Int {
        return community.id
    }
}