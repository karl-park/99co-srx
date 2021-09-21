package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalculateMaxPurchasePriceResponse(
    @SerializedName("result")
    val result: AffordQuickPO
) : Serializable