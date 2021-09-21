package sg.searchhouse.agentconnect.model.api.homereport

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil

data class GetHomeReportUsageResponse(
    @SerializedName("result")
    val result: Result
) {
    data class Result(
        @SerializedName("genForMonth")
        val genForMonth: Int,
        @SerializedName("genForMonthPct")
        val genForMonthPct: Double,
        @SerializedName("genForYear")
        val genForYear: Int,
        @SerializedName("genForYearPct")
        val genForYearPct: Double
    ) {
        fun getFormattedGenForMonth(): String {
            return NumberUtil.formatThousand(genForMonth)
        }

        fun getFormattedGenForYear(): String {
            return NumberUtil.formatThousand(genForYear)
        }

        fun getFormattedGenForMonthPct(): Float {
            return (genForMonthPct / 100).toFloat()
        }

        fun getFormattedGenForYearPct(): Float {
            return (genForYearPct / 100).toFloat()
        }

        fun getGenForMonthCounter(context: Context): String {
            return context.resources.getQuantityString(
                R.plurals.home_report_usage_report_counter,
                genForMonth
            )
        }

        fun getGenForYearCounter(context: Context): String {
            return context.resources.getQuantityString(
                R.plurals.home_report_usage_report_counter,
                genForYear
            )
        }
    }
}