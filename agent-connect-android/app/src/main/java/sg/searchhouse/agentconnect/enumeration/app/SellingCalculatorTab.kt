package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class SellingCalculatorTab(val position: Int, @StringRes val label: Int) {
    LOOK_UP_PROPERTY(0, R.string.tab_calculator_selling_look_up_property),
    ENTER_DETAILS(1, R.string.tab_calculator_selling_enter_details)
}