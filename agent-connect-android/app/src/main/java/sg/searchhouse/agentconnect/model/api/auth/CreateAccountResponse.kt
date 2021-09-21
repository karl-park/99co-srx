package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName

data class CreateAccountResponse(
    @SerializedName("data")
    val data: String = ""
)