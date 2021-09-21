package sg.searchhouse.agentconnect.model.api.listing.map

import com.google.gson.annotations.SerializedName

data class MapFeatureCollectionPO(
    @SerializedName("type")
    val type: String,
    @SerializedName("features")
    val features: MapFeaturePO
)