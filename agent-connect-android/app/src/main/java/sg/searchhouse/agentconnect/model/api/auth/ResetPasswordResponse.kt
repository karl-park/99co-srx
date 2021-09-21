package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(
    @SerializedName("data")
    val data: ResetPasswordData = ResetPasswordData()
) {
    data class ResetPasswordData(
        @SerializedName("email")
        val email: String = "",
        @SerializedName("message")
        val message: String = ""
    )
}