package sg.searchhouse.agentconnect.model.api.userprofile

import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("profile")
    val profile: UserPO
)