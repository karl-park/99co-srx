package sg.searchhouse.agentconnect.viewmodel.fragment.main.chat

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.dsl.performAsyncQueriesLenient
import sg.searchhouse.agentconnect.dsl.performAsyncQueryLenient
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class AgentConversationListViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var applicationContext: Context

    var startIndex: Int = 0
    var conversations = arrayListOf<Any>()
    var selectedConversations = arrayListOf<SsmConversationPO>()

    private val isSwipeRefresh = MutableLiveData<Boolean>()
    val total = MutableLiveData<Int>()
    var storedConversations = MutableLiveData<ArrayList<SsmConversationPO>>()
    val status = MutableLiveData<ApiStatus.StatusKey>()

    val isListOccupied: LiveData<Boolean> = Transformations.map(status) { statusKey ->
        statusKey?.let {
            (it == SUCCESS || it == LOADING_NEXT_LIST_ITEM) && conversations.isNotEmpty()
        } ?: false
    }
    val isShowEmpty: LiveData<Boolean> = Transformations.map(status) { statusKey ->
        statusKey?.let {
            (it == SUCCESS || it == LOADING_NEXT_LIST_ITEM) && conversations.isEmpty()
        } ?: false
    }

    init {
        viewModelComponent.inject(this)

        isSwipeRefresh.value = false
        storedConversations.value = arrayListOf()
        conversations.clear()
        selectedConversations.clear()
    }


    fun initialLoadAgentConversations() {
        startIndex = 0
        selectedConversations.clear()
        status.postValue(LOADING)
        loadAgentConversationsFromDB()
        loadAgentConversationsFromServer()
    }

    fun loadMoreAgentConversations() {
        startIndex = conversations.filterIsInstance<SsmConversationPO>().size
        status.postValue(LOADING_NEXT_LIST_ITEM)
        loadAgentConversationsFromDB()
        loadAgentConversationsFromServer()
    }

    fun reloadAgentConversations() {
        startIndex = 0
        selectedConversations.clear()
        isSwipeRefresh.postValue(true)
        loadAgentConversationsFromDB()
        loadAgentConversationsFromServer()
    }

    fun refreshAgentConversations() {
        startIndex = 0
        selectedConversations.clear()
        status.postValue(LOADING)
        val limit = if (conversations.isEmpty()) {
            ApiConstant.MAX_RESULTS_CONVERSATION_LIST
        } else {
            conversations.filterIsInstance<SsmConversationPO>().size
        }
        loadAgentConversationsFromDB(limit)
        loadAgentConversationsFromServer(limit)
    }

    private fun loadAgentConversationsFromDB(maxResults: Int = ApiConstant.MAX_RESULTS_CONVERSATION_LIST) {
        chatDbRepository.getAgentConversations(startIndex, maxResults)
            .performAsyncQueryLenient(onSuccess = { conversations ->
                if (conversations.isNotEmpty()) {
                    storedConversations.postValue(mappingToConversationPO(conversations))
                }
            }, onFail = {
                // Do nothing
            })
    }

    private fun loadAgentConversationsFromServer(maxResults: Int = ApiConstant.MAX_RESULTS_CONVERSATION_LIST) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.findAgentSsmConversations(
                startIndex,
                maxResults
            ),
            onSuccess = {
                total.postValue(it.total)
                insertConversations(conversations = it.convos, maxResults = maxResults)
            },
            onFail = {
                status.postValue(FAIL)
            },
            onError = {
                status.postValue(ERROR)
            }
        )
    }

    private fun mappingToConversationPO(list: List<SsmConversationEntity>): ArrayList<SsmConversationPO> {
        val conversations: ArrayList<SsmConversationPO> = arrayListOf()
        list.forEach { convo ->
            conversations.add(convo.fromEntityToSsmConversation())
        }
        return conversations
    }

    private fun insertConversations(
        conversations: ArrayList<SsmConversationPO>,
        maxResults: Int = ApiConstant.MAX_RESULTS_CONVERSATION_LIST
    ) {
        chatDbRepository.insertSsmConversations(conversations).performAsyncQueriesLenient {
            status.postValue(SUCCESS)
            loadAgentConversationsFromDB(maxResults)
        }
    }

    fun canLoadNext(): Boolean {
        val listItemsSize = conversations.filterIsInstance<SsmConversationPO>().size
        val total = total.value ?: return false
        return listItemsSize < total
    }

}