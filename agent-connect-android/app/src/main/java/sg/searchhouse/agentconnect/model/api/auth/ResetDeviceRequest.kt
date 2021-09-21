package sg.searchhouse.agentconnect.model.api.auth

data class ResetDeviceRequest(
    val email: String = "",
    val userId: String = ""
)