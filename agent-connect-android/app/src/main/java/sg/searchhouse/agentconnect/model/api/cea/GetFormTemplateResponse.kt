package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName

data class GetFormTemplateResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("template")
    val template: CeaFormTemplatePO
)