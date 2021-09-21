package sg.searchhouse.agentconnect.viewmodel.fragment.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.data.repository.FindAgentRepository
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.agentdirectory.FindAgentsResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class FindAgentViewModel constructor(application: Application) :
    ApiBaseViewModel<FindAgentsResponse>(application) {

    @Inject
    lateinit var findAgentRepository: FindAgentRepository

    @Inject
    lateinit var agentRepository: AgentRepository

    @Inject
    lateinit var applicationContext: Context

    var page: Int = 0
    var searchText: String = ""
    var selectedDistrictIds: String = ""
    var selectedHdbTownIds: String = ""
    var selectedAreaSpecializations: String = ""
    var agents = arrayListOf<Any>()

    val total = MutableLiveData<Int>()
    val agentPO = MutableLiveData<AgentPO>()

    init {
        viewModelComponent.inject(this)
        mainStatus.value = ApiStatus.StatusKey.SUCCESS
    }

    fun loadAgents() {
        page = 1
        performRequest(
            findAgentRepository.findAgentsForAgentDirectory(
                page,
                searchText = searchText,
                selectedDistrictIds = selectedDistrictIds,
                selectedHdbTownIds = selectedHdbTownIds,
                selectedAreaSpecializations = selectedAreaSpecializations
            )
        ) //done calling perform request
    }

    fun loadMoreFindAgent() {
        //do next request for load more
        performNextRequest(
            findAgentRepository.findAgentsForAgentDirectory(
                page,
                searchText = searchText,
                selectedDistrictIds = selectedDistrictIds,
                selectedHdbTownIds = selectedHdbTownIds,
                selectedAreaSpecializations = selectedAreaSpecializations
            )
        )
    }

    fun getAgentCv(agentId: Int) {
        ApiUtil.performRequest(
            applicationContext,
            agentRepository.getAgentCv(agentId),
            onSuccess = { agentPO.postValue(it.data) },
            onFail = { Log.e("FindAgent", "error in calling getAgentCv") },
            onError = { Log.e("FindAgent", "error in calling getAgentCv") }
        )
    }

    fun canLoadNext(): Boolean {
        val size = agents.filterIsInstance<AgentPO>().size
        val total = total.value ?: return false
        return size < total
    }

    override fun shouldResponseBeOccupied(response: FindAgentsResponse): Boolean {
        return agents.isNotEmpty()
    }
}
