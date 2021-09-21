package sg.searchhouse.agentconnect.event.chat

data class ChatSelectAllEvent(
    val checkAll: Boolean,
    val type: ChatConversationType
) {
    enum class ChatConversationType {
        ALL,
        PUBLIC,
        SRX,
        AGENT
    }
}