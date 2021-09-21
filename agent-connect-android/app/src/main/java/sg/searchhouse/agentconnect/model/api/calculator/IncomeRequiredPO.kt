package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IncomeRequiredPO(
    @SerializedName("propPurchasePrice")
    val propPurchasePrice: Long,
    @SerializedName("monthlyDebt")
    val monthlyDebt: Long,
    @SerializedName("downpayment")
    val downpayment: Long
): Serializable