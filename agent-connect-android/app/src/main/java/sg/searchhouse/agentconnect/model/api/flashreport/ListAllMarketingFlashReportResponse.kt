package sg.searchhouse.agentconnect.model.api.flashreport

import com.google.gson.annotations.SerializedName

data class ListAllMarketingFlashReportResponse(
    @SerializedName("marketFlashReports")
    val marketFlashReports: MarketFlashReports
) {
    data class MarketFlashReports(
        @SerializedName("results")
        val results: List<MarketingFlashReportPO>,
        @SerializedName("total")
        val total: Int
    )
}