package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia
import java.io.Serializable

data class PhotoPO(
    @SerializedName("url")
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerializedName("type")
    val type: String
) : Serializable, ListingMedia {
    override fun getMediaUrl(): String {
        return url
    }

    override fun getMediaThumbnailUrl(): String {
        return thumbnailUrl
    }

    enum class Type(val value: String) {
        SITE_MAP("S"), ELEVATION_MAP("E")
    }
}