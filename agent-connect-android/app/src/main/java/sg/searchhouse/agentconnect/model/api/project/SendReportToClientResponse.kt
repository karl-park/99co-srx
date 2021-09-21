package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName

data class SendReportToClientResponse(
    @SerializedName("reportAvailableSuccess")
    val reportAvailableSuccess: List<Int>,
    @SerializedName("reportAvailableFailure")
    val reportAvailableFailure: List<Int>,
    @SerializedName("mobileNumbersAttempted")
    val mobileNumbersAttempted: List<String>
)