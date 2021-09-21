package sg.searchhouse.agentconnect.model.api.listing.map

import com.google.gson.annotations.SerializedName

data class MapFeaturePO(
    @SerializedName("type")
    val type: String,
    @SerializedName("properties")
    val properties: MapFeaturePropertyPO,
    @SerializedName("geometry")
    val geometry: MapGeometryPolygonPO
)