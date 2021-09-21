package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchWithWalkupResponse(
    @SerializedName("data")
    val `data`: List<Data>
) : Serializable {
    data class Data(
        @SerializedName("address")
        val address: String,
        @SerializedName("buildingName")
        val buildingName: String,
        @SerializedName("buildingNum")
        val buildingNum: String,
        @SerializedName("buildingType")
        val buildingType: String,
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double,
        @SerializedName("postalCode")
        val postalCode: Int,
        @SerializedName("streetName")
        val streetName: String
    ) : Serializable
}