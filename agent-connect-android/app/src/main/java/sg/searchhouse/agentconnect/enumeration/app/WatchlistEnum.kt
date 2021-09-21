package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class WatchlistEnum {
    enum class WatchlistType(val value: Int, @StringRes val label: Int) {
        TRANSACTIONS(1, R.string.watchlist_type_transactions),
        LISTINGS(2, R.string.watchlist_type_listings)
    }

    enum class BedroomCount(@StringRes val label: Int, val value: String?) {
        ANY(R.string.room_count_any, null),
        ONE(R.string.room_count_one, "1"),
        TWO(R.string.room_count_two, "2"),
        THREE(R.string.room_count_three, "3"),
        FOUR(R.string.room_count_four, "4"),
        FIVE(R.string.room_count_five, "5"),
        SIX_AND_ABOVE(R.string.room_count_six_and_above, "6+")
    }

    enum class BathroomCount(@StringRes val label: Int, val value: String?) {
        ANY(R.string.room_count_any, null),
        ONE(R.string.room_count_one, "1"),
        TWO(R.string.room_count_two, "2"),
        THREE(R.string.room_count_three, "3"),
        FOUR(R.string.room_count_four, "4"),
        FIVE(R.string.room_count_five, "5"),
        SIX_AND_ABOVE(R.string.room_count_six_and_above, "6+")
    }

    enum class AreaType(val value: String?, @StringRes val label: Int) {
        ANY(null, R.string.label_any),
        STRATA("S", R.string.area_type_strata),
        LAND("L", R.string.area_type_land)
    }

    enum class RentalType(val value: Boolean?, @StringRes val label: Int) {
        ANY(null, R.string.label_rental_type_any),
        ENTIRE_HOUSE(false, R.string.label_rental_type_entire_house),
        ROOM_RENTAL(true, R.string.label_rental_type_room_rental)
    }

    enum class ProjectType(val value: Boolean?, @StringRes val label: Int) {
        ALL(null, R.string.label_watchlist_project_type_any),
        NEW_PROJECTS(true, R.string.label_watchlist_project_type_new),
        RESALE_PROJECTS(false, R.string.label_watchlist_project_type_resale)
    }
}