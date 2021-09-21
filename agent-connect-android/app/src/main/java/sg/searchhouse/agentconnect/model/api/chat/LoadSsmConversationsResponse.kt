package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName

data class LoadSsmConversationsResponse constructor(
    @SerializedName("result")
    val result: String,
    @SerializedName("convos")
    val convos: ArrayList<SsmConversationPO>,
    @SerializedName("unreadCount")
    val unreadCount: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("unreadMessageCount")
    val unreadMessageCount: Int
)