package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName

data class StampDutyPO(
    @SerializedName("buyerStampDuty")
    val buyerStampDuty: Long,
    @SerializedName("addBuyerStampDuty")
    val addBuyerStampDuty: Long,
    @SerializedName("addBuyerStampDutyPct")
    val addBuyerStampDutyPct: Double,
    @SerializedName("totalStampDuty")
    val totalStampDuty: Long,
    @SerializedName("shortenUrl")
    val shortenUrl: String
)