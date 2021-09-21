package sg.searchhouse.agentconnect.util

import android.content.Context
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.model.api.listing.DroneViewPO
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia
import sg.searchhouse.agentconnect.model.api.listing.ListingVirtualTourPO

object ListingMediaUtil {
    // Strategy to minimize memory overflow:
    // Setup web view only when free memory more than 40% of total
    // otherwise just show thumbnail
    fun isMemoryPermitted(): Boolean {
        val runtime = Runtime.getRuntime()
        val total = runtime.totalMemory()
        val free = runtime.freeMemory()
        return free > 0.4 * total
    }

    fun getMediaTitle(context: Context, listingMedia: ListingMedia): String {
        return context.getString(
            when (listingMedia) {
                is ListingVirtualTourPO -> R.string.listing_media_tag_virtual_tour
                is DroneViewPO -> R.string.listing_media_tag_drone_view
                else -> R.string.listing_media_tag_generic
            }
        )
    }
}