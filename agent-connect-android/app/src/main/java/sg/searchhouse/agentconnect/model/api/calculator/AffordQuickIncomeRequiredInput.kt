package sg.searchhouse.agentconnect.model.api.calculator

class AffordQuickIncomeRequiredInput(
    var propPurchasePrice: Int? = null,
    var monthlyDebt: Int? = null,
    var downpayment: Int? = null
) {
    fun isValid(): Boolean {
        return propPurchasePrice != null && monthlyDebt != null && downpayment != null
    }
}