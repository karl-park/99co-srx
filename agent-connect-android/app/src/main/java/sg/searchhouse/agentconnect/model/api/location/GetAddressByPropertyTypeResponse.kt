package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName

data class GetAddressByPropertyTypeResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("addressPropertyType")
    val addressPropertyType: AddressPropertyTypePO
)