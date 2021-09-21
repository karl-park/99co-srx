package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant

class ReportEnum {

    enum class ReportType(val value: Int, @StringRes val label: Int) {
        HOME_REPORT(1, R.string.label_report_home_report),
        X_VALUE_REPORT(2, R.string.label_report_x_value_report),

        //AFFORDABILITY_REPORT(3, R.string.label_report_affordability_report),
        NEW_LAUNCHES_REPORT(4, R.string.label_report_new_launches_report),
        FLASH_REPORT(5, R.string.label_report_flash_report)
    }

    enum class OrderCriteria(val property: String, val order: String, @StringRes val label: Int) {
        DATE_DESC("date", "desc", R.string.sort_option_flash_report_date_desc),
        DATE_ASC("date", "asc", R.string.sort_option_flash_report_date_asc)
    }

    enum class FlashReportType(val type: String, @StringRes val label: Int) {
        CONDO_RESALE("CONDO_RESALE", R.string.report_type_condo_resale),
        HDB_RESALE("HDB_RESALE", R.string.report_type_hdb_resale),
        CONDO_AND_HDB_RENTAL("HDB_AND_CONDO_RENTAL", R.string.report_type_condo_and_hdb_rental)
    }

    enum class NewLaunchesReportSortType(
        val property: String,
        val order: String,
        @StringRes val label: Int
    ) {
        NEW_LAUNCH_DATE_DESC(
            "LAUNCHDATE",
            "desc",
            R.string.label_new_launch_reports_sort_launch_date_desc
        ),
        NEW_LAUNCH_DATE_ASC(
            "LAUNCHDATE",
            "asc",
            R.string.label_new_launch_reports_sort_launch_date_asc
        )
    }

    //Note: backend never standardize values. create enum classed every times add modules
    //TODO: remove this enum in future and use MAIN TYPE from listing enum
    enum class NewLaunchesPropertyType(
        val value: Int,
        val propertySubType: List<ListingEnum.PropertySubType>,
        @StringRes val label: Int
    ) {
        CONDO_APT(
            1,
            listOf(
                ListingEnum.PropertySubType.CONDOMINIUM,
                ListingEnum.PropertySubType.APARTMENT
            ),
            R.string.listing_sub_type_condo_apt
        ),
        LANDED(
            2, listOf(
                ListingEnum.PropertySubType.TERRACE,
                ListingEnum.PropertySubType.SEMI_DETACHED,
                ListingEnum.PropertySubType.DETACHED
            ), R.string.property_type_landed
        )
    }

    //note: temp enum for new launches
    enum class NewLaunchesTenure(val value: String, @StringRes val label: Int) {
        ALL("ALL", R.string.label_all),
        FREEHOLD("FREEHOLD", R.string.project_tenure_freehold),
        LEASEHOLD("LEASEHOLD", R.string.project_tenure_leasehold)
    }

    //note : temp enum for new launches
    enum class NewLaunchesCompletion(val value: String, @StringRes val label: Int) {
        ALL("ALL", R.string.label_all),
        UNDER_CONSTRUCTION("UNDER_CONSTRUCTION", R.string.project_completion_under_construction),
        COMPLETED("COMPLETED", R.string.project_completion_completed)
    }

    //TODO: create this enum in ReportEnum first. will move or separate as another file
    enum class SendOutReportOption(val position: Int, @StringRes val label: Int) {
        PHONE_BOOK(0, R.string.label_option_phone_book)
        //TODO: will add clients in next phase
        //MY_CLIENTS(1, R.string.label_option_my_clients)
    }

    enum class ReportServiceType(
        val value: Int,
        val dir: String,
        val initialFileName: String?,
        val fullFileName: String?,
        @StringRes val downloadProgressMessage: Int,
        @StringRes val failedErrorMessage: Int,
        @StringRes val downloadSuccessMessage: Int
    ) {
        NEW_LAUNCHES_REPORTS(
            1,
            AppConstant.DIR_NEW_LAUNCHES_REPORT,
            "new_project_",
            null,
            R.string.notification_download_new_launches_report_progress,
            R.string.notification_download_new_launches_report_error,
            R.string.notification_download_new_launches_report_success
        ),
        FLASH_REPORTS(
            2,
            AppConstant.DIR_FLASH_REPORT,
            "flash_report_",
            null,
            R.string.notification_download_flash_report_progress,
            R.string.notification_download_flash_report_error,
            R.string.notification_download_flash_report_success
        ),
        VALUATION_SAMPLE_REPORT(
            3,
            AppConstant.DIR_MISC,
            null,
            AppConstant.FILE_NAME_VALUATION_SAMPLE_REPORT,
            R.string.notification_download_file_in_progress,
            R.string.toast_download_failed,
            R.string.notification_download_valuation_sample_report
        )
    }
}