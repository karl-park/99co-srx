package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName

data class SendSsmMessageResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("newMessages")
    val newMessages: ArrayList<SsmMessagePO>
)