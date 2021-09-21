package sg.searchhouse.agentconnect.enumeration.api

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class CalculatorEnum {
    enum class PropertyType(val value: String) { RESIDENTIAL("resd"), COMMERCIAL("comm") }

    enum class AdvancedPropertyType(val value: String, @StringRes val label: Int) {
        HDB("hdb", R.string.advanced_property_type_hdb),
        PRIVATE("private", R.string.advanced_property_type_private)
    }

    enum class PropertyNumber(val label: String, val value: Int) {
        ZERO("0", 0),
        ONE("1", 1),
        TWO_PLUS("2+", 2)
    }

    enum class ApplicationType(val value: String, @StringRes val label: Int) {
        SINGLE("single", R.string.buyer_application_type_single),
        JOINT_APPLICANT("joint", R.string.buyer_application_type_joint_applicant)
    }

    enum class BuyerProfile(val value: String, @StringRes val label: Int) {
        SINGAPOREAN("sporean", R.string.buyer_profile_singaporean),
        SPR("spr", R.string.buyer_profile_pr),
        FTA("fta", R.string.buyer_profile_fta),
        NON_FTA("non-fta", R.string.buyer_profile_non_fta)
    }
}