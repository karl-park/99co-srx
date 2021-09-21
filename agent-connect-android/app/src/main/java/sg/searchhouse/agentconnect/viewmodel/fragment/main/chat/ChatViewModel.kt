package sg.searchhouse.agentconnect.viewmodel.fragment.main.chat

import android.app.Application
import android.content.Context
import android.text.Editable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.dsl.performBlockQueryLenient
import sg.searchhouse.agentconnect.dsl.performAsyncQueriesLenient
import sg.searchhouse.agentconnect.dsl.performAsyncQueryLenient
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.chat.SrxAgentEnquiryPO
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.enumeration.status.ButtonState.*
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class ChatViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var applicationContext: Context

    val selectedConversations = MutableLiveData<ArrayList<SsmConversationPO>>()
    val searchResults = MutableLiveData<ArrayList<Any>>()
    val searchKeyword = MutableLiveData<String>()
    val isEditMode = MutableLiveData<Boolean>()
    val buttonState = MutableLiveData<ButtonState>()
    val isEnabledActionButtons = MediatorLiveData<Boolean>()
    val conversation = MutableLiveData<SsmConversationPO>()
    val isSearchConversationEnabled = MutableLiveData<Boolean>().apply { value = true }

    //API Result
    val apiCallStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    init {
        viewModelComponent.inject(this)
        searchResults.value = arrayListOf()
        isEditMode.value = false
        searchKeyword.value = ""
        //TODO: remove it don't remember why added it
        buttonState.value = ERROR
        setUpIsEnabledActionButtons()
    }

    private fun setUpIsEnabledActionButtons() {
        isEnabledActionButtons.value = false
        isEnabledActionButtons.addSource(buttonState) {
            val response = it ?: return@addSource
            isEnabledActionButtons.postValue(response == NORMAL || response == SUBMITTED)
        }
        isEnabledActionButtons.addSource(selectedConversations) {
            val response = it ?: return@addSource
            isEnabledActionButtons.postValue(response.size > 0)
        }
    }

    fun onToggleEditMode() {
        isEditMode.value = isEditMode.value != true
    }

    fun onDeleteConversations() {
        buttonState.postValue(SUBMITTING)
        selectedConversations.value?.let { conversations ->
            val enquiries =
                conversations.filter { conversation -> conversation.type == ChatEnum.ConversationType.AGENT_ENQUIRY.value } as ArrayList<SsmConversationPO>
            if (enquiries.isNotEmpty()) {
                conversations.removeAll { conversation -> conversation.type == ChatEnum.ConversationType.AGENT_ENQUIRY.value }
                deleteEnquiries(enquiries, conversations)
            } else {
                leaveSsmConversations(conversations)
            }
        }
    }

    private fun leaveSsmConversations(conversations: ArrayList<SsmConversationPO>) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.leaveSsmConversations(conversations),
            onSuccess = { response ->
                deleteConversationsFromLocalDB(conversations, response)
            },
            onFail = { apiError ->
                buttonState.postValue(NORMAL)
                apiCallStatus.postValue(ApiStatus.failInstance(apiError))
            },
            onError = {
                buttonState.postValue(NORMAL)
                apiCallStatus.postValue(ApiStatus.errorInstance())
            })
    }

    private fun deleteEnquiries(
        enquiries: ArrayList<SsmConversationPO>,
        conversations: ArrayList<SsmConversationPO>
    ) {
        //get srx enquiries from srx conversation po
        val srxEnquiries = arrayListOf<SrxAgentEnquiryPO>()
        enquiries.map { conversation -> conversation.enquiry?.let { srxEnquiries.add(it) } }

        ApiUtil.performRequest(
            applicationContext,
            chatRepository.deleteEnquiries(srxEnquiries),
            onSuccess = { response ->
                deleteConversationsFromLocalDB(enquiries, response)
                if (conversations.isNotEmpty()) {
                    leaveSsmConversations(conversations)
                }
            },
            onFail = { apiError ->
                buttonState.postValue(NORMAL)
                apiCallStatus.postValue(ApiStatus.failInstance(apiError))
            },
            onError = {
                buttonState.postValue(NORMAL)
                apiCallStatus.postValue(ApiStatus.errorInstance())
            })
    }

    fun onBlacklistAgents() {
        buttonState.postValue(SUBMITTING)
        selectedConversations.value?.let { conversations ->
            ApiUtil.performRequest(
                applicationContext,
                chatRepository.blacklistLeaveSsmConversations(conversations),
                onSuccess = { response ->
                    deleteConversationsFromLocalDB(conversations, response)
                },
                onFail = { apiError ->
                    buttonState.postValue(NORMAL)
                    apiCallStatus.postValue(ApiStatus.failInstance(apiError))
                },
                onError = {
                    buttonState.postValue(NORMAL)
                    apiCallStatus.postValue(ApiStatus.errorInstance())
                }
            )
        }
    }

    fun onMarkAsReadConversations() {
        buttonState.postValue(SUBMITTING)
        var callApiCount = 0
        selectedConversations.value?.let { conversations ->
            conversations.forEach { eachConversation ->
                callApiCount += 1
                ApiUtil.performRequest(
                    applicationContext,
                    chatRepository.resetUnreadCount(eachConversation),
                    onSuccess = { response ->
                        if (callApiCount == conversations.size) {
                            buttonState.postValue(SUBMITTED)
                            apiCallStatus.postValue(ApiStatus.successInstance(response))
                        }
                    },
                    onFail = { apiError ->
                        buttonState.postValue(NORMAL)
                        apiCallStatus.postValue(ApiStatus.failInstance(apiError))
                    },
                    onError = {
                        buttonState.postValue(NORMAL)
                        apiCallStatus.postValue(ApiStatus.errorInstance())
                    }

                )
            }
        }
    }

    //SEARCH
    fun afterTextChangedSearch(editable: Editable?) {
        val searchText = editable?.toString() ?: ""
        if (searchText.trim().length > 2) {
            searchConversationsByKeywords()
        } else if (searchText.trim().isEmpty()) {
            searchResults.value?.let {
                it.clear()
                searchResults.postValue(it)
            }
        }
    }

    fun searchConversationsByKeywords() {
        CoroutineScope(Dispatchers.IO).launch {
            val keyword = searchKeyword.value ?: return@launch
            val thisSearchResults = searchResults.value ?: return@launch
            thisSearchResults.clear()
            //Conversations
            val conversations =
                chatDbRepository.searchConversationsByKeyword(keyword.trim()).performBlockQueryLenient()
                    ?: emptyList()
            if (conversations.isNotEmpty()) {
                thisSearchResults.add(applicationContext.getString(R.string.label_conversations))
                thisSearchResults.addAll(conversations)
            }
            //Messages
            val messages =
                chatDbRepository.searchMessagesByKeyword(keyword.trim()).performBlockQueryLenient()
                    ?: emptyList()
            if (messages.isNotEmpty()) {
                thisSearchResults.add(applicationContext.getString(R.string.label_messages))
                thisSearchResults.addAll(messages)
            }
            searchResults.postValue(thisSearchResults)
        }
    }

    fun getConversationById(conversationId: String) {
        chatDbRepository.findSsmConversationById(conversationId)
            .performAsyncQueryLenient(onSuccess = { entity ->
                conversation.postValue(entity.fromEntityToSsmConversation())
            }, onFail = {
                // Do nothing
            })
    }

    private fun deleteConversationsFromLocalDB(
        conversations: ArrayList<SsmConversationPO>,
        response: DefaultResultResponse
    ) {
        conversations.map {
            chatDbRepository.deleteConversation(it.getSsmConversationId())
        }.flatten().performAsyncQueriesLenient {
            buttonState.postValue(SUBMITTED)
            apiCallStatus.postValue(ApiStatus.successInstance(response))
        }
    }
}
