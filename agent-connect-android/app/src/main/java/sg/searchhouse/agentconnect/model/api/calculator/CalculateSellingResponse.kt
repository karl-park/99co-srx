package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalculateSellingResponse(
    @SerializedName("result")
    val result: SellingPO
) : Serializable