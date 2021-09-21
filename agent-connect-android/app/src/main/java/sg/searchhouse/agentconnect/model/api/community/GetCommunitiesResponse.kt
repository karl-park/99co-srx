package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1339064349/Community+V1+API
 */
data class GetCommunitiesResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("communities")
    val communities: List<CommunityTopDownPO>
)