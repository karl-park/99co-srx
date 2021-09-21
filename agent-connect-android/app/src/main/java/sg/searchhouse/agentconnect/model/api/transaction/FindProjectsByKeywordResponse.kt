package sg.searchhouse.agentconnect.model.api.transaction

import com.google.gson.annotations.SerializedName

data class FindProjectsByKeywordResponse(
    @SerializedName("projects")
    val projects: List<TransactionSearchResultPO>
)