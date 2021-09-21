package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1339064349/Community+V1+API
 */
data class GetCommunityMemberCountResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("selected")
    val selected: Int,
    @SerializedName("total")
    val total: Int
)