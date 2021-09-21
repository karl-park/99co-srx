package sg.searchhouse.agentconnect.model.api.userprofile

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.db.UserEntity
import java.io.Serializable

data class UserPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobileCountryCode")
    val mobileCountryCode: Int,
    @SerializedName("mobileLocalNum")
    val mobileLocalNum: Int,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("debugAllowed")
    val debugAllowed: Boolean
) : Serializable {

    fun fromUserPOToEntity(): UserEntity {
        return UserEntity(
            id = id,
            email = email,
            name = name,
            mobileCountryCode = mobileCountryCode,
            mobileLocalNum = mobileLocalNum,
            photo = photo,
            companyName = companyName
        )
    }
}