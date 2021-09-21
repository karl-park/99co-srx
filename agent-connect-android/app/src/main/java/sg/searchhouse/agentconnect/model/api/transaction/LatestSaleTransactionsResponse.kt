package sg.searchhouse.agentconnect.model.api.transaction


import com.google.gson.annotations.SerializedName

data class LatestSaleTransactionsResponse(
    @SerializedName("SaleTransactionData")
    val saleTransactionData: SaleTransactionData
) {
    data class SaleTransactionData(
        @SerializedName("recentTransactionPO")
        val recentTransactionPO: RecentTransactionPO
    )

    data class RecentTransactionPO(
        @SerializedName("highestPrice")
        val highestPrice: String = "",
        @SerializedName("lowestPrice")
        val lowestPrice: String = "",
        @SerializedName("highestPsf")
        val highestPsf: String = "",
        @SerializedName("transactionList")
        val transactionList: List<TransactionListItem>?,
        @SerializedName("lowestPsf")
        val lowestPsf: String = "",
        @SerializedName("transactionInfo")
        val transactionInfo: List<TransactionInfoItem>?
    )

    data class TransactionInfoItem(
        @SerializedName("value")
        val value: String,
        @SerializedName("key")
        val key: String
    )
}

