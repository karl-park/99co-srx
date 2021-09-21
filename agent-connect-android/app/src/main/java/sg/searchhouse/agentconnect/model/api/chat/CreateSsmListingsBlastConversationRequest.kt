package sg.searchhouse.agentconnect.model.api.chat

data class CreateSsmListingsBlastConversationRequest(
    val title: String?,
    val message: String?,
    val srxstp: List<String>?,
    val tableLP: List<String>?
)