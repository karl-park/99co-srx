package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName

data class GetExistingXValuesResponse(
    @SerializedName("xvalues")
    val xvalues: Xvalues
) {
    data class Xvalues(
        @SerializedName("results")
        val results: List<XValue>,
        @SerializedName("total")
        val total: Int
    )
}