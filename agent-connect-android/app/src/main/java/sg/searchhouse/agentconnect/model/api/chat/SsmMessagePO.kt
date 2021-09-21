package sg.searchhouse.agentconnect.model.api.chat

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.dsl.toJsonString
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.db.SsmMessageEntity
import sg.searchhouse.agentconnect.util.DateTimeUtil

data class SsmMessagePO constructor(
    @SerializedName("messageId")
    val messageId: String,
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("dateSent")
    val dateSent: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("thumbUrl")
    val thumbUrl: String,
    @SerializedName("listingId")
    val listingId: Int,
    @SerializedName("listing")
    var listing: ListingPO? = null,
    @SerializedName("isFromOtherUser")
    val isFromOtherUser: Boolean,
    @SerializedName("otherUser")
    val otherUser: String,
    @SerializedName("otherUserName")
    val otherUserName: String,
    @SerializedName("otherUserNumber")
    val otherUserNumber: String,
    @SerializedName("sourceUserId")
    val sourceUserId: String,
    @SerializedName("sourceUserMobileNumber")
    val sourceUserMobileNumber: String,
    @SerializedName("sourceUserName")
    val sourceUserName: String
) {
    fun getFormattedDateSent(context: Context): String {
        return DateTimeUtil.getFormattedDate(
            context,
            dateSent,
            DateTimeUtil.FORMAT_DATE_1
        )
    }

    fun fromSsmMessageToEntity(): SsmMessageEntity {
        return SsmMessageEntity(
            messageId = messageId,
            conversationId = conversationId,
            message = message,
            dateSent = dateSent,
            type = type,
            photoUrl = photoUrl,
            thumbUrl = thumbUrl,
            listingId = listingId,
            listing = listing?.toJsonString(ListingPO::class.java) ?: "",
            isFromOtherUser = isFromOtherUser,
            otherUser = otherUser,
            otherUserName = otherUserName,
            otherUserNumber = otherUserNumber,
            sourceUserId = sourceUserId,
            sourceUserMobileNumber = sourceUserMobileNumber,
            sourceUserName = sourceUserName
        )
    }

    fun getImageCaption(): String? {
        return if (message.isNotEmpty() && message != DEFAULT_IMAGE_CAPTION) {
            message
        } else {
            null
        }
    }

    companion object {
        private const val DEFAULT_IMAGE_CAPTION = "Uploaded an image!"
    }
}