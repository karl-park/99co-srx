package sg.searchhouse.agentconnect.viewmodel.fragment.main.chat

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.dsl.performAsyncQueriesLenient
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.chat.SrxAgentEnquiryPO
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class ChatCustomerEnquiryViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var applicationContext: Context

    val listingInformation: MutableLiveData<ListingPO> = MutableLiveData()
    val srxAgentEnquiryPO: MutableLiveData<SrxAgentEnquiryPO> = MutableLiveData()
    val deleteEnquiryStatus: MutableLiveData<ApiStatus<DefaultResultResponse>> = MutableLiveData()
    val status: MutableLiveData<ApiStatus.StatusKey> = MutableLiveData()
    val showListingInfo: MutableLiveData<Boolean> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }

    fun populateListingInformation(listingId: String, listingType: String) {
        status.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(
            applicationContext,
            listingSearchRepository.getListing(listingId, listingType),
            onSuccess = { response ->
                response.fullListing.let {
                    //getting listingPO info
                    it.listingDetailPO.listingPO.let { listingPO ->
                        listingInformation.postValue(listingPO)
                    }
                }
                status.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = { apiError ->
                println(apiError.error)
                status.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                status.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    //delete enquiry
    fun deleteEnquiry() {
        //start calling loading
        deleteEnquiryStatus.postValue(ApiStatus.loadingInstance())
        srxAgentEnquiryPO.value?.let { it ->
            ApiUtil.performRequest(
                applicationContext,
                chatRepository.deleteEnquiries(arrayListOf(it)),
                onSuccess = { requestBody ->
                    deleteEnquiryStatus.postValue(ApiStatus.successInstance(requestBody))
                },
                onFail = { apiError ->
                    deleteEnquiryStatus.postValue(ApiStatus.failInstance(apiError))
                },
                onError = {
                    deleteEnquiryStatus.postValue(ApiStatus.errorInstance())
                }
            )
        }
    }

    fun resetUnreadCount(conversation: SsmConversationPO) {
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.resetUnreadCount(conversation),
            onSuccess = { response ->
                println("Reset Account success $response")
            },
            onFail = { apiError ->
                println("Reset Account fail $apiError")
            },
            onError = {
                println("on Error resetting account")
            }
        )
    }

    fun deleteConversationsFromLocalDB(conversations: ArrayList<SsmConversationPO>) {
        conversations.map {
            chatDbRepository.deleteConversation(it.getSsmConversationId())
        }.flatten().performAsyncQueriesLenient { }
    }
}