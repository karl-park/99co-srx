package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class GeneratePaymentLinkResponse(
    @SerializedName("result")
    val result: String
) {
    fun getResultWithoutBanner(): String {
        return "$result&showAppBanner=false"
    }
}