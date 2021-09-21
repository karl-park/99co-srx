package sg.searchhouse.agentconnect.viewmodel.activity.agent.client

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.AgentClientRepository
import sg.searchhouse.agentconnect.model.api.agentclient.GetAgentClientsInviteMessageResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class InviteAgentClientsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetAgentClientsInviteMessageResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var agentClientRepository: AgentClientRepository

    val link: LiveData<String> = Transformations.map(mainResponse) {
        it?.inviteUrl ?: ""
    }

    val inviteMessage: LiveData<String> = Transformations.map(mainResponse) {
        it?.inviteMsg ?: ""
    }

    fun performRequest() {
        performRequest(agentClientRepository.getAgentClientsInviteMessage())
    }

    init {
        viewModelComponent.inject(this)
    }

    override fun shouldResponseBeOccupied(response: GetAgentClientsInviteMessageResponse): Boolean {
        return true
    }
}