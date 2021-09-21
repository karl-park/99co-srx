package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName

data class GetPropertyDataResponse(
    @SerializedName("tenure")
    val tenure: Int,
    @SerializedName("result")
    val result: String,
    @SerializedName("sizeType")
    val sizeType: String,
    @SerializedName("builtYear")
    val builtYear: Int,
    @SerializedName("size")
    val size: Int?
)