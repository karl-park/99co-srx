package sg.searchhouse.agentconnect.model.api.flashreport

import com.google.gson.annotations.SerializedName

data class ListActiveMarketingFlashReportResponse(
    @SerializedName("marketFlashReports")
    val marketFlashReports: List<MarketingFlashReportPO>,
    @SerializedName("hasHistoricalAccess")
    val hasHistoricalAccess: Boolean
)