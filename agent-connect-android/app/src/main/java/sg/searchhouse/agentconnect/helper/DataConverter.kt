package sg.searchhouse.agentconnect.helper

import androidx.room.TypeConverter
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

class DataConverter {

    @TypeConverter
    fun fromMemberList(members: List<UserPO>?): String {
        members?.let {
            return Gson().toJson(members, object : TypeToken<List<UserPO>>() {}.type)
        }
        return ""
    }

    @TypeConverter
    fun toMemberList(membersString: String?): List<UserPO>? {
        membersString?.let {
            return Gson().fromJson(membersString, object : TypeToken<List<UserPO>>() {}.type)
        }
        return emptyList()
    }
}