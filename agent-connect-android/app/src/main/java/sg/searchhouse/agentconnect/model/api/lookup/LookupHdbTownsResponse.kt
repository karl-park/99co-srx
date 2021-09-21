package sg.searchhouse.agentconnect.model.api.lookup

import com.google.gson.annotations.SerializedName

data class LookupHdbTownsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("hdbTowns")
    val hdbTowns: List<HdbTown>
) {
    class HdbTown(
        @SerializedName("amenityId")
        val amenityId: Int,
        @SerializedName("townName")
        val townName: String
    )
}