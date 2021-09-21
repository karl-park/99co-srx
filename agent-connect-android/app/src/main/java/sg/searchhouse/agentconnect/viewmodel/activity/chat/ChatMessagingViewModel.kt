package sg.searchhouse.agentconnect.viewmodel.activity.chat

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.dsl.performBlockQueryLenient
import sg.searchhouse.agentconnect.dsl.performAsyncQueriesLenient
import sg.searchhouse.agentconnect.dsl.performAsyncQueryLenient
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ChatEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.chat.*
import sg.searchhouse.agentconnect.model.api.listing.GetListingResponse
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import java.io.File
import javax.inject.Inject

class ChatMessagingViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var applicationContext: Context

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private val serverMessageTotal = MutableLiveData<Int>()
    private val localMessageTotal = MutableLiveData<Int>()

    val messages = arrayListOf<Any>()

    val user = MutableLiveData<UserPO>()
    val agent = MutableLiveData<AgentPO>()
    val conversationId = MutableLiveData<String>()
    val conversation = MutableLiveData<SsmConversationPO>()
    val storedMessages = MutableLiveData<List<SsmMessagePO>>()
    val serverMessages = MutableLiveData<List<SsmMessagePO>>()
    val newMessages = MutableLiveData<ArrayList<SsmMessagePO>>()
    val isNetworkConnected = MutableLiveData<Boolean>()
    lateinit var paramType: ChatEnum.ChatMessagingParamType
    val isConversationRetrieved = MutableLiveData<Boolean>()

    // Listing to share
    val listingTypeId = MutableLiveData<Pair<String, Int>>()

    val getConversationResponse = MutableLiveData<ApiStatus<GetSsmConversationResponse>>()
    val resetUnreadCountResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val findSsmMessagesResponse = MutableLiveData<ApiStatus<FindSsmMessagesResponse>>()
    val apiCallDefaultResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val createSsmConversationResponse = MutableLiveData<ApiStatus<CreateSsmConversationResponse>>()
    val sendMessageResponse = MutableLiveData<ApiStatus<SendSsmMessageResponse>>()
    val findMessagesByMessageIdResponse = MutableLiveData<ApiStatus<FindSsmMessagesResponse>>()

    val getPreviewListingResponse = MutableLiveData<ApiStatus<GetListingResponse>>()
    val previewListing = MutableLiveData<GetListingResponse>()

    init {
        viewModelComponent.inject(this)
        setupInitialData()
    }

    private fun setupInitialData() {
        messages.clear()
    }

    fun getConversationById(conversationId: Int) {
        if (isNetworkConnected.value == true) {
            getConversationByIdServer(conversationId)
        } else {
            getConversationByIdLocalDB(conversationId) {
                getConversationByIdServer(conversationId)
            }
        }
    }

    private fun getConversationByIdServer(conversationId: Int) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.getSsmConversation(conversationId),
            onSuccess = { response ->
                if (response.convo.unreadCount > 0) {
                    resetUnreadCount(response.convo)
                }
                conversation.postValue(response.convo)
                isConversationRetrieved.postValue(true)
                insertConversation(response.convo)
            },
            onFail = { getConversationResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { getConversationResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun performGetListingBlastConversations(conversationId: Int) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.getSsmConversation(conversationId),
            onSuccess = { response ->
                getConversationResponse.postValue(ApiStatus.successInstance(response))
            },
            onFail = { getConversationResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { getConversationResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    private fun getConversationByIdLocalDB(conversationId: Int, onFail: () -> Unit) {
        viewModelScope.launch {
            chatDbRepository.findSsmConversationById(conversationId = conversationId.toString())
                .toObservable()
                .subscribe({
                    if (it != null) {
                        conversation.postValue(it.fromEntityToSsmConversation())
                        isConversationRetrieved.postValue(true)
                    } else {
                        onFail.invoke()
                    }
                }, { error ->
                    error.printStackTrace()
                    onFail.invoke()
                })
        }
    }

    private fun resetUnreadCount(conversation: SsmConversationPO) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.resetUnreadCount(conversation),
            onSuccess = { resetUnreadCountResponse.postValue(ApiStatus.successInstance(it)) },
            onFail = { resetUnreadCountResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { resetUnreadCountResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    //Load Messages
    fun initialLoadMessages() {
        //TODO: bad logic. still cannot think how to show message from local and server. will refactor in future.
        //TODO:currently separate call with db and server by network connection
        if (isNetworkConnected.value == true) {
            loadMessagesFromServer()
        } else {
            loadMessagesFromLocalDB()
        }
    }

    fun loadMoreMessages() {
        loadMoreMessagesFromServer()
    }

    private fun loadMessagesFromLocalDB(startIndex: Int = 0) {
        viewModelScope.launch {
            val conversationId = conversation.value?.conversationId ?: return@launch
            chatDbRepository.findMessagesByConversationId(
                conversationId = conversationId,
                startIndex = startIndex,
                maxResults = ApiConstant.MAX_RESULTS_FIND_MESSAGES
            ).toObservable().subscribe({ items ->
                if (items.isNotEmpty()) {
                    val messages = items.map { it.fromEntityToSsmMessage() }
                    getMessageCountByConversationId()
                    loadMessagesFromServer(startIndex = startIndex, isReloadFromLocalDB = false)
                    storedMessages.postValue(messages)
                } else {
                    loadMessagesFromServer(startIndex = startIndex, isReloadFromLocalDB = true)
                }
            }, { error ->
                error.printStackTrace()
            })
        }
    }

    private fun loadMessagesFromServer(startIndex: Int = 0, isReloadFromLocalDB: Boolean = true) {
        if (isNetworkConnected.value == true) {
            val conversationId = conversation.value?.conversationId
                ?: return ErrorUtil.handleError("Missing conversation id")
            ApiUtil.performRequest(
                applicationContext,
                chatRepository.findSsmMessages(
                    conversationId = conversationId,
                    startIndex = startIndex,
                    maxResults = ApiConstant.MAX_RESULTS_FIND_MESSAGES
                ),
                onSuccess = { response ->
                    serverMessageTotal.postValue(response.total)
                    if (response.messages.isNotEmpty()) {
                        insertMessages(
                            messages = response.messages,
                            isReloadFromLocalDB = isReloadFromLocalDB
                        )
                    }
                },
                onFail = { findSsmMessagesResponse.postValue(ApiStatus.failInstance(it)) },
                onError = { findSsmMessagesResponse.postValue(ApiStatus.errorInstance()) }
            )
        }
    }

    private fun loadMoreMessagesFromServer() {
        val conversationId = conversation.value?.conversationId
            ?: return ErrorUtil.handleError("Missing conversation id")
        val startIndex = getMessageCountIndex() ?: 0
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.findSsmMessages(
                conversationId = conversationId,
                startIndex = startIndex,
                maxResults = ApiConstant.MAX_RESULTS_FIND_MESSAGES
            ),
            onSuccess = { response ->
                serverMessageTotal.postValue(response.total)
                serverMessages.postValue(response.messages)
                insertMessages(messages = response.messages, isReloadFromLocalDB = false)
            },
            onFail = { findSsmMessagesResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { findSsmMessagesResponse.postValue(ApiStatus.errorInstance()) }
        )
    }


    fun blackListAgent() {
        val ssmConversation = conversation.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.blacklistLeaveSsmConversations(listOf(ssmConversation)),
            onSuccess = { response ->
                deleteConversationFromLocalDB(ssmConversation, response)
            },
            onFail = { apiCallDefaultResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { apiCallDefaultResponse.postValue(ApiStatus.errorInstance()) })
    }

    fun deleteConversation() {
        val ssmConversation = conversation.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.leaveSsmConversations(arrayListOf(ssmConversation)),
            onSuccess = { response ->
                deleteConversationFromLocalDB(ssmConversation, response)
            },
            onFail = { apiCallDefaultResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { apiCallDefaultResponse.postValue(ApiStatus.errorInstance()) })
    }

    fun createSsmConversation(sendMessage: (conversationId: String) -> Unit) {
        when (paramType) {
            ChatEnum.ChatMessagingParamType.AGENT -> {
                val agentPO = agent.value ?: return ErrorUtil.handleError("Missing agent")
                val tempMobileNumber = if (NumberUtil.isNumber(agentPO.mobile)) {
                    agentPO.mobile.toIntOrNull() ?: 0
                } else {
                    StringUtil.getNumbersFromString(agentPO.mobile).toIntOrNull() ?: 0
                }

                val user = UserPO(
                    id = agentPO.userId,
                    email = agentPO.email,
                    name = agentPO.name,
                    mobileCountryCode = AppConstant.COUNTRY_CODE_SINGAPORE,
                    mobileLocalNum = tempMobileNumber,
                    photo = agentPO.photo,
                    companyName = agentPO.agencyName,
                    debugAllowed = false
                )
                performCreateSsmConversationRequest(
                    CreateSsmConversationRequest(
                        members = arrayListOf(user),
                        title = "",
                        type = ChatEnum.ConversationType.ONE_TO_ONE.value
                    ), sendMessage
                )
            }
            ChatEnum.ChatMessagingParamType.USER -> {
                val userPO = user.value ?: return ErrorUtil.handleError("Missing user")
                performCreateSsmConversationRequest(
                    CreateSsmConversationRequest(
                        members = arrayListOf(userPO),
                        title = "",
                        type = ChatEnum.ConversationType.ONE_TO_ONE.value
                    ), sendMessage
                )
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun performCreateSsmConversationRequest(
        ssmConversationRequest: CreateSsmConversationRequest,
        sendMessage: (conversationId: String) -> Unit
    ) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.createSsmConversation(ssmConversationRequest),
            onSuccess = { response ->
                //update values
                conversation.postValue(response.convo)
                conversationId.postValue(response.convo.conversationId)
                paramType = ChatEnum.ChatMessagingParamType.CONVERSATION_ID
                insertConversation(response.convo)
                sendMessage.invoke(response.convo.conversationId)
            },
            onFail = { createSsmConversationResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { createSsmConversationResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun sendMessage(message: String) {
        val listingTypeId = listingTypeId.value
        if (previewListing.value != null && listingTypeId != null) {
            sendListingEnquiry(listingTypeId.first, listingTypeId.second, message)
        } else {
            sendTextMessage(message)
        }
    }

    private fun sendTextMessage(message: String) {
        val conversationId = conversation.value?.conversationId ?: return
        sendMessageResponse.postValue(ApiStatus.loadingInstance())
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.sendTextMessage(conversationId, message),
            onSuccess = { response ->
                insertMessages(response.newMessages, isReloadFromLocalDB = false)
                sendMessageResponse.postValue(ApiStatus.successInstance(response))
            },
            onFail = { sendMessageResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { sendMessageResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    private fun sendListingEnquiry(listingType: String, listingId: Int, message: String) {
        if (previewListing.value == null) return
        val conversationId = conversation.value?.conversationId ?: return
        sendMessageResponse.postValue(ApiStatus.loadingInstance())
        chatRepository.sendEnquireListingMessage(conversationId, listingType, listingId, message)
            .performRequest(applicationContext, onSuccess = { response ->
                // Assume only one message
                val newMessages = when (response.newMessages.size) {
                    1 -> {
                        val thisMessage = response.newMessages[0]
                        val listingPO =
                            previewListing.value?.fullListing?.listingDetailPO?.listingPO
                        thisMessage.listing = listingPO
                        listOf(thisMessage)
                    }
                    else -> emptyList()
                }
                insertMessages(newMessages, isReloadFromLocalDB = false)
                sendMessageResponse.postValue(ApiStatus.successInstance(response))
            }, onFail = {
                sendMessageResponse.postValue(ApiStatus.failInstance(it))
            }, onError = {
                sendMessageResponse.postValue(ApiStatus.errorInstance())
            })
    }

    fun sendImage(image: File, caption: String? = null) {
        val conversationId = conversation.value?.conversationId ?: return
        sendMessageResponse.postValue(ApiStatus.loadingInstance())
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.sendImage(conversationId, image, caption),
            onSuccess = {
                insertMessages(it.newMessages, isReloadFromLocalDB = false)
                sendMessageResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = { sendMessageResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { sendMessageResponse.postValue(ApiStatus.errorInstance()) }
        )

    }

    fun loadMessagesByMessageIdFromServer(conversationId: String, messageId: String) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.findSsmMessagesByMessageId(
                conversationId,
                0,
                1,
                messageId
            ),
            onSuccess = { response ->
                insertMessages(response.messages, isReloadFromLocalDB = false)
                serverMessageTotal.postValue(response.total)
                newMessages.postValue(response.messages)
            },
            onFail = { findMessagesByMessageIdResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { findMessagesByMessageIdResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    //Room DB
    private fun insertConversation(conversation: SsmConversationPO) {
        chatDbRepository.insertSsmConversation(conversation).performAsyncQueriesLenient {
            // Do nothing
        }
    }

    private fun getMessageCountByConversationId() {
        val conversationId = conversation.value?.conversationId ?: return
        viewModelScope.launch {
            chatDbRepository.getMessageCountByConversationId(conversationId)
                .toObservable()
                .subscribe({ count ->
                    println(count)
                    localMessageTotal.postValue(count)
                }, { error -> error.printStackTrace() })
        }
    }

    private fun insertMessages(messages: List<SsmMessagePO>, isReloadFromLocalDB: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            val conversationId = conversation.value?.conversationId ?: return@launch
            // Check if has conversation
            chatDbRepository.findSsmConversationById(conversationId).performBlockQueryLenient()
                ?: return@launch

            // Filter
            val thisConversationMessages = messages.filter { it.conversationId == conversationId }

            // Insert or update messages
            thisConversationMessages.map { message ->
                chatDbRepository.insertOrUpdateSsmMessage(message = message.fromSsmMessageToEntity())
            }

            // Maybe reload messages from local DB
            if (isReloadFromLocalDB) {
                loadMessagesFromLocalDB(startIndex = 0)
            }
        }
    }

    fun findSsmConversationIdByUserId(userId: Int) {
        chatDbRepository.findConversationIdByUserId(userId = userId).performAsyncQueryLenient(
            onSuccess = {
                conversationId.postValue(it)
                paramType = ChatEnum.ChatMessagingParamType.CONVERSATION_ID
            }, onFail = {
                // Do nothing
            })
    }

    private fun deleteConversationFromLocalDB(
        conversation: SsmConversationPO,
        response: DefaultResultResponse
    ) {
        viewModelScope.launch {
            chatDbRepository.findSsmConversationById(conversation.getSsmConversationId())
                .toObservable()
                .subscribe({
                    chatDbRepository.deleteConversation(conversation.getSsmConversationId())
                        .performAsyncQueriesLenient {
                            if ((it.last() ?: 0) > 0) {
                                apiCallDefaultResponse.postValue(ApiStatus.successInstance(response))
                            } else {
                                ErrorUtil.handleError("Failed to remove conversation")
                            }
                        }
                }, { error -> error.printStackTrace() })
        }
    }

    private fun getMessageCountIndex(): Int? {
        val messageList = messages.filterIsInstance<SsmMessagePO>()
        return messageList.size
    }

    fun removeLoadingIndicator() {
        messages.removeAll(messages.filterIsInstance<Loading>())
    }

    fun canLoadMore(): Boolean {
        val messageItemSize = getMessageCountIndex() ?: return false
        val total = serverMessageTotal.value ?: return false
        return messageItemSize < total
    }

    fun performGetPreviewListing(listingType: String, listingId: Int) {
        getPreviewListingResponse.postValue(ApiStatus.loadingInstance())
        listingSearchRepository.getListing(listingId.toString(), listingType)
            .performRequest(applicationContext, onSuccess = {
                previewListing.postValue(it)
                getPreviewListingResponse.postValue(ApiStatus.successInstance(it))
            }, onFail = {
                previewListing.postValue(null)
                getPreviewListingResponse.postValue(ApiStatus.failInstance(it))
            }, onError = {
                previewListing.postValue(null)
                getPreviewListingResponse.postValue(ApiStatus.errorInstance())
            })
    }
}