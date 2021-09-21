package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName

data class GetProjectInformationResponse(
    @SerializedName("details")
    val details: SRXProjectDetailsPO
)