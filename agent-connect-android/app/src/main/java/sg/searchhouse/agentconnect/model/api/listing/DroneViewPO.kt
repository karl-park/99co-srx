package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class DroneViewPO(
    @SerializedName("type")
    val type: Int,
    @SerializedName("typeDescription")
    val typeDescription: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerializedName("description")
    val description: String
): ListingMedia {
    override fun getMediaUrl(): String {
        return url
    }

    override fun getMediaThumbnailUrl(): String {
        return thumbnailUrl
    }
}