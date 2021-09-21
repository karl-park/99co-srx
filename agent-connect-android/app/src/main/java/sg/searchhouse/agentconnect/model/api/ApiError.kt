package sg.searchhouse.agentconnect.model.api

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("errorCode")
    val errorCode: String = "",
    @SerializedName("error")
    val error: String = ""
)