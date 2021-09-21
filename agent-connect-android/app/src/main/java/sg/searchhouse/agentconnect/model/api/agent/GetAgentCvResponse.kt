package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class GetAgentCvResponse(
    @SerializedName("data")
    val data: AgentPO
)