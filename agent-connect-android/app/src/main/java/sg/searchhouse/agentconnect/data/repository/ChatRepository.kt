package sg.searchhouse.agentconnect.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.chat.*
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API:             https://streetsine.atlassian.net/wiki/spaces/SIN/pages/685211649/SSM+V1+API
 * Data structure:  https://streetsine.atlassian.net/wiki/spaces/SIN/pages/685342724/SSM+V1+Data+Structures
 */
@Singleton
class ChatRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    fun deleteEnquiries(srxAgentEnquiryPOS: ArrayList<SrxAgentEnquiryPO>): Call<DefaultResultResponse> {
        return srxDataSource.deleteEnquiries(srxAgentEnquiryPOS)
    }

    fun resetUnreadCount(ssmConversationPO: SsmConversationPO): Call<DefaultResultResponse> {
        return srxDataSource.resetUnreadCount(ssmConversationPO)
    }

    fun findAllSsmConversations(
        startIndex: Int,
        maxResults: Int
    ): Call<LoadSsmConversationsResponse> {
        return srxDataSource.findSsmConversations(
            ChatEnum.ChatTabGroup.ALL.value,
            startIndex,
            maxResults,
            oneToOneOtherUserType = null,
            dateLastLoad = null,
            isUnreadOnly = null
        )
    }

    fun findPublicSsmConversations(
        startIndex: Int,
        maxResults: Int
    ): Call<LoadSsmConversationsResponse> {
        return srxDataSource.findSsmConversations(
            ChatEnum.ChatTabGroup.PUBLIC.value,
            startIndex,
            maxResults,
            oneToOneOtherUserType = ChatEnum.UserType.PUBLIC.value,
            dateLastLoad = null,
            isUnreadOnly = null
        )
    }

    fun findSRXSsmConversations(
        startIndex: Int,
        maxResults: Int
    ): Call<LoadSsmConversationsResponse> {
        return srxDataSource.findSsmConversations(
            ChatEnum.ChatTabGroup.SRX.value,
            startIndex,
            maxResults,
            oneToOneOtherUserType = null,
            dateLastLoad = null,
            isUnreadOnly = null
        )
    }

    fun findAgentSsmConversations(
        startIndex: Int,
        maxResults: Int
    ): Call<LoadSsmConversationsResponse> {
        return srxDataSource.findSsmConversations(
            ChatEnum.ChatTabGroup.AGENT.value,
            startIndex,
            maxResults,
            oneToOneOtherUserType = ChatEnum.UserType.AGENT.value,
            dateLastLoad = null,
            isUnreadOnly = null
        )
    }

    fun findUnreadAllSsmConversations(): Call<LoadSsmConversationsResponse> {
        return srxDataSource.findSsmConversations(
            ChatEnum.ChatTabGroup.ALL.value,
            startIndex = 0,
            maxResults = 10,
            isUnreadOnly = true,
            oneToOneOtherUserType = null,
            dateLastLoad = null
        )
    }

    //leave ssm conversation
    fun leaveSsmConversations(ssmConversationPOS: ArrayList<SsmConversationPO>): Call<DefaultResultResponse> {
        return srxDataSource.leaveSsmConversations(ssmConversationPOS)
    }

    //create ssm for group chat or one to one chat
    fun createSsmConversation(createSsmRequest: CreateSsmConversationRequest): Call<CreateSsmConversationResponse> {
        return srxDataSource.createSsmConversation(createSsmRequest)
    }

    //Find ssm messages by conversationId
    fun findSsmMessages(
        conversationId: String,
        startIndex: Int,
        maxResults: Int
    ): Call<FindSsmMessagesResponse> {
        var id = 0
        if (NumberUtil.isNaturalNumber(conversationId)) {
            id = conversationId.toInt()
        }
        return srxDataSource.findSsmMessages(
            conversationId = id,
            startIndex = startIndex,
            maxResults = maxResults,
            messageId = null,
            isBefore = null,
            isAscending = null
        )
    }

    fun findSsmMessagesByMessageId(
        conversationId: String,
        startIndex: Int,
        maxResults: Int,
        messageId: String
    ): Call<FindSsmMessagesResponse> {
        var convoId = 0
        if (NumberUtil.isNaturalNumber(conversationId)) {
            convoId = conversationId.toInt()
        }
        var msgId = 0
        if (NumberUtil.isNaturalNumber(messageId)) {
            msgId = messageId.toInt()
        }
        return srxDataSource.findSsmMessages(
            conversationId = convoId,
            startIndex = startIndex,
            maxResults = maxResults,
            messageId = msgId,
            isBefore = true,
            isAscending = null
        )
    }

    fun sendTextMessage(conversationId: String, message: String): Call<SendSsmMessageResponse> {
        val conversationIdParam =
            conversationId.toRequestBody("text/plain".toMediaTypeOrNull())
        val messageTypeParam =
            ChatEnum.MessageType.MESSAGE.value.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        val messageParam = message.toRequestBody("text/plain".toMediaTypeOrNull())

        return srxDataSource.sendSsmMessage(
            conversationIdParam,
            messageTypeParam,
            message = messageParam,
            listingId = null,
            file = null
        )
    }

    fun sendEnquireListingMessage(
        conversationId: String,
        listingType: String,
        listingId: Int,
        message: String
    ): Call<SendSsmMessageResponse> {
        val conversationIdParam =
            conversationId.toRequestBody("text/plain".toMediaTypeOrNull())
        val messageTypeParam = when (listingType) {
            ListingEnum.ListingType.SRX_LISTING.value -> ChatEnum.MessageType.LISTING
            ListingEnum.ListingType.PUBLIC_LISTING.value -> ChatEnum.MessageType.PUBLIC_LISTING
            else -> throw IllegalArgumentException("Invalid listing type!")
        }.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val listingIdParam = listingId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val messageParam = message.toRequestBody("text/plain".toMediaTypeOrNull())

        return srxDataSource.sendSsmMessage(
            conversationIdParam,
            messageTypeParam,
            message = messageParam,
            listingId = listingIdParam,
            file = null
        )
    }

    fun sendImage(
        conversationId: String,
        file: File,
        message: String? = null
    ): Call<SendSsmMessageResponse> {
        val conversationIdParam =
            conversationId.toRequestBody("text/plain".toMediaTypeOrNull())
        val messageTypeParam =
            ChatEnum.MessageType.PHOTO.value.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        val fileParam = MultipartBody.Part.createFormData(
            "File", file.name, file
                .asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )
        val messageParam = message?.toRequestBody("text/plain".toMediaTypeOrNull())

        return srxDataSource.sendSsmMessage(
            conversationIdParam,
            messageTypeParam,
            message = messageParam,
            listingId = null,
            file = fileParam
        )
    }


    fun getSsmConversationInfo(conversationId: Int): Call<GetSsmConversationInfoResponse> {
        return srxDataSource.getSsmConversationInfo(conversationId)
    }

    fun removeMembersFromConversation(
        conversationId: String,
        members: ArrayList<UserPO>
    ): Call<DefaultResultResponse> {
        return srxDataSource.removeMembersFromConversation(
            RemoveMembersFromConversationRequest(
                conversationId,
                members
            )
        )
    }

    fun updateSsmConversationSettings(ssmConversationSettingPO: SsmConversationSettingPO):
            Call<DefaultResultResponse> {
        return srxDataSource.updateSsmConversationSettings(ssmConversationSettingPO)
    }

    fun addMembersToConversation(
        conversationId: String,
        members: ArrayList<UserPO>
    ): Call<DefaultResultResponse> {
        return srxDataSource.addMembersToConversation(
            AddMemberToConversatioinRequest(
                conversationId,
                members
            )
        )
    }

    fun blacklistLeaveSsmConversations(
        conversations: List<SsmConversationPO>
    ): Call<DefaultResultResponse> {
        return srxDataSource.blacklistLeaveSsmConversations(conversations)
    }

    fun getSsmConversation(conversationId: Int): Call<GetSsmConversationResponse> {
        return srxDataSource.getSsmConversation(conversationId)
    }

    fun createSsmListingsBlastConversation(
        title: String?,
        message: String?,
        srxstp: List<String>?,
        tableLP: List<String>?
    ): Call<DefaultResultResponse> {
        return srxDataSource.createSsmListingsBlastConversation(
            CreateSsmListingsBlastConversationRequest(
                title = title,
                message = message,
                srxstp = srxstp,
                tableLP = tableLP
            )
        )
    }
}