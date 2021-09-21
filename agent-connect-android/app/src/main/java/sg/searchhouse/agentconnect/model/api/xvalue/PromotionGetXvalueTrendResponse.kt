package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.JsonUtil

data class PromotionGetXvalueTrendResponse(
    // Return either list of XTrendKeyValue or "-" (when empty)
    @SerializedName("xValueObjTrend")
    val xValueObjTrend: Any?,
    @SerializedName("xValueTrend")
    val xValueTrend: String
) {
    fun getXValueObjTrendList(): List<XTrendKeyValue>? {
        return xValueObjTrend?.run {
            JsonUtil.parseListOrNull(
                toString(),
                XTrendKeyValue::class.java
            )
        }
    }
}