package sg.searchhouse.agentconnect.model.api.agentdirectory

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.agent.AgentPO

data class FindAgentsResponse(
    @SerializedName("result")
    val result: String = "",
    @SerializedName("agents")
    val agents: List<AgentPO> = emptyList(),
    @SerializedName("total")
    val total: Int = 0
)