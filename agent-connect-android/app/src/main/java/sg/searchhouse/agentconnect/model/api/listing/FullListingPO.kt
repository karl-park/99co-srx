package sg.searchhouse.agentconnect.model.api.listing

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.agent.AgentPO

data class FullListingPO(
    @SerializedName("listingDetailPO")
    val listingDetailPO: ListingDetailPO,
    @SerializedName("listingPhotoPO")
    val listingPhotoPO: ListingPhotoPO,
    @SerializedName("agentPO")
    val agentPO: AgentPO
) {
    private fun getListingMedias(): List<ListingMedia> {
        val listingPO = listingDetailPO.listingPO
        val virtualTour = listingPO.virtualTour?.let { listOf(it) } ?: emptyList()
        val droneViews = listingPO.droneViews
        val videoPOs = listingPO.videoPOs
        return droneViews + virtualTour + videoPOs + listingPhotoPO.listingImageLinks
    }

    fun getValidListingMedias(): List<ListingMedia> {
        return getListingMedias().filter {
            !TextUtils.isEmpty(it.getMediaUrl()) && !TextUtils.isEmpty(it.getMediaThumbnailUrl())
        }
    }
}