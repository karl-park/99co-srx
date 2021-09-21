package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class DateTimeEnum {
    enum class DayPeriod(val label: String, val position: Int) {
        AM("AM", 0), PM("PM", 1)
    }

    enum class Month(@StringRes val label: Int, val position: Int) {
        JAN(R.string.month_jan, 0),
        FEB(R.string.month_feb, 1),
        MAR(R.string.month_mar, 2),
        APR(R.string.month_apr, 3),
        MAY(R.string.month_may, 4),
        JUN(R.string.month_jun, 5),
        JUL(R.string.month_jul, 6),
        AUG(R.string.month_aug, 7),
        SEP(R.string.month_sep, 8),
        OCT(R.string.month_oct, 9),
        NOV(R.string.month_nov, 10),
        DEC(R.string.month_dec, 11)
    }
}