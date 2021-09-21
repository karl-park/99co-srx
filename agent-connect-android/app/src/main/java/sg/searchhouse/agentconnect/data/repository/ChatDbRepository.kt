package sg.searchhouse.agentconnect.data.repository

import androidx.annotation.WorkerThread
import io.reactivex.Single
import sg.searchhouse.agentconnect.data.datasource.LocalDataSource
import sg.searchhouse.agentconnect.dsl.performBlockQueryLenient
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.db.ConversationUserJoinEntity
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.model.db.SsmMessageEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatDbRepository @Inject constructor(private val localDataSource: LocalDataSource) {
    /**
     * Insert conversation with members and their join table
     */
    fun insertSsmConversation(conversation: SsmConversationPO): List<Single<Long>> {
        val insertConversation =
            localDataSource.insertSsmConversation(conversation.fromSsmConversationToEntity())
        val joinUsers = conversation.members.map { user ->
            val insertUser = localDataSource.insertUser(user.fromUserPOToEntity())
            val insertJoin = localDataSource.insertConversationUserJoin(
                ConversationUserJoinEntity(
                    conversationId = conversation.conversationId,
                    userId = user.id,
                    type = conversation.type
                )
            )
            listOf(insertUser, insertJoin)
        }.flatten()
        return listOf(insertConversation) + joinUsers
    }

    /**
     * Insert multiple conversations with members and their join table
     */
    fun insertSsmConversations(conversations: List<SsmConversationPO>): List<Single<Long>> {
        return conversations.map { conversation ->
            insertSsmConversation(conversation)
        }.flatten()
    }

    @WorkerThread
    fun insertOrUpdateSsmMessage(message: SsmMessageEntity) {
        val rowId = localDataSource.insertSsmMessage(message).performBlockQueryLenient()
        if (rowId == -1L) {
            localDataSource.updateSsmMessage(message).performBlockQueryLenient()
        }
        println("`insertOrUpdateSsmMessage` row id = $rowId")
    }

    fun getOneConversation(): Single<SsmConversationEntity> {
        return localDataSource.getOneConversation()
    }

    //ALL TAB
    fun getAllConversations(
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmConversationEntity>> {
        return localDataSource.getAllConversations(
            types = ChatEnum.ChatTabGroup.ALL.value.split(","),
            startIndex = startIndex,
            maxResults = maxResults
        )
    }

    //PUBLIC TAB
    fun getPublicConversations(
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmConversationEntity>> {
        return localDataSource.getPublicConversations(
            startIndex = startIndex,
            maxResults = maxResults
        )
    }

    //SRX TAB
    fun getSRXConversations(
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmConversationEntity>> {
        return localDataSource.getSRXConversations(
            startIndex = startIndex,
            maxResults = maxResults
        )
    }

    //AGENT TAB
    fun getAgentConversations(
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmConversationEntity>> {
        return localDataSource.getAgentConversations(
            types = listOf(
                ChatEnum.ConversationType.BLAST.value,
                ChatEnum.ConversationType.GROUP_CHAT.value,
                ChatEnum.ConversationType.BLAST_NEW_TYPE.value
            ),
            startIndex = startIndex,
            maxResults = maxResults
        )
    }

    fun findMessagesByConversationId(
        conversationId: String,
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmMessageEntity>> {
        return localDataSource.findMessagesByConversationId(conversationId, startIndex, maxResults)
    }

    fun searchMessagesByKeyword(keyword: String): Single<List<SsmMessageEntity>> {
        return localDataSource.searchMessagesByKeyword(keyword)
    }

    fun searchConversationsByKeyword(keyword: String): Single<List<SsmConversationEntity>> {
        return localDataSource.searchConversationsByKeyword(keyword)
    }

    fun deleteConversation(conversationId: String): List<Single<Int>> {
        return listOf(
            localDataSource.removeConversationFromJoinById(conversationId),
            localDataSource.removeAllMessagesByConversationId(conversationId),
            localDataSource.removeConversationById(conversationId)
        )
    }

    fun deleteAllTables(): List<Single<Int>> {
        return listOf(
            localDataSource.deleteAllConversationAndUsersJoin(),
            localDataSource.deleteAllUsers(),
            localDataSource.deleteAllMessages(),
            localDataSource.deleteAllConversations()
        )
    }

    // conversation id by user id
    fun findConversationIdByUserId(userId: Int): Single<String> {
        return localDataSource.findOneToOneConversationIdByUserId(id = userId)
    }

    // TODO Implement as `SELECT 1` to save resource
    // conversation by conversation id
    fun findSsmConversationById(conversationId: String): Single<SsmConversationEntity> {
        return localDataSource.findSsmConversationById(conversationId = conversationId)
    }

    fun getMessageCountByConversationId(conversationId: String): Single<Int> {
        return localDataSource.getMessageCountByConversationId(conversationId = conversationId)
    }
}