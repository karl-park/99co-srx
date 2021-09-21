package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName

data class GetPropertyTypeResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("propertyType")
    val propertyType: Int,
    @SerializedName("propertySubType")
    val propertySubType: Int,
    @SerializedName("propertySubTypeList")
    val propertySubTypeList: ArrayList<Int>
)