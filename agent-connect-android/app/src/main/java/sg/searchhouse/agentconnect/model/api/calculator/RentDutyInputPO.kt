package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RentDutyInputPO(
    @SerializedName("monthlyRent")
    val monthlyRent: Long,
    @SerializedName("otherCharges")
    val otherCharges: Long,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String
) : Serializable