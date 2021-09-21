package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName

data class CalculateResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("rentId")
    val rentId: Int?,
    @SerializedName("rentResult")
    val rentResult: Int?,
    @SerializedName("result")
    val result: Int?,
    @SerializedName("xvalue")
    val xvalue: XValue?
)