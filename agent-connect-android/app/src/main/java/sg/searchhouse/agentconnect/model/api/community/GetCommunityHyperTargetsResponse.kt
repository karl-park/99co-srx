package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/685375547/Listing+Management+V1+API
 */
data class GetCommunityHyperTargetsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("targets")
    val targets: Targets
) {
    data class Targets(
        @SerializedName("results")
        val results: List<CommunityHyperTargetTemplatePO>,
        @SerializedName("total")
        val total: Int
    )
}