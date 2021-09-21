package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.common.NameValuePO

data class GetAllPlanningDecisionTypesResponse(
    @SerializedName("details")
    val details: List<NameValuePO>
)