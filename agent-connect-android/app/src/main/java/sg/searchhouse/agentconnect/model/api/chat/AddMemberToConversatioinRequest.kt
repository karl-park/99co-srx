package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO

data class AddMemberToConversatioinRequest(
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("members")
    val members: ArrayList<UserPO>
)