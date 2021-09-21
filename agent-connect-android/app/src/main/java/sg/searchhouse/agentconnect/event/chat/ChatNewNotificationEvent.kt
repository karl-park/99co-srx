package sg.searchhouse.agentconnect.event.chat

data class ChatNewNotificationEvent(
    val messageId: String,
    val conversationId: String,
    val enquiryId: String
)