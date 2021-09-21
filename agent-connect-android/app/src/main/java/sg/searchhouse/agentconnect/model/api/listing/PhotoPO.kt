package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PhotoPO (
    @SerializedName("url")
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String
): Serializable, ListingMedia {
    override fun getMediaUrl(): String {
        return url
    }

    override fun getMediaThumbnailUrl(): String {
        return thumbnailUrl
    }
}