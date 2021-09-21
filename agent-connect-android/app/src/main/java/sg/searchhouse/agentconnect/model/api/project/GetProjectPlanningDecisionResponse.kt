package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName

data class GetProjectPlanningDecisionResponse(
    @SerializedName("details")
    val details: List<PlanningDecisionPO>
)