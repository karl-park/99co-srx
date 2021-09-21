package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

data class ParamInfoResponse(
    @SerializedName("paramStructure")
    val paramStructure: TransactionSearchCriteriaV2VO
)
