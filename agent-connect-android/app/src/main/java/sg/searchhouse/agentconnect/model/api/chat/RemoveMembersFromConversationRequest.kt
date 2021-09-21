package sg.searchhouse.agentconnect.model.api.chat

import sg.searchhouse.agentconnect.model.api.userprofile.UserPO

data class RemoveMembersFromConversationRequest(
    val conversationId: String,
    val members: ArrayList<UserPO>
)