package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class CalculatorAppEnum {
    enum class CalculatorType(val position: Int, @StringRes val label: Int) {
        AFFORDABILITY_QUICK(0, R.string.calculator_type_affordability_quick),
        AFFORDABILITY_ADVANCED(1, R.string.calculator_type_affordability_advanced),
        SELLING(2, R.string.calculator_type_selling),
        STAMP_DUTY(3, R.string.calculator_type_stamp_duty)
    }

    enum class SellingCalculatorEntryType {
        SELLING, SELLING_STAMP_DUTY
    }
}