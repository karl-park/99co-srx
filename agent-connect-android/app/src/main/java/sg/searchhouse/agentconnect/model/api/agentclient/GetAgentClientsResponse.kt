package sg.searchhouse.agentconnect.model.api.agentclient

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.agent.SRXPropertyUserPO
import java.io.Serializable

// https://streetsine.atlassian.net/wiki/spaces/SIN/pages/930512913/Agent+Client+V1+API
data class GetAgentClientsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("clients")
    val clients: List<SRXPropertyUserPO>,
    @SerializedName("searchType")
    val searchType: Int
) : Serializable {
    @Throws(IllegalArgumentException::class)
    fun getSearchTypeObject(): SearchType {
        return SearchType.values().find { it.value == searchType }
            ?: throw IllegalArgumentException("Invalid `searchType` of value `$searchType`!")
    }

    enum class SearchType(val value: Int) {
        ADDRESS(1),
        CLIENT_PROFILE(2)
    }
}