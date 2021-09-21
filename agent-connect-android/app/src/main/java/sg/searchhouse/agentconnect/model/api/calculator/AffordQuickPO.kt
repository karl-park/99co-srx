package sg.searchhouse.agentconnect.model.api.calculator

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AffordQuickPO(
    @SerializedName("maxLoan")
    val maxLoan: Long,
    @SerializedName("maxPurchasePrice")
    val maxPurchasePrice: Long,
    @SerializedName("incomeRequired")
    val incomeRequired: Long
) : Serializable