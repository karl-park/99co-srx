package sg.searchhouse.agentconnect.model.api

import com.google.gson.annotations.SerializedName

data class DefaultResultResponse(
    @SerializedName("result")
    val result: String = ""
)