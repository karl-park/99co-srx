package sg.searchhouse.agentconnect.model.api.agentclient

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetAgentClientsInviteMessageResponse(
    @SerializedName("inviteUrl")
    val inviteUrl: String,
    @SerializedName("inviteMsg")
    val inviteMsg: String,
    @SerializedName("result")
    val result: String
): Serializable