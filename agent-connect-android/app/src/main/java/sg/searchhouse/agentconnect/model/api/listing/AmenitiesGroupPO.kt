package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class AmenitiesGroupPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("amenities")
    val amenities: List<AmenitiesPO>
)