package sg.searchhouse.agentconnect.model.api.propertynews

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.ApiUtil

data class OnlineCommunicationPO(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("contentShort")
    val contentShort: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("encryptedId")
    val encryptedId: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("imageUrlSmall")
    val imageUrlSmall: String,
    @SerializedName("datePosted")
    val datePosted: String
) {
    fun getFormattedContent(): String {
        return "<style>img{display: inline;height: auto;max-width: 100%;}p{line-height:1.6}div{line-height:1.6}</style>$content"
    }

    fun getPropertyNewsUrl(context: Context): String {
        return "${ApiUtil.getBaseUrl(context)}/singapore-property-news/$id/$slug"
    }
}