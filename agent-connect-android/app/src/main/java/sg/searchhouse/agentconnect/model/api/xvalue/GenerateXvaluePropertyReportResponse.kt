package sg.searchhouse.agentconnect.model.api.xvalue
import com.google.gson.annotations.SerializedName

data class GenerateXvaluePropertyReportResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("url")
    val url: String
)