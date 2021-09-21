package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.NumberUtil

data class PackageDetailPO(
    @SerializedName("packageId")
    val packageId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("installmentApplicable")
    val installmentApplicable: Boolean,
    @SerializedName("description")
    val description: String
) {
    fun getFormattedAmount(): String {
        val isAmountInteger = (amount * 100).toInt() % 100 == 0
        return if (isAmountInteger) {
            NumberUtil.getFormattedCurrency(amount)
        } else {
            NumberUtil.getFormattedCurrency(amount, 2)
        }
    }
}