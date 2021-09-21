package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalculateAffordAdvancedResponse(
    @SerializedName("result")
    val result: AffordAdvancedPO
) : Serializable