package sg.searchhouse.agentconnect.model.api.lookup

import com.google.gson.annotations.SerializedName

data class LookupListingFeaturesFixturesAreasResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("features")
    val features: ArrayList<Data>,
    @SerializedName("fixtures")
    val fixtures: ArrayList<Data>,
    @SerializedName("areas")
    val areas: ArrayList<Data>

) {
    class Data(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("value")
        val value: String
    )
}