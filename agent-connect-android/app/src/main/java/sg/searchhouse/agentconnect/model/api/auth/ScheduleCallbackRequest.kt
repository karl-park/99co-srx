package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName

data class ScheduleCallbackRequest(
    @SerializedName("epochTimeSeconds")
    val epochTimeSeconds: Long,
    @SerializedName("purpose")
    val purpose: Int
)