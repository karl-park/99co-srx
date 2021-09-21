package sg.searchhouse.agentconnect.model.api.cobrokesms

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendSmsResponse(
    @SerializedName("result")
    val result: String = "",
    @SerializedName("smsSentCount")
    val smsSentCount: Int = 0
) : Serializable
