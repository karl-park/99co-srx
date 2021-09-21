package sg.searchhouse.agentconnect.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "conversation_user_join",
    primaryKeys = ["conversation_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = SsmConversationEntity::class,
            parentColumns = arrayOf("conversation_id"),
            childColumns = arrayOf("conversation_id")
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("user_id"),
            childColumns = arrayOf("user_id")
        )]
)
class ConversationUserJoinEntity(
    @ColumnInfo(name = "conversation_id")
    val conversationId: String,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "type")
    val type: String
)