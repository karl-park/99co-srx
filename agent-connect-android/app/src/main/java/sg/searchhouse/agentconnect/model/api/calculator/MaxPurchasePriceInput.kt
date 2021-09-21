package sg.searchhouse.agentconnect.model.api.calculator

class MaxPurchasePriceInput(
    var monthlyIncome: Int? = null,
    var monthlyDebt: Int? = null,
    var downpayment: Int? = null
) {
    fun isValid(): Boolean {
        return monthlyIncome != null && monthlyDebt != null && downpayment != null
    }
}