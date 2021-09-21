package sg.searchhouse.agentconnect.tracking

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsEventKeys
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsPropertyKeys
import sg.searchhouse.agentconnect.util.FirebaseAnalyticsTrackingUtil
import sg.searchhouse.agentconnect.view.adapter.main.MainBottomNavigationTab
import sg.searchhouse.agentconnect.view.widget.main.BottomAppBar

object NavigationBarTabTracker {

    fun trackNavigationBarTabClicked(context: Context, tab: MainBottomNavigationTab) {
        val properties = hashMapOf<String, String>().apply {
            put(FirebaseAnalytics.Param.SCREEN_CLASS, tab.screen)
            put(FirebaseAnalyticsPropertyKeys.NAVIGATION_BAR_TAB_NAME.key, tab.key)
        }

        FirebaseAnalyticsTrackingUtil.trackFirebaseAnalyticsEvents(
            context = context,
            event = FirebaseAnalyticsEventKeys.NAVIGATION_BAR_TAB_CLICKED,
            properties = properties
        )
    }

    fun trackBottomAppBarOptionClicked(context: Context, action: BottomAppBar.Action) {
        val properties = hashMapOf<String, String>().apply {
            put(FirebaseAnalyticsPropertyKeys.BOTTOM_APP_BAR_OPTION_NAME.key, action.key)
        }
        FirebaseAnalyticsTrackingUtil.trackFirebaseAnalyticsEvents(
            context = context,
            event = FirebaseAnalyticsEventKeys.X_TAB_OPTION_CLICKED,
            properties = properties
        )
    }
}