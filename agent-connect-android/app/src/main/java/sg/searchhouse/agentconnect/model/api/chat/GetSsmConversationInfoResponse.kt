package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO

data class GetSsmConversationInfoResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("settings")
    val settings: SsmConversationSettingPO,
    @SerializedName("members")
    val members: ArrayList<UserPO>
)