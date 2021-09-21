package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class ListingVirtualTourPO(
    @SerializedName("url")
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String
): ListingMedia {
    override fun getMediaUrl(): String {
        return url
    }

    override fun getMediaThumbnailUrl(): String {
        return thumbnailUrl
    }
}
