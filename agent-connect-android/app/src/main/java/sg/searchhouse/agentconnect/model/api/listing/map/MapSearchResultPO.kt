package sg.searchhouse.agentconnect.model.api.listing.map

import com.google.gson.annotations.SerializedName

data class MapSearchResultPO(
    @SerializedName("mapData")
    val mapData: MapFeatureCollectionPO,
    @SerializedName("mapFocusData")
    val mapFocusData: MapFeatureCollectionPO,
    @SerializedName("numListings")
    val numListings: Int,
    @SerializedName("numMarkers")
    val numMarkers: Int,
    @SerializedName("isDetailedResult")
    val isDetailedResult: Boolean,
    @SerializedName("center")
    val center: MapGeometryPointPO,
    @SerializedName("radius")
    val radius: Double,
    @SerializedName("mapZoomLevel")
    val mapZoomLevel: Int,
    @SerializedName("listingIds")
    val listingIds: List<Int>
)