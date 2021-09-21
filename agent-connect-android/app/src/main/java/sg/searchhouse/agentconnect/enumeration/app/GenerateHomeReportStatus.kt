package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class GenerateHomeReportStatus(@StringRes val messageResId: Int) {
    GENERATING(R.string.card_generating_home_report),
    DOWNLOADING(R.string.card_downloading_home_report),
    GENERATE_FAIL(R.string.card_generate_home_report_fail),
    GENERATE_ERROR(R.string.card_generate_home_report_error),
    DOWNLOAD_ERROR(R.string.card_download_home_report_error),
    DONE(R.string.card_download_home_report_done)
}