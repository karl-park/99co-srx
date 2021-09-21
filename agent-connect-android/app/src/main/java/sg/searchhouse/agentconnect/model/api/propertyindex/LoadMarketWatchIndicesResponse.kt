package sg.searchhouse.agentconnect.model.api.propertyindex

import android.content.Context
import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.app.MarketDirection
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable
import java.util.*

data class LoadMarketWatchIndicesResponse(
    @SerializedName("hdb")
    val hdb: SrxIndexPO,
    @SerializedName("hdbRent")
    val hdbRent: SrxIndexPO,
    @SerializedName("hdbRentalSubIndexes")
    val hdbRentalSubIndexes: HdbSubIndexes,
    @SerializedName("hdbSubIndexes")
    val hdbSubIndexes: HdbSubIndexes,
    @SerializedName("lastUpdated")
    val lastUpdated: String,
    @SerializedName("nonLandedPrivate")
    val nonLandedPrivate: SrxIndexPO,
    @SerializedName("nonLandedPrivateRent")
    val nonLandedPrivateRent: SrxIndexPO,
    @SerializedName("nonLandedPrivateRentalSubIndexes")
    val nonLandedPrivateRentalSubIndexes: NonLandedPrivateSubIndexes,
    @SerializedName("nonLandedPrivateSubIndexes")
    val nonLandedPrivateSubIndexes: NonLandedPrivateSubIndexes
) {
    data class HdbSubIndexes(
        @SerializedName("hdb3Room")
        val hdb3Room: SrxIndexPO,
        @SerializedName("hdb4Room")
        val hdb4Room: SrxIndexPO,
        @SerializedName("hdb5Room")
        val hdb5Room: SrxIndexPO,
        @SerializedName("hdbExecutive")
        val hdbExecutive: SrxIndexPO
    )

    data class NonLandedPrivateSubIndexes(
        @SerializedName("ccr")
        val ccr: SrxIndexPO,
        @SerializedName("ocr")
        val ocr: SrxIndexPO,
        @SerializedName("rcr")
        val rcr: SrxIndexPO
    )

    data class SrxIndexPO(
        @SerializedName("date")
        val date: String,
        @SerializedName("dateUpd")
        val dateUpd: String,
        @SerializedName("increase")
        val increase: String,
        @SerializedName("month")
        val month: Int,
        @SerializedName("monthlyVolChange")
        val monthlyVolChange: String,
        @SerializedName("value")
        val value: String,
        @SerializedName("volume")
        val volume: String,
        @SerializedName("year")
        val year: Int,
        @SerializedName("yoyIndexChange")
        val yoyIndexChange: String,
        @SerializedName("yoyVolChange")
        val yoyVolChange: String
    ) : Serializable {
        fun getMonthlyVolChangeArrow(context: Context): Drawable? {
            return getArrow(context, monthlyVolChange)
        }

        fun getIncreaseArrow(context: Context): Drawable? {
            return getArrow(context, increase)
        }

        fun getMonthlyVolChangeLabel(): String {
            return "${getDouble(monthlyVolChange)}%"
        }

        fun getIncreaseLabel(): String {
            return "${getDouble(increase)}%"
        }

        @Throws(NumberFormatException::class)
        private fun getDouble(numberString: String): Double {
            return NumberUtil.roundDouble(numberString.toDouble(), 1)
        }

        fun getIncreaseMarketDirection(): MarketDirection {
            return getMarketDirection(increase)
        }

        fun getMonthlyVolChangeMarketDirection(): MarketDirection {
            return getMarketDirection(monthlyVolChange)
        }

        private fun getMarketDirection(numberString: String): MarketDirection {
            val value = getDouble(numberString)
            return when {
                value > 0.0 -> MarketDirection.UP
                value < 0.0 -> MarketDirection.DOWN
                else -> MarketDirection.NEUTRAL
            }
        }

        private fun getArrow(context: Context, numberString: String): Drawable? {
            return context.getDrawable(getMarketDirection(numberString).arrowDrawable)
        }

        fun getFormattedDate(): String {
            return getFormattedDateCore(date)
        }

        fun getFormattedDatePreviousMonth(): String {
            val calendar = DateTimeUtil.convertStringToCalendar(date, DateTimeUtil.FORMAT_DATE_8)
            calendar.add(Calendar.MONTH, -1)
            val dateString = DateTimeUtil.convertDateToString(calendar.time, DateTimeUtil.FORMAT_DATE_8)
            return getFormattedDateCore(dateString)
        }

        private fun getFormattedDateCore(date: String): String {
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