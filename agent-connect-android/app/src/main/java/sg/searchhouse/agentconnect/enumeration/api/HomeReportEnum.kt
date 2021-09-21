package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class HomeReportEnum {
    enum class Tenure(val value: String, @StringRes val label: Int) {
        FREEHOLD("FH", R.string.home_report_tenure_free_hold),
        LEASEHOLD("LH", R.string.home_report_tenure_lease_hold)
    }
}