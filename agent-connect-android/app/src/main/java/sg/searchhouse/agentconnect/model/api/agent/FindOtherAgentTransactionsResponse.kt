package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

data class FindOtherAgentTransactionsResponse(
    @SerializedName("transactions")
    val transactions: Transactions
) {
    data class Transactions(
        @SerializedName("total")
        val total: Int,
        @SerializedName("listingPOs")
        val listingPOs: List<ListingPO>
    )
}