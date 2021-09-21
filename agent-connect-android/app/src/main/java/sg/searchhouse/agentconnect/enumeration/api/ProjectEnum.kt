package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class ProjectEnum {

    enum class FilterType(val value: String) {
        ALL("ALL")
    }

    enum class DecisionDate(val months: Int, @StringRes val label: Int) {
        ALL(0, R.string.decision_date_all),
        THREE_MONTHS(3, R.string.decision_date_three_month),
        SIX_MONTHS(6, R.string.decision_date_six_months),
        ONE_YEAR(12, R.string.decision_date_one_year),
        TWO_YEARS(24, R.string.decision_date_two_years),
        FIVE_YEARS(60, R.string.decision_date_five_years)
    }

    enum class SearchMode(val value: Int) {
        RESIDENTIAL(1),
        COMMERCIAL(2)
    }

    enum class SortType(val property: String, val order: String, @StringRes val label: Int) {
        NAME_ASC("name", "asc", R.string.label_project_sort_name_asc),
        DATE_DESC("date", "desc", R.string.label_project_sort_date_desc),
        DATE_ASC("date", "asc", R.string.label_project_sort_date_asc)
    }
    
    enum class ProjectRadius(val value: Int, @StringRes val label: Int) {
        ANY(0, R.string.project_radius_any),
        HALF_KM(1, R.string.project_radius_half_km),
        ONE_KM(2, R.string.project_radius_one_km),
        ONE_AND_HALF_KM(3, R.string.project_radius_one_half_km),
        TWO_KM(4, R.string.project_radius_two_km)
    }

    enum class TypeOfArea(val value: Int, @StringRes val label: Int) {
        ANY(0, R.string.project_type_of_area_any),
        STRATA(1, R.string.project_type_of_area_strata),
        LAND(2, R.string.project_type_of_area_land)
    }

    enum class Completion(val value: Int, @StringRes val label: Int) {
        ANY(-10, R.string.project_completion_any),
        COMPLETED(0, R.string.project_completion_completed),
        NEW_PROJECTS(-2, R.string.project_completion_new_projects),
        UNDER_CONSTRUCTION(-1, R.string.project_completion_under_construction)
    }

    enum class Tenure(val value: Int, @StringRes val label: Int) {
        ANY(0, R.string.project_tenure_any),
        FREEHOLD(1, R.string.project_tenure_freehold),
        LEASEHOLD(2, R.string.project_tenure_leasehold)
    }
}


