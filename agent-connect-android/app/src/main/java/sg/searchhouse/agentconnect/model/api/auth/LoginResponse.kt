package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName

/**
 * Login with cea and mobile , result status
 * 1 -> Login Success
 * 2 -> Require OTP
 * 3 -> Require Reset
 *
 * Login with email and password, result status
 * 1 -> Login Success
 * 2 -> Require OTP
 * 3 -> Require Reset
 * **/
data class LoginResponse(
    @SerializedName("result")
    val result: Int = 0,
    @SerializedName("data")
    val loginResponseData: LoginResponseData,
    @SerializedName("userId")
    val userId: String = ""
)

