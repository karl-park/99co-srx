package sg.searchhouse.agentconnect.viewmodel.fragment.main.menu

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.data.repository.FindAgentRepository
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.agentdirectory.FindAgentsResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class LoginAsAgentViewModel constructor(application: Application) :
    ApiBaseViewModel<FindAgentsResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var repository: FindAgentRepository

    @Inject
    lateinit var authRepository: AuthRepository

    val agents = arrayListOf<AgentPO>()
    val loginAsAgentResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()


    init {
        viewModelComponent.inject(this)
        initializeValue()
    }

    private fun initializeValue() {
        mainStatus.value = ApiStatus.StatusKey.SUCCESS
    }

    fun findAgentsBySearchKeyword(searchText: String) {
        performRequest(repository.findAgentsForLoginAsAgent(searchText))
    }

    fun loginAsAgent(agentPO: AgentPO) {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.loginAsAgent(agentPO),
            onSuccess = {
                loginAsAgentResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                loginAsAgentResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                loginAsAgentResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    override fun shouldResponseBeOccupied(response: FindAgentsResponse): Boolean {
        return response.agents.isNotEmpty()
    }

}