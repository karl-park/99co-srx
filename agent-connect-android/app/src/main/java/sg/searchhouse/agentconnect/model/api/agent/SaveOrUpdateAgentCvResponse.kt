package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class SaveOrUpdateAgentCvResponse(
    @SerializedName("result")
    val result: AgentCvPO
)