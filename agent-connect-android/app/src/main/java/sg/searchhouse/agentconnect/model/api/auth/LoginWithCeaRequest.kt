package sg.searchhouse.agentconnect.model.api.auth

data class LoginWithCeaRequest(
    val ceaRegNo: String,
    val mobile: String
)