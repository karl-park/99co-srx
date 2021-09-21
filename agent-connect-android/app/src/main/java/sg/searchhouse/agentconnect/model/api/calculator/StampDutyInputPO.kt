package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StampDutyInputPO(
    @SerializedName("pptyPrice")
    val pptyPrice: Long,
    @SerializedName("pptyType")
    val pptyType: String,
    @SerializedName("appType")
    val appType: String,
    @SerializedName("applicantDetail")
    val applicantDetail: List<ApplicationDetail>
) : Serializable {
    data class ApplicationDetail(
        @SerializedName("buyerProfile")
        val buyerProfile: String,
        @SerializedName("noOfProperty")
        val noOfProperty: Int
    ) : Serializable
}