package sg.searchhouse.agentconnect.model.api.auth

data class LoginWithEmailRequest(
    val email: String,
    val password: String
)