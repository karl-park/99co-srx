package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class AmenitiesCategoryPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("amenities")
    val amenities: List<AmenitiesPO>
)