package sg.searchhouse.agentconnect.model.api.lookup

import com.google.gson.annotations.SerializedName

data class LookupBedroomsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("bedrooms")
    val bedrooms: ArrayList<Bedroom>
) {
    class Bedroom(
        @SerializedName("id")
        val id: Int,
        @SerializedName("description")
        val description: String
    )
}