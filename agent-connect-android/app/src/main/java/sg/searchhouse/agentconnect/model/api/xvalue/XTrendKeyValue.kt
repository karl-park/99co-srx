package sg.searchhouse.agentconnect.model.api.xvalue


import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import android.content.Context
import sg.searchhouse.agentconnect.R
import java.util.*


data class XTrendKeyValue(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: Int
) {

    fun getYearAndMonthLabel(): String {
        return DateTimeUtil.getConvertedFormatDate(
            key,
            DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN,
            DateTimeUtil.FORMAT_YEAR_MONTH
        )
    }

    fun getMonthLabel(): String {
        return DateTimeUtil.getConvertedFormatDate(
            key,
            DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN,
            DateTimeUtil.FORMAT_MONTH
        )
    }

    var isLatest = false

    fun getYear(): Int {
        val yearString = DateTimeUtil.getConvertedFormatDate(
            key,
            DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN,
            DateTimeUtil.FORMAT_YEAR
        )
        val year = NumberUtil.toInt(yearString)
        if (year == null) {
            ErrorUtil.handleError("XTrendKeyValue key got invalid date format: $key")
        }
        return NumberUtil.toInt(yearString) ?: 0
    }

    private fun getYearQuarter(): String {
        val year = getYear()
        val quarter = getQuarter()
        return "$year Q$quarter"
    }

    fun getQuarterYearOrLatest(context: Context): String {
        return if (!isLatest) {
            val year = getYear()
            val quarter = getQuarter()
            "Q$quarter $year"
        } else {
            context.getString(R.string.label_latest)
        }
    }

    fun getMarkerLabel(): String {
        return "${getYearQuarter()}\n${getFormattedValue()}"
    }

    fun getFormattedValue(): String {
        return NumberUtil.getFormattedCurrency(value)
    }

    fun getQuarter(): Int {
        val calendar =
            DateTimeUtil.convertStringToCalendar(key, DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN)
        val month = calendar.get(Calendar.MONTH)
        return (month / 3) + 1
    }

    fun getMillis(): Long {
        return DateTimeUtil.convertStringToCalendar(key, DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN)
            .timeInMillis
    }

    companion object {
        fun isSameYearQuarter(
            xTrendKeyValue1: XTrendKeyValue,
            xTrendKeyValue2: XTrendKeyValue
        ): Boolean {
            return xTrendKeyValue1.getYear() == xTrendKeyValue2.getYear() && xTrendKeyValue1.getQuarter() == xTrendKeyValue2.getQuarter()
        }
    }
}