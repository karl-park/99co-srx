package sg.searchhouse.agentconnect.viewmodel.fragment.main.chat

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.data.repository.FindAgentRepository
import sg.searchhouse.agentconnect.dsl.performAsyncQueryLenient
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.agentdirectory.FindAgentsResponse
import sg.searchhouse.agentconnect.model.api.chat.CreateSsmConversationRequest
import sg.searchhouse.agentconnect.model.api.chat.CreateSsmConversationResponse
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.view.fragment.main.chat.SRXAgentsDialogFragment
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SRXAgentsDialogViewModel constructor(application: Application) :
    ApiBaseViewModel<FindAgentsResponse>(application) {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var findAgentRepository: FindAgentRepository

    var page: Int = 1
    var searchText: String = ""
    var agents = arrayListOf<Any>()

    val total = MutableLiveData<Int>()
    val createSsmLiveData = MutableLiveData<ApiStatus<CreateSsmConversationResponse>>()
    val selectedAgents = MutableLiveData<ArrayList<AgentPO>>()
    val source = MutableLiveData<SRXAgentsDialogFragment.SRXAgentsSource>()
    val conversationId = MutableLiveData<String>()

    init {
        viewModelComponent.inject(this)
        initializeData()
    }

    private fun initializeData() {
        total.value = 0
        selectedAgents.value = arrayListOf()
        source.value = SRXAgentsDialogFragment.SRXAgentsSource.SOURCE_CHAT_MAIN
    }

    fun loadSRXAgents() {
        page = 1
        agents.clear()
        performRequest(findAgentRepository.findAgentsForChat(page, searchText))
    }

    fun loadMoreSRXAgents() {
        performNextRequest(findAgentRepository.findAgentsForChat(page, searchText))
    }

    fun createGroup(title: String) {
        createSsmLiveData.postValue(ApiStatus.loadingInstance())
        val users: ArrayList<UserPO> = ArrayList()
        selectedAgents.value?.let { agents ->
            agents.forEach {
                users.add(
                    UserPO(
                        id = it.userId,
                        email = it.email,
                        name = it.name,
                        mobileCountryCode = AppConstant.COUNTRY_CODE_SINGAPORE,
                        mobileLocalNum = it.mobile.toInt(),
                        photo = it.photo,
                        companyName = it.agencyName,
                        debugAllowed = false
                    )
                )
            }
        }

        val ssmConversationRequest = CreateSsmConversationRequest(members = users, title = title)
        if (users.size == 1) {
            ssmConversationRequest.type = ChatEnum.ConversationType.ONE_TO_ONE.value
        } else {
            ssmConversationRequest.type = ChatEnum.ConversationType.GROUP_CHAT.value
        }

        ApiUtil.performRequest(
            applicationContext,
            chatRepository.createSsmConversation(ssmConversationRequest),
            onSuccess = { response ->
                insertConversation(response.convo)
                createSsmLiveData.postValue(ApiStatus.successInstance(response))
            },
            onFail = { apiError -> createSsmLiveData.postValue(ApiStatus.failInstance(apiError)) },
            onError = { createSsmLiveData.postValue(ApiStatus.errorInstance()) }
        )
    }

    private fun insertConversation(conversation: SsmConversationPO) {
        chatDbRepository.insertSsmConversation(conversation)
    }

    fun findConversationByUserId(id: Int) {
        chatDbRepository.findConversationIdByUserId(userId = id).performAsyncQueryLenient(onSuccess = {
            conversationId.postValue(it)
        }, onFail = {
            conversationId.postValue(null)
        })
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