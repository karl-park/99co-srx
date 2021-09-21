package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class CriteriaFormType(@StringRes val title: Int) {
    ADD(R.string.activity_add_watchlist_criteria), EDIT(R.string.activity_edit_watchlist_criteria)
}