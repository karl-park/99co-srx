package sg.searchhouse.agentconnect.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.JsonSyntaxException
import sg.searchhouse.agentconnect.dsl.toJsonObject
import sg.searchhouse.agentconnect.model.api.chat.SsmMessagePO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

@Entity(
    tableName = "ssm_messages",
    foreignKeys = [ForeignKey(
        entity = SsmConversationEntity::class,
        parentColumns = arrayOf("conversation_id"),
        childColumns = arrayOf("conversation_id")
    )]
)
data class SsmMessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,
    @ColumnInfo(name = "conversation_id", index = true)
    val conversationId: String,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "date_sent")
    val dateSent: String,
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String,
    @ColumnInfo(name = "thumb_url")
    val thumbUrl: String,
    @ColumnInfo(name = "listing_id")
    val listingId: Int,
    @ColumnInfo(name = "listing")
    val listing: String,
    @ColumnInfo(name = "is_from_other_user")
    val isFromOtherUser: Boolean,
    @ColumnInfo(name = "other_user")
    val otherUser: String,
    @ColumnInfo(name = "other_username")
    val otherUserName: String,
    @ColumnInfo(name = "other_user_number")
    val otherUserNumber: String,
    @ColumnInfo(name = "source_user_id")
    val sourceUserId: String,
    @ColumnInfo(name = "source_user_mobile_number")
    val sourceUserMobileNumber: String,
    @ColumnInfo(name = "source_username")
    val sourceUserName: String
) {
    fun fromEntityToSsmMessage(): SsmMessagePO {
        val listingPO = getListingPO()
        return SsmMessagePO(
            messageId = messageId,
            conversationId = conversationId,
            message = message,
            dateSent = dateSent,
            type = type,
            photoUrl = photoUrl,
            thumbUrl = thumbUrl,
            listingId = listingId,
            listing = listingPO,
            isFromOtherUser = isFromOtherUser,
            otherUser = otherUser,
            otherUserName = otherUserName,
            otherUserNumber = otherUserNumber,
            sourceUserId = sourceUserId,
            sourceUserMobileNumber = sourceUserMobileNumber,
            sourceUserName = sourceUserName
        )
    }

    private fun getListingPO(): ListingPO? {
        return if (listing.isNotEmpty()) {
            try {
                listing.toJsonObject(ListingPO::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        } else {
            null
        }
    }
}