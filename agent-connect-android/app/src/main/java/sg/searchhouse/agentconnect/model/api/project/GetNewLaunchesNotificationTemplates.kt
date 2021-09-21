package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName

data class GetNewLaunchesNotificationTemplates(
    @SerializedName("newLaunchesNotificationTemplate")
    val newLaunchesNotificationTemplate: String
)