package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName

data class SsmConversationSettingPO constructor(
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("adminInd")
    val adminInd: Boolean,
    @SerializedName("publicInd")
    val publicInd: Boolean,
    @SerializedName("adminCommunicationInd")
    val adminCommunicationInd: Boolean,
    @SerializedName("notificationsDisabledInd")
    val notificationsDisabledInd: Boolean
)