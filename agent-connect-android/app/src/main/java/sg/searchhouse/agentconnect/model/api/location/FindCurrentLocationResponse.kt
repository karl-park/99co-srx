package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName

data class FindCurrentLocationResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("property")
    val property: PropertyPO
)