package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable

data class AffordAdvancedPO(
    @SerializedName("mortgageService")
    val mortgageService: Long,
    @SerializedName("totalDebtService")
    val totalDebtService: Long,
    @SerializedName("maxLoan")
    val maxLoan: Long,
    @SerializedName("monthlyLoan")
    val monthlyLoan: Long,
    @SerializedName("maxPurchasePrice")
    val maxPurchasePrice: Long,
    @SerializedName("downpayment")
    val downpayment: Long,
    @SerializedName("totalDownpayment")
    val totalDownpayment: Long,
    @SerializedName("ltv")
    val ltv: Float,
    @SerializedName("bsd")
    val bsd: Long,
    @SerializedName("absd")
    val absd: Long,
    @SerializedName("totalMonthlyIncome")
    val totalMonthlyIncome: Long,
    @SerializedName("totalMonthlyCommitment")
    val totalMonthlyCommitment: Long,
    @SerializedName("shortenUrl")
    val shortenUrl: String
) : Serializable {

    fun getFormattedMortgageService(): String {
        return NumberUtil.getFormattedCurrency(mortgageService.toInt())
    }

    fun getFormattedTotalDebtService(): String {
        return NumberUtil.getFormattedCurrency(totalDebtService.toInt())
    }

    fun getFormattedMaxLoan(): String {
        return NumberUtil.getFormattedCurrency(maxLoan.toInt())
    }

    fun getFormattedMonthlyLoan(): String {
        return NumberUtil.getFormattedCurrency(monthlyLoan.toInt())
    }

    fun getFormattedMaxPurchasePrice(): String {
        return NumberUtil.getFormattedCurrency(maxPurchasePrice.toInt())
    }

    fun getFormattedDownpayment(): String {
        return NumberUtil.getFormattedCurrency(downpayment.toInt())
    }

    fun getFormattedTotalDownpayment(): String {
        return NumberUtil.getFormattedCurrency(totalDownpayment.toInt())
    }

    fun getFormattedBsd(): String {
        return NumberUtil.getFormattedCurrency(bsd.toInt())
    }

    fun getFormattedAbsd(): String {
        return NumberUtil.getFormattedCurrency(absd.toInt())
    }

    fun getFormattedTotalMonthlyIncome(): String {
        return NumberUtil.getFormattedCurrency(totalMonthlyIncome.toInt())
    }

    fun getFormattedTotalMonthlyCommitment(): String {
        return NumberUtil.getFormattedCurrency(totalMonthlyCommitment.toInt())
    }

    fun getFormattedLtv(): String {
        return "${NumberUtil.roundFloat(ltv, 0).toInt()}%"
    }
}