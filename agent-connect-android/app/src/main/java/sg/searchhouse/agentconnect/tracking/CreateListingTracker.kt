package sg.searchhouse.agentconnect.tracking

import android.content.Context
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsEventKeys
import sg.searchhouse.agentconnect.enumeration.app.FirebaseAnalyticsPropertyKeys
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.FirebaseAnalyticsTrackingUtil

object CreateListingTracker {

    fun trackListingCreationStartTime(
        context: Context,
        listingId: String,
        createListing: CreateListingTrackerPO
    ) {
        val properties = hashMapOf<String, String>().apply {
            put(FirebaseAnalyticsPropertyKeys.CREATE_LISTING_LISTING_ID.key, listingId)
            put(
                FirebaseAnalyticsPropertyKeys.CREATE_LISTING_TIMESTAMP.key,
                createListing.timeStamp.toString()
            )
        }
        FirebaseAnalyticsTrackingUtil.trackFirebaseAnalyticsEvents(
            context = context,
            event = FirebaseAnalyticsEventKeys.CREATE_LISTING_BUTTON_CLICKED,
            properties = properties
        )
    }

    fun trackListingCreationEndTime(context: Context, listingId: String) {
        val properties = hashMapOf<String, String>().apply {
            put(FirebaseAnalyticsPropertyKeys.CREATE_LISTING_LISTING_ID.key, listingId)
            put(
                FirebaseAnalyticsPropertyKeys.CREATE_LISTING_TIMESTAMP.key,
                DateTimeUtil.getCurrentTimeInMillis().toString()
            )
        }
        FirebaseAnalyticsTrackingUtil.trackFirebaseAnalyticsEvents(
            context = context,
            event = FirebaseAnalyticsEventKeys.PUBLISH_LISTING_SUCCESS,
            properties = properties
        )
    }
}