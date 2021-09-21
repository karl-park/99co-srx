package sg.searchhouse.agentconnect.model.api.propertyindex

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.PropertyIndexEnum
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import java.util.*

data class LoadMarketWatchGraphResponse(
    @SerializedName("priceGraphData")
    val priceGraphData: List<GraphData>
) {
    data class GraphData(
        // Format e.g. Jan 2006
        @SerializedName("date")
        val date: String,
        @SerializedName("value")
        val value: String,
        @SerializedName("volume")
        val volume: String
    ) {
        fun getYear(): String {
            return DateTimeUtil.getConvertedFormatDate(
                date,
                DateTimeUtil.FORMAT_DATE_8,
                DateTimeUtil.FORMAT_YEAR
            )
        }

        fun getYearMonth(): String {
            return DateTimeUtil.getConvertedFormatDate(
                date,
                DateTimeUtil.FORMAT_DATE_8,
                DateTimeUtil.FORMAT_YEAR_MONTH
            )
        }

        fun getMonth(): String {
            return DateTimeUtil.getConvertedFormatDate(
                date,
                DateTimeUtil.FORMAT_DATE_8,
                DateTimeUtil.FORMAT_MONTH
            )
        }

        private fun getCalendar(): Calendar {
            return DateTimeUtil.convertStringToCalendar(date, DateTimeUtil.FORMAT_DATE_8)
        }

        fun getMillis(): Long {
            return getCalendar().timeInMillis
        }

        fun isJanuary(): Boolean {
            return getCalendar().get(Calendar.MONTH) == Calendar.JANUARY
        }

        private fun getFormattedValue(): String {
            return NumberUtil.formatThousand(value.toDouble(), decimalPlace = 1)
        }

        private fun getFormattedVolume(): String {
            return NumberUtil.formatThousand(volume.toInt())
        }

        fun getMarkerLabel(context: Context, indicator: PropertyIndexEnum.Indicator): String {
            return when (indicator) {
                PropertyIndexEnum.Indicator.PRICE -> {
                    context.getString(
                        R.string.marker_market_watch_index,
                        getFormattedDate(),
                        getFormattedValue()
                    )
                }
                PropertyIndexEnum.Indicator.VOLUME -> {
                    context.getString(
                        R.string.marker_market_watch_volume,
                        getFormattedDate(),
                        getFormattedVolume()
                    )
                }
            }
        }

        private fun getFormattedDate(): String {
            val year = DateTimeUtil.getConvertedFormatDate(
                date,
                DateTimeUtil.FORMAT_DATE_8,
                DateTimeUtil.FORMAT_YEAR_SHORT
            )
            val month = DateTimeUtil.getConvertedFormatDate(
                date,
                DateTimeUtil.FORMAT_DATE_8,
                DateTimeUtil.FORMAT_MONTH
            )
            return "$month \'$year"
        }
    }
}