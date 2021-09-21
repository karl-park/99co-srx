package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetAddressPropertyTypeResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("addressPropertyType")
    val addressPropertyType: AddressPropertyTypePO
): Serializable