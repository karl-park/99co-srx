package sg.searchhouse.agentconnect.model.api.agent

import com.google.gson.annotations.SerializedName

data class InstallmentPaymentOptionPO(
    @SerializedName("bankId")
    val bankId: Int,
    @SerializedName("logos")
    val logos: List<String>,
    @SerializedName("monthsAvailable")
    val monthsAvailable: List<Int>,
    @SerializedName("showMonthOptions")
    val showMonthOptions: Boolean
)