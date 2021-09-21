package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

@Suppress("unused")
class XValueEnum {
    enum class PropertyType(
        val value: Int
    ) {
        ANY(0),
        HDB(1),
        PRIVATE_RESIDENTIAL(2),
        COMMERCIAL_INDUSTRIAL(3)
    }

    enum class AreaType(val value: Int, val stringValue: String, @StringRes val label: Int) {
        STRATA(1, "S", R.string.area_type_strata), LAND(2, "L", R.string.area_type_land)
    }

    enum class Tenure(val value: Int, @StringRes val label: Int) {
        FREEHOLD(
            1,
            R.string.tenure_label_full_freehold
        ),
        LEASEHOLD(
            2,
            R.string.tenure_label_full_leasehold
        ),
    }

    enum class GetExistingXValuesProperty(val value: String, @StringRes val label: Int) {
        DATE("date", R.string.x_value_sort_date)
    }

    enum class GetExistingXValuesOrder(val value: String, @StringRes val label: Int) {
        DESC("desc", R.string.x_value_sort_date_desc), ASC("asc", R.string.x_value_sort_date_asc)
    }

    enum class OwnershipType(val value: String) {
        SALE("S"),
        RENT("R"),
        BOTH("B")
    }

    enum class TimeScale(val numQuarters: Int, @StringRes val label: Int, val smallerNeighbor: TimeScale?) {
        ONE_YEAR(4, R.string.x_trend_time_scale_one_year, null),
        THREE_YEARS(12, R.string.x_trend_time_scale_three_years, ONE_YEAR),
        FIVE_YEARS(20, R.string.x_trend_time_scale_five_years, THREE_YEARS),
        TEN_YEARS(40, R.string.x_trend_time_scale_ten_years, FIVE_YEARS)
    }
}