package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

data class TransactionTableListRequest(
    @SerializedName("params")
    val params: TransactionSearchCriteriaV2VO
)
