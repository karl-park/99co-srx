package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName

data class CeaSubmitFormResponse(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("result")
    val result: String
)