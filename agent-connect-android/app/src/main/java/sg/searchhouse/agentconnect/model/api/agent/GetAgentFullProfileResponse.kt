package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class GetAgentFullProfileResponse(
    @SerializedName("data")
    val data: AgentFullProfile
) {
    data class AgentFullProfile(
        @SerializedName("credits")
        val credits: ArrayList<UserCreditPO>,
        @SerializedName("profile")
        val profile: AgentPO?,
        @SerializedName("subscription")
        val subscription: UserSubscriptionPO?,
        @SerializedName("subscriptionStatus")
        val subscriptionStatus: String,
        @SerializedName("clientCount")
        val clientCount: Int
    )
}