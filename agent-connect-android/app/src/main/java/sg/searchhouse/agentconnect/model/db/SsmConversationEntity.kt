package sg.searchhouse.agentconnect.model.db

import androidx.room.*
import sg.searchhouse.agentconnect.model.api.chat.SrxAgentEnquiryPO
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO

@Entity(tableName = "ssm_conversations")
data class SsmConversationEntity(
    @PrimaryKey
    @ColumnInfo(name = "conversation_id", index = true)
    val conversationId: String,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "date_sent")
    val dateSent: String,
    @ColumnInfo(name = "other_user")
    val otherUser: String,
    @ColumnInfo(name = "other_username")
    val otherUserName: String,
    @ColumnInfo(name = "other_user_number")
    val otherUserNumber: String,
    @ColumnInfo(name = "other_user_photo")
    val otherUserPhoto: String,
    @ColumnInfo(name = "other_user_agent_ind")
    val otherUserAgentInd: Boolean,
    @ColumnInfo(name = "unread_ind")
    val unreadInd: Boolean,
    @ColumnInfo(name = "unread_count")
    val unreadCount: Int,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "one_to_one_type")
    val oneToOneType: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String,
    @ColumnInfo(name = "members")
    val members: List<UserPO>? = null,
    @Embedded(prefix = "enquiry_")
    val enquiry: SrxAgentEnquiryPO?
) {
    fun fromEntityToSsmConversation(): SsmConversationPO {
        var memberLists = listOf<UserPO>()
        members?.let { memberLists = it }
        return SsmConversationPO(
            conversationId = conversationId,
            message = message,
            dateSent = dateSent,
            otherUser = otherUser,
            otherUserName = otherUserName,
            otherUserNumber = otherUserNumber,
            otherUserPhoto = otherUserPhoto,
            otherUserAgentInd = otherUserAgentInd,
            unreadInd = unreadInd,
            unreadCount = unreadCount,
            type = type,
            oneToOneType = oneToOneType,
            title = title,
            photoUrl = photoUrl,
            members = memberLists,
            enquiry = enquiry
        )
    }
}