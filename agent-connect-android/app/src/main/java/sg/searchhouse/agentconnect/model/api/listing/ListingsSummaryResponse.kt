package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class ListingsSummaryResponse(
    @SerializedName("rentListingsBreakdown")
    val rentListingsBreakdown: List<Breakdown> = listOf(),
    @SerializedName("result")
    val result: String = "",
    @SerializedName("saleListingsBreakdown")
    val saleListingsBreakdown: List<Breakdown> = listOf()
) {
    data class Breakdown(
        @SerializedName("cdResearchSubtype")
        val cdResearchSubtype: Int = 0,
        @SerializedName("description")
        val description: String = "",
        @SerializedName("total")
        val total: Int = 0
    ) {
        fun getTotalString(): String {
            return total.toString()
        }
    }
}