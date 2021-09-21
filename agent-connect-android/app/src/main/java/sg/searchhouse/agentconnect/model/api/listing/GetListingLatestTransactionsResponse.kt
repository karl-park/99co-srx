package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.xvalue.HomeReportTransaction

// Response from temp endpoint, only take relevant attributes
data class GetListingLatestTransactionsResponse(
    @SerializedName("transactions")
    val transactions: Transactions?
) {
    data class Transactions(
        @SerializedName("recentTransactionPO")
        val recentTransactionPO: RecentTransactionPO?
    ) {
        data class RecentTransactionPO(
            @SerializedName("highestPrice")
            val highestPrice: String,
            @SerializedName("highestPsf")
            val highestPsf: String,
            @SerializedName("lowestPrice")
            val lowestPrice: String,
            @SerializedName("lowestPsf")
            val lowestPsf: String,
            @SerializedName("transactionInfo")
            val transactionInfo: List<TransactionInfo>,
            @SerializedName("transactionList")
            val transactionList: List<HomeReportTransaction>
        ) {
            data class TransactionInfo(
                @SerializedName("key")
                val key: String,
                @SerializedName("value")
                val value: String
            )
        }
    }
}