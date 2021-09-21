package sg.searchhouse.agentconnect.model.api.auth

data class CreateAccountRequest(
    val otp: String = "",
    val email: String = "",
    val password: String = "",
    val ceaRegNo: String = "",
    val mobileNum: String = ""
)