package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

class UserVO(
    @SerializedName("email")
    val email: String,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("userId")
    val userId: Int
)