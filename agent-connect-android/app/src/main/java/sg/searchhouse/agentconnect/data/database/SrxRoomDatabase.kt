package sg.searchhouse.agentconnect.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sg.searchhouse.agentconnect.data.datasource.LocalDataSource
import sg.searchhouse.agentconnect.helper.DataConverter
import sg.searchhouse.agentconnect.model.db.ConversationUserJoinEntity
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.model.db.SsmMessageEntity
import sg.searchhouse.agentconnect.model.db.UserEntity

@Database(
    entities = [
        SsmConversationEntity::class,
        SsmMessageEntity::class,
        UserEntity::class,
        ConversationUserJoinEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class SrxRoomDatabase : RoomDatabase() {

    abstract fun localDataSource(): LocalDataSource

    companion object {

        @Volatile
        private var INSTANCE: SrxRoomDatabase? = null

        fun getDatabase(context: Context): SrxRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SrxRoomDatabase::class.java,
                    "srx_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}