package sg.searchhouse.agentconnect.model.api.listing.search

import com.google.gson.annotations.SerializedName

data class ExportListingsResponse(
    @SerializedName("result")
    val result: String = "",
    @SerializedName("url")
    val url: String = ""
)