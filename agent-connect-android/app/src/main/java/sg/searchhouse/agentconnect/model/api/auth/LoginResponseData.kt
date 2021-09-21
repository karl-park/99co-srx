package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * LoginResponseData class
 * to receive loginResponseData object from login for status 2
 * to pass loginResponseData object for verifyOTP & resendOTP
 * */
data class LoginResponseData(
    @SerializedName("ceaRegNo")
    var ceaRegNo: String = "",
    @SerializedName("mobileNum")
    var mobileNum: String = "",
    @SerializedName("otp")
    var otp: String = "",
    @SerializedName("purpose")
    var purpose: String = "",
    @SerializedName("userId")
    var userId: String = ""
) : Serializable