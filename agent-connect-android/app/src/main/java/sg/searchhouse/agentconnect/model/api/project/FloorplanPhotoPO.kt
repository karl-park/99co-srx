package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia
import java.io.Serializable

data class FloorplanPhotoPO(
    @SerializedName("url")
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerializedName("blk")
    val blk: String,
    @SerializedName("floorNo")
    val floorNo: String,
    @SerializedName("unitNo")
    val unitNo: String
) : Serializable, ListingMedia {
    override fun getMediaUrl(): String {
        return url
    }

    override fun getMediaThumbnailUrl(): String {
        return thumbnailUrl
    }

    fun getBlockFloorUnit(): String {
        return "Blk $blk #${floorNo}-${unitNo}"
    }
}