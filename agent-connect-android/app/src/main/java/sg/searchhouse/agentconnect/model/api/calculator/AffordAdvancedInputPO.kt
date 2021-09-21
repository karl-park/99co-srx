package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AffordAdvancedInputPO(
    @SerializedName("pptyType")
    val pptyType: String,
    @SerializedName("appType")
    val appType: String,
    @SerializedName("share")
    val share: Boolean = false,
    @SerializedName("applicantDetail")
    val applicantDetail: List<ApplicationDetail>
) : Serializable {
    data class ApplicationDetail(
        @SerializedName("age")
        val age: Int,
        @SerializedName("buyerProfile")
        val buyerProfile: String,
        @SerializedName("monthlyFixedIncome")
        val monthlyFixedIncome: Long,
        @SerializedName("monthlyVariableIncome")
        val monthlyVariableIncome: Long,
        @SerializedName("monthlyDebt")
        val monthlyDebt: Long,
        @SerializedName("monthlyPropertyLoan")
        val monthlyPropertyLoan: Long,
        @SerializedName("monthlyAddCommitment")
        val monthlyAddCommitment: Long,
        @SerializedName("noOfProperty")
        val noOfProperty: Int,
        @SerializedName("noOfPropertyLoan")
        val noOfPropertyLoan: Int,
        @SerializedName("cash")
        val cash: Long,
        @SerializedName("cpfOA")
        val cpfOA: Long
    ) : Serializable
}