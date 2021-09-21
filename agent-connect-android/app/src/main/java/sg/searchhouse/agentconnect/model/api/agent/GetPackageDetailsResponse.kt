package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class GetPackageDetailsResponse(
    @SerializedName("package")
    val packages: List<PackageDetailPO>,
    @SerializedName("installmentOptions")
    val installmentOptions: List<InstallmentPaymentOptionPO> = emptyList(),
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("paymentType")
    val paymentType: Int
) {
    fun hasDescription(): Boolean {
        return description?.isNotEmpty() == true
    }
}