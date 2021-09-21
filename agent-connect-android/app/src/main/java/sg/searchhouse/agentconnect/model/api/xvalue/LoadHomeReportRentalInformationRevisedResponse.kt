package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName

data class LoadHomeReportRentalInformationRevisedResponse(
    @SerializedName("RentalTransactionData")
    val rentalTransactionData: RentalTransactionData
) {
    fun getTransactionList(): List<HomeReportTransaction> {
        return rentalTransactionData.rentalTransactionPO.transactionList
    }

    data class RentalTransactionData(
        @SerializedName("commonRentalInfo")
        val commonRentalInfo: Any?,
        @SerializedName("comparableTransactions")
        val comparableTransactions: List<Any>,
        @SerializedName("rentalListingPO")
        val rentalListingPO: RentalListingPO,
        @SerializedName("rentalOfficialTransactionPO")
        val rentalOfficialTransactionPO: RentalOfficialTransactionPO,
        @SerializedName("rentalTransactionPO")
        val rentalTransactionPO: RentalTransactionPO,
        @SerializedName("rentalTransactions")
        val rentalTransactions: List<Any>,
        @SerializedName("rentalYield")
        val rentalYield: RentalYield,
        @SerializedName("rentals")
        val rentals: List<Any>
    ) {
        data class RentalListingPO(
            @SerializedName("listingInfo")
            val listingInfo: ListingInfo,
            @SerializedName("listings")
            val listings: List<Any>
        ) {
            class ListingInfo
        }

        data class RentalOfficialTransactionPO(
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

        data class RentalTransactionPO(
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

        // TODO: Populate
        class RentalYield
    }
}