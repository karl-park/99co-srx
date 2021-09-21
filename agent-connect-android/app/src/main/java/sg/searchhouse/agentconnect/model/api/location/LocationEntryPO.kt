package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocationEntryPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("displayText")
    val displayText: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("propertyType")
    val propertyType: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
): Serializable