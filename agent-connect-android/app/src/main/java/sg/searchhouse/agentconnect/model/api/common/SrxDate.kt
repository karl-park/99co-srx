package sg.searchhouse.agentconnect.model.api.common

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.DateTimeUtil
import java.io.Serializable

data class SrxDate(
    @SerializedName("date")
    val date: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("hours")
    val hours: Int,
    @SerializedName("minutes")
    val minutes: Int,
    @SerializedName("month")
    val month: Int,
    @SerializedName("nanos")
    val nanos: Int,
    @SerializedName("seconds")
    val seconds: Int,
    @SerializedName("time")
    val time: Long,
    @SerializedName("timezoneOffset")
    val timezoneOffset: Int,
    @SerializedName("year")
    val year: Int
) : Serializable {
    fun getFormattedDate4(): String {
        return DateTimeUtil.convertTimeStampToString(time, DateTimeUtil.FORMAT_DATE_4)
    }
}