package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PortalAccountPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("portalType")
    val portalType: Int,
    @SerializedName("portalId")
    val portalId: String?,
    @SerializedName("syncOption")
    val syncOption: Int?,
    @SerializedName("syncFrequency")
    val syncFrequency: Int?
) : Serializable