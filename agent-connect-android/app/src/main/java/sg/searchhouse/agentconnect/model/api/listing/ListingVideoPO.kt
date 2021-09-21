package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.StringUtil

// Assumption: Only youtube video with format e.g. https://youtu.be/74RSKOHAqyg
class ListingVideoPO(
    @SerializedName("url")
    val url: String
): ListingMedia {
    override fun getMediaUrl(): String {
        return url
    }

    override fun getMediaThumbnailUrl(): String {
        return if (StringUtil.isYoutubeUrlValid(getMediaUrl())) {
            // Sample media URL: https://youtu.be/74RSKOHAqyg
            val youtubeId = url.replace("https://youtu.be/", "")
            return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
        } else {
            ""
        }
    }
}
