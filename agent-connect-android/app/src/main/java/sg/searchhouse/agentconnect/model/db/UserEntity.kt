package sg.searchhouse.agentconnect.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id", index = true)
    val id: Int,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "mobile_country_code")
    val mobileCountryCode: Int,
    @ColumnInfo(name = "mobile_local_num")
    val mobileLocalNum: Int,
    @ColumnInfo(name = "photo")
    val photo: String,
    @ColumnInfo(name = "company_name")
    val companyName: String
)