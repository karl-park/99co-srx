package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

data class LatestTransactionsRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("block")
    val block: String? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("streetKey")
    val streetKey: String? = null,
    @SerializedName("priceandPSFRangeRequest")
    val priceandPSFRangeRequest: Boolean = true
)