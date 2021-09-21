package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName

data class FindSsmMessagesResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("messages")
    val messages: ArrayList<SsmMessagePO>,
    @SerializedName("total")
    val total: Int = 0
)