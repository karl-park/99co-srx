package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agentdirectory.FindAgentsResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindAgentRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    fun findAgentsForChat(page: Int, searchText: String): Call<FindAgentsResponse> {
        return srxDataSource.findAgents(
            page,
            ApiConstant.MAX_RESULTS_AGENT_DIRECTORY,
            searchText = searchText,
            selectedDistrictIds = null,
            selectedHdbTownIds = null,
            selectedAreaSpecializations = null
        )
    }

    fun findAgentsForAgentDirectory(
        page: Int,
        searchText: String,
        selectedDistrictIds: String,
        selectedHdbTownIds: String,
        selectedAreaSpecializations: String
    ): Call<FindAgentsResponse> {
        return srxDataSource.findAgents(
            page,
            ApiConstant.MAX_RESULTS_AGENT_DIRECTORY,
            searchText,
            selectedDistrictIds,
            selectedHdbTownIds,
            selectedAreaSpecializations
        )
    }

    fun findAgentsForLoginAsAgent(searchText: String): Call<FindAgentsResponse> {
        return srxDataSource.findAgents(
            page = 1,
            maxResults = AppConstant.BATCH_SIZE_AGENTS,
            searchText = searchText,
            selectedDistrictIds = null,
            selectedHdbTownIds = null,
            selectedAreaSpecializations = null
        )
    }

    fun blackListAgents(agentMobileNumbers: ArrayList<String>): Call<DefaultResultResponse> {
        return srxDataSource.blacklistAgents(agentMobileNumbers)
    }

}