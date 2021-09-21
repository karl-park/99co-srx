package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

@Suppress("unused")
class PropertyIndexEnum {
    enum class PropertyType(
        val value: String
    ) {
        HDB("HDB"),
        NLP("NLP") // Non Landed Private
    }

    enum class TransactionType(
        val value: String
    ) {
        SALE("SALE"),
        RENTAL("RENTAL")
    }

    enum class Indicator {
        PRICE, VOLUME
    }

    enum class TimeScale(val numMonths: Int, @StringRes val label: Int, val smallerNeighbor: TimeScale?) {
        ONE_YEAR(12, R.string.x_trend_time_scale_one_year, null),
        THREE_YEARS(36, R.string.x_trend_time_scale_three_years, ONE_YEAR),
        FIVE_YEARS(60, R.string.x_trend_time_scale_five_years, THREE_YEARS),
        TEN_YEARS(120, R.string.x_trend_time_scale_ten_years, FIVE_YEARS)
    }
}