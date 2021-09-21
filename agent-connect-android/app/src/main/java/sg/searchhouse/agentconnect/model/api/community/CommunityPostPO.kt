package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1140719619/Communities+Data+Structure
 */
data class CommunityPostPO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("parentPost")
    val parentPost: CommunityPostPO?,
    @SerializedName("user")
    val user: UserPO,
    @SerializedName("type")
    val type: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("externalUrl")
    val externalUrl: String,
    @SerializedName("dateCreate")
    val dateCreate: String,
    @SerializedName("commentsTotal")
    val commentsTotal: Int,
    @SerializedName("listing")
    val listing: ListingPO?,
    @SerializedName("community")
    val community: CommunityPO,
    @SerializedName("media")
    val media: List<CommunityPostMediaPO>,
    @SerializedName("comments")
    val comments: List<CommunityPostPO>,
    @SerializedName("reactions")
    val reactions: List<CommunityPostReactionPO>,
    @SerializedName("transactedListings")
    val transactedListings: List<ListingPO>
) : Serializable