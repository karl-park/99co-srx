package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

data class LatestRentalTransactionsResponse(
    @SerializedName("RentalTransactionData")
    val rentalTransactionData: RentalTransactionData
) {
    data class TransactionInfoItem(
        @SerializedName("value")
        val value: String,
        @SerializedName("key")
        val key: String
    )

    data class RentalOfficialTransactionPO(
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


    data class RentalTransactionData(
        @SerializedName("rentalOfficialTransactionPO")
        val rentalOfficialTransactionPO: RentalOfficialTransactionPO,
        @SerializedName("rentalTransactionPO")
        val rentalTransactionPO: RentalTransactionPO
    )

    data class RentalTransactionPO(
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
}