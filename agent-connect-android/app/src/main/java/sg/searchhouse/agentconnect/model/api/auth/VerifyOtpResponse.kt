package sg.searchhouse.agentconnect.model.api.auth

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.StringUtil
import java.io.Serializable

data class VerifyOtpResponse(
    @SerializedName("data")
    val data: LoginResponseData,
    @SerializedName("success")
    val success: VerifyOtpSuccess
) : Serializable {

    class VerifyOtpSuccess(
        @SerializedName("existingUser")
        val existingUser: Boolean = false,
        @SerializedName("otpVerification")
        val otpVerification: Boolean = false,
        @SerializedName("agentPhotoUrl")
        val agentPhotoUrl: String = "",
        @SerializedName("agencyPhotoUrl")
        val agencyPhotoUrl: String = "",
        @SerializedName("ceaRegNo")
        val ceaRegNo: String = "",
        @SerializedName("agentName")
        val agentName: String = "",
        @SerializedName("otp")
        val otp: String = "",
        @SerializedName("deviceResetRequired")
        val deviceResetRequired: Boolean = false,
        @SerializedName("userId")
        val userId: String = ""
    ) : Serializable {

        fun getAgentName(context: Context): String {
            return if (!TextUtils.isEmpty(agentName)) {
                "${context.getString(R.string.title_welcome)} $agentName"
            } else {
                context.getString(R.string.title_welcome)
            }
        }

        fun getCeaNo(): String {
            return if (!TextUtils.isEmpty(ceaRegNo)) {
                "CEA No: ${StringUtil.toUpperCase(ceaRegNo)}"
            } else {
                ""
            }
        }
    }
}