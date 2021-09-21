package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName

data class GetOwnerCertificationNotificationTemplateResponse(
    @SerializedName("template")
    val template: String,
    @SerializedName("result")
    val result: String
)