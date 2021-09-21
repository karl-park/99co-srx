package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

@Suppress("unused")
class CommunityEnum {
    enum class CommunityLevel(val value: Int) {
        PRIVATE(1),
        ESTATE(64),
        SUB_ZONE(128),
        ELECTORAL(132),
        PLANNING_AREA(136),
        COUNTRY(144)
    }

    // TODO Populate proper value, current ones are placeholders only
    enum class SponsorDuration(val value: Int, @StringRes val label: Int, @StringRes val labelLong: Int) {
        ONE_WEEK(1, R.string.sponsor_duration_one_week, R.string.sponsor_duration_long_one_week),
        TWO_WEEKS(2, R.string.sponsor_duration_two_weeks, R.string.sponsor_duration_long_two_weeks),
        THREE_WEEKS(3, R.string.sponsor_duration_three_weeks, R.string.sponsor_duration_long_three_weeks),
        FOUR_WEEKS(4, R.string.sponsor_duration_four_weeks, R.string.sponsor_duration_long_four_weeks),
    }
}