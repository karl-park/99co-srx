package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName

data class SellingPO(
    @SerializedName("sellerStampDuty")
    val sellerStampDuty: Long,
    @SerializedName("capitalGain")
    val capitalGain: Double,
    @SerializedName("shortenUrl")
    val shortenUrl: String
)