package sg.searchhouse.agentconnect.model.api.listing.map

import com.google.gson.annotations.SerializedName

data class FindMapRegionViewResponse(
    @SerializedName("result")
    val result: MapFeatureCollectionPO
)