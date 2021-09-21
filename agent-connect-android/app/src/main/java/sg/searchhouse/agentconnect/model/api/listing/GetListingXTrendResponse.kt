package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.xvalue.XTrendKeyValue
import sg.searchhouse.agentconnect.util.JsonUtil

data class GetListingXTrendResponse(
    // Return either list of XTrendKeyValue or "-" (when empty)
    @SerializedName("xValueTrend")
    val xValueTrend: Any?
) {
    fun getXValueTrendList(): List<XTrendKeyValue> {
        return xValueTrend?.run {
            JsonUtil.parseListOrEmpty(
                toString(),
                XTrendKeyValue::class.java
            )
        } ?: emptyList()
    }
}