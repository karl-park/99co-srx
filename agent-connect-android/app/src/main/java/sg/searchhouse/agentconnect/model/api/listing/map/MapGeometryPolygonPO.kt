package sg.searchhouse.agentconnect.model.api.listing.map

import com.google.gson.annotations.SerializedName

data class MapGeometryPolygonPO(
    @SerializedName("type")
    val type: String,
    @SerializedName("coordinates")
    val coordinates: List<List<Double>>
)
