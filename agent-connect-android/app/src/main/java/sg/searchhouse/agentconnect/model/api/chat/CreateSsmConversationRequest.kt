package sg.searchhouse.agentconnect.model.api.chat

import sg.searchhouse.agentconnect.model.api.userprofile.UserPO

data class CreateSsmConversationRequest constructor(
    var type: String = "",
    val members: ArrayList<UserPO>,
    val title: String = ""
)