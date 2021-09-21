package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PropertyPO(
    @SerializedName("buildingNum")
    val buildingNum: String,
    @SerializedName("streetName")
    val streetName: String,
    @SerializedName("buildingName")
    val buildingName: String,
    @SerializedName("buildingType")
    val buildingType: String,
    @SerializedName("postalCode")
    val postalCode: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("address")
    val address: String
) : Serializable