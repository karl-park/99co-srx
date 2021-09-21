package sg.searchhouse.agentconnect.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsDefaultPropertyKeys.*
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsEventKeys

object FirebaseAnalyticsTrackingUtil {

    fun trackFirebaseAnalyticsEvents(
        context: Context,
        event: FirebaseAnalyticsEventKeys,
        properties: Map<String, String>
    ) {
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        firebaseAnalytics.setDefaultEventParameters(generateDefaultEventParameters(context))
        setUserProperties(firebaseAnalytics)
        firebaseAnalytics.logEvent(event.key) {
            properties.map { property ->
                param(property.key, property.value)
            }
        }
    }

    private fun generateDefaultEventParameters(context: Context): Bundle {
        return Bundle().apply {
            SessionUtil.getCurrentUser()?.let { currentUser ->
                putString(USER_ID.key, currentUser.id.toString())
            }
            putString(DEVICE_ID.key, PackageUtil.getDeviceId(context))
        }
    }

    private fun setUserProperties(firebaseAnalytics: FirebaseAnalytics) {
        SessionUtil.getCurrentUser()?.let { currentUser ->
            firebaseAnalytics.setUserProperty(SRX_USER_ID.key, currentUser.id.toString())
            firebaseAnalytics.setUserProperty(EMAIL.key, currentUser.email)
        }
    }
}