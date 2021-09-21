package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName

data class CreateSsmConversationResponse constructor(
    @SerializedName("result")
    val result: String = "",
    @SerializedName("convoId")
    val convoId: Int = 0,
    @SerializedName("unreadCount")
    val unreadCount: Int = 0,
    @SerializedName("convo")
    val convo: SsmConversationPO
)