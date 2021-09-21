package sg.searchhouse.agentconnect.model.api.listing.search

import com.google.gson.annotations.SerializedName

data class ExportListingsRequest(
    @SerializedName("srxstp")
    val srxstp: List<Int>,
    @SerializedName("withAgentContact")
    val withAgentContact: Boolean,
    @SerializedName("withPhoto")
    val withPhoto: Boolean,
    @SerializedName("tableLP")
    val tableLP: List<Int>
)