package sg.searchhouse.agentconnect.model.api.watchlist

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import java.io.Serializable

data class GetPropertyCriteriaLatestTransactionsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("transactions")
    val transactions: TableListResponse
) : Serializable