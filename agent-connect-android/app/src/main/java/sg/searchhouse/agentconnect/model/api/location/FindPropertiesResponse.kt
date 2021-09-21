package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName

data class FindPropertiesResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("properties")
    val properties: List<PropertyPO>
)