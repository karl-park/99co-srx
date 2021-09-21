package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class GetAgentDetailsResponse(
    @SerializedName("data")
    val data: AgentPO
)