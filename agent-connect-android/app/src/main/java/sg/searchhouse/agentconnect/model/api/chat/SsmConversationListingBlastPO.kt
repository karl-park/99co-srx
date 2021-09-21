package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

data class SsmConversationListingBlastPO(
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("unreadCount")
    val unreadCount: Int,
    @SerializedName("listingsBlasted")
    val listingsBlasted: List<ListingPO>
)