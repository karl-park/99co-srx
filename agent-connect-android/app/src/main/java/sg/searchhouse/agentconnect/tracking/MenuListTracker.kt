package sg.searchhouse.agentconnect.tracking

import android.content.Context
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.CalculatorType
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsEventKeys
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsPropertyKeys
import sg.searchhouse.agentconnect.enumeration.app.SettingMenu
import sg.searchhouse.agentconnect.util.FirebaseAnalyticsTrackingUtil

object MenuListTracker {

    fun trackMenuItemClicked(context: Context, menuItem: SettingMenu) {
        val properties = hashMapOf<String, String>().apply {
            put(FirebaseAnalyticsPropertyKeys.MENU_ITEM_NAME.key, context.getString(menuItem.label))
        }

        FirebaseAnalyticsTrackingUtil.trackFirebaseAnalyticsEvents(
            context = context,
            event = FirebaseAnalyticsEventKeys.MENU_ITEM_CLICKED,
            properties = properties
        )
    }

    fun trackCalculatorMenuOptionClicked(context: Context, calculatorType: CalculatorType?) {
        val properties = hashMapOf<String, String>().apply {
            put(
                FirebaseAnalyticsPropertyKeys.CALCULATOR_MENU_OPTION_NAME.key,
                context.getString(calculatorType?.label ?: R.string.calculator_type_all)
            )
        }

        FirebaseAnalyticsTrackingUtil.trackFirebaseAnalyticsEvents(
            context = context,
            event = FirebaseAnalyticsEventKeys.CALCULATOR_MENU_OPTION_CLICKED,
            properties = properties
        )
    }

}