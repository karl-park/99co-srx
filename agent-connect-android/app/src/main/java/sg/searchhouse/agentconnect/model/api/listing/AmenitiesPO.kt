package sg.searchhouse.agentconnect.model.api.listing

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class AmenitiesPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cdAmenityId")
    val cdAmenityId: Int,
    @SerializedName("cdAmenitiesDescription")
    val cdAmenitiesDescription: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortName")
    val shortName: String,
    @SerializedName("distance")
    val distance: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
) {
    fun getLatLng(): LatLng? {
        val latitudeDouble = latitude.toDoubleOrNull() ?: return null
        val longitudeDouble = longitude.toDoubleOrNull() ?: return null
        return LatLng(latitudeDouble, longitudeDouble)
    }
}