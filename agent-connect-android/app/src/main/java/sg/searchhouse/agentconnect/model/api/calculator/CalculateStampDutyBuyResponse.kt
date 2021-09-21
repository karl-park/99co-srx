package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalculateStampDutyBuyResponse(
    @SerializedName("result")
    val result: StampDutyPO
) : Serializable