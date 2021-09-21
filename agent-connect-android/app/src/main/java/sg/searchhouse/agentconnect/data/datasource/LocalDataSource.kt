package sg.searchhouse.agentconnect.data.datasource

import androidx.room.*
import io.reactivex.Single
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.db.ConversationUserJoinEntity
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.model.db.SsmMessageEntity
import sg.searchhouse.agentconnect.model.db.UserEntity

/**
 * NOTE:
 * 1. Must return integer instead of null for update/delete to prevent `RuntimeException`: The callable return null value
 *
 * Ref: https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
interface LocalDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSsmConversation(conversation: SsmConversationEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversationUserJoin(join: ConversationUserJoinEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSsmMessage(message: SsmMessageEntity): Single<Long>

    @Update
    fun updateSsmMessage(message: SsmMessageEntity): Single<Int>

    @Query("SELECT * FROM ssm_conversations LIMIT 1")
    fun getOneConversation(): Single<SsmConversationEntity>

    @Query("SELECT * FROM ssm_conversations WHERE type IN(:types) ORDER BY date_sent DESC LIMIT (:maxResults) OFFSET (:startIndex)")
    fun getAllConversations(
        types: List<String>,
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmConversationEntity>>

    @Query("SELECT * FROM ssm_conversations WHERE type=:enquiryType OR (type=:oneToOneType AND one_to_one_type=:userType) ORDER BY date_sent DESC LIMIT (:maxResults) OFFSET (:startIndex)")
    fun getPublicConversations(
        startIndex: Int,
        maxResults: Int,
        enquiryType: String = ChatEnum.ConversationType.AGENT_ENQUIRY.value,
        oneToOneType: String = ChatEnum.ConversationType.ONE_TO_ONE.value,
        userType: String = ChatEnum.UserType.PUBLIC.value
    ): Single<List<SsmConversationEntity>>

    @Query("SELECT * FROM ssm_conversations WHERE type=:type ORDER BY date_sent DESC LIMIT (:maxResults) OFFSET (:startIndex)")
    fun getSRXConversations(
        startIndex: Int,
        maxResults: Int,
        type: String = ChatEnum.ConversationType.SRX_ANNOUNCEMENTS.value
    ): Single<List<SsmConversationEntity>>

    @Query("SELECT * FROM ssm_conversations WHERE type IN(:types) OR (type=:oneToOneType AND one_to_one_type=:userType) ORDER BY date_sent DESC LIMIT (:maxResults) OFFSET (:startIndex)")
    fun getAgentConversations(
        types: List<String>,
        startIndex: Int,
        maxResults: Int,
        oneToOneType: String = ChatEnum.ConversationType.ONE_TO_ONE.value,
        userType: String = ChatEnum.UserType.AGENT.value
    ): Single<List<SsmConversationEntity>>

    @Query("SELECT * FROM ssm_messages WHERE conversation_id=:conversationId ORDER BY date_sent DESC LIMIT (:maxResults) OFFSET (:startIndex)")
    fun findMessagesByConversationId(
        conversationId: String,
        startIndex: Int,
        maxResults: Int
    ): Single<List<SsmMessageEntity>>

    @Query("SELECT * FROM ssm_messages WHERE message LIKE  '%' || :keyword || '%'")
    fun searchMessagesByKeyword(keyword: String): Single<List<SsmMessageEntity>>

    @Query("SELECT * FROM ssm_conversations WHERE  message LIKE  '%' || :keyword || '%' OR  other_username LIKE  '%' || :keyword || '%' OR title LIKE  '%' || :keyword || '%'")
    fun searchConversationsByKeyword(keyword: String): Single<List<SsmConversationEntity>>

    @Query("DELETE FROM ssm_messages WHERE conversation_id =:conversationId")
    fun removeAllMessagesByConversationId(conversationId: String): Single<Int>

    @Query("DELETE FROM ssm_conversations WHERE conversation_id =:conversationId")
    fun removeConversationById(conversationId: String): Single<Int>

    @Query("DELETE FROM conversation_user_join WHERE conversation_id=:conversationId")
    fun removeConversationFromJoinById(conversationId: String): Single<Int>

    @Query("DELETE FROM ssm_conversations")
    fun deleteAllConversations(): Single<Int>

    @Query("DELETE FROM ssm_messages")
    fun deleteAllMessages(): Single<Int>

    @Query("DELETE FROM user")
    fun deleteAllUsers(): Single<Int>

    @Query("DELETE FROM conversation_user_join")
    fun deleteAllConversationAndUsersJoin(): Single<Int>

    @Query("SELECT conversation_id FROM conversation_user_join WHERE user_id = :id AND type = :type")
    fun findOneToOneConversationIdByUserId(
        id: Int,
        type: String = ChatEnum.ConversationType.ONE_TO_ONE.value
    ): Single<String>

    @Query("SELECT * FROM ssm_conversations WHERE conversation_id=:conversationId")
    fun findSsmConversationById(conversationId: String): Single<SsmConversationEntity>

    @Query("SELECT COUNT(*) FROM ssm_messages WHERE conversation_id=:conversationId")
    fun getMessageCountByConversationId(conversationId: String): Single<Int>
}