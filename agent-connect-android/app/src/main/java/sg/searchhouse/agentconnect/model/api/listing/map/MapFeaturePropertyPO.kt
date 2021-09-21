package sg.searchhouse.agentconnect.model.api.listing.map

import com.google.gson.annotations.SerializedName

data class MapFeaturePropertyPO (
    @SerializedName("name")
    val name: String,
    @SerializedName("cadastralId")
    val cadastralId: Int,
    @SerializedName("districtId")
    val districtId: String,
    @SerializedName("hdbtownId")
    val hdbtownId: String,
    @SerializedName("center")
    val center: MapGeometryPointPO
)
