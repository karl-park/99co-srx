package sg.searchhouse.agentconnect.model.api.chat

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import java.io.Serializable
import java.text.ParseException

data class SsmConversationPO constructor(
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("dateSent")
    val dateSent: String,
    @SerializedName("otherUser")
    val otherUser: String,
    @SerializedName("otherUserName")
    val otherUserName: String,
    @SerializedName("otherUserNumber")
    val otherUserNumber: String,
    @SerializedName("otherUserPhoto")
    val otherUserPhoto: String,
    @SerializedName("otherUserAgentInd")
    val otherUserAgentInd: Boolean,
    @SerializedName("unreadInd")
    val unreadInd: Boolean,
    @SerializedName("unreadCount")
    val unreadCount: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("oneToOneType")
    val oneToOneType: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("members")
    val members: List<UserPO>,
    @SerializedName("enquiry")
    val enquiry: SrxAgentEnquiryPO?,

    var isSelected: Boolean = false
) : Serializable {

    fun getSsmConversationType(): ChatEnum.ConversationType? {
        return ChatEnum.ConversationType.values().find { it.value == type }
    }

    fun getFormattedDate(context: Context): String {
        try {
            return DateTimeUtil.getFormattedDateTime(context, dateSent)
        } catch (e: ParseException) {
            ErrorUtil.handleError(context, R.string.exception_parse, e)
        } catch (e: IllegalArgumentException) {
            ErrorUtil.handleError(context, R.string.exception_illegal_argument, e)
        }
        return ""
    }

    fun fromSsmConversationToEntity(): SsmConversationEntity {
        return SsmConversationEntity(
            conversationId = getSsmConversationId(),
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
            members = members,
            enquiry = enquiry
        )
    }

    fun getSsmConversationId(): String {
        return if (!TextUtils.isEmpty(conversationId)) {
            conversationId
        } else {
            "${type}-${enquiry?.id}"
        }
    }
}