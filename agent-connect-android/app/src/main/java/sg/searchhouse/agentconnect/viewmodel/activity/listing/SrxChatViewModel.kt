package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.listing.FindListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SrxChatViewModel constructor(application: Application) :
    ApiBaseViewModel<FindListingsResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    val title = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val listingIds = MutableLiveData<List<Pair<String, String>>>()
    val listings = MutableLiveData<List<ListingPO>>()
    val srxstp = MutableLiveData<List<String>>()
    val tableLP = MutableLiveData<List<String>>()
    val createSsmBlastStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    private val btnState = MutableLiveData<ButtonState>()
    val actionBtnLabel: LiveData<String> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_sending_srx_chat)
            else -> applicationContext.getString(R.string.action_send_srx_chat)
        }
    }
    val isActionBtnEnabled: LiveData<Boolean> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> false
            else -> true
        }
    }
    val listingCount: LiveData<String> = Transformations.map(listingIds) { items ->
        items?.run {
            applicationContext.resources.getQuantityString(
                R.plurals.label_listing_count,
                this.size,
                NumberUtil.formatThousand(this.size)
            )
        } ?: ""
    }
    val isBlastListingsShowed = MutableLiveData<Boolean>().apply { value = true }
    val successMessage = MutableLiveData<String>()
    val createBlastStatusKey = MutableLiveData<ApiStatus.StatusKey>()

    init {
        viewModelComponent.inject(this)
        btnState.value = ButtonState.NORMAL
        title.value = applicationContext.getString(R.string.label_title)
        message.value = applicationContext.getString(R.string.msg_srx_blast)
    }

    fun getListingsByListingIds() {
        val tempListings = listingIds.value ?: return
        val listingIds = tempListings.joinToString(",") { it.first }
        performRequest(
            listingSearchRepository.findListingsByIds(
                maxResults = tempListings.size,
                listingIds = listingIds
            )
        )
    }

    fun createSsmBlastConversation() {
        btnState.value = ButtonState.SUBMITTING
        createBlastStatusKey.value = ApiStatus.StatusKey.LOADING
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.createSsmListingsBlastConversation(
                title = title.value,
                message = message.value,
                srxstp = srxstp.value,
                tableLP = tableLP.value
            ),
            onSuccess = {
                val listingCount = getListingCount()
                successMessage.postValue(
                    applicationContext.resources.getQuantityString(
                        R.plurals.label_srx_chat_success_listing,
                        listingCount,
                        NumberUtil.formatThousand(listingCount)
                    )
                )
                createBlastStatusKey.postValue(ApiStatus.StatusKey.SUCCESS)
                btnState.postValue(ButtonState.SUBMITTED)
                createSsmBlastStatus.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                createBlastStatusKey.postValue(ApiStatus.StatusKey.FAIL)
                btnState.postValue(ButtonState.ERROR)
                createSsmBlastStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                btnState.postValue(ButtonState.ERROR)
                createBlastStatusKey.postValue(ApiStatus.StatusKey.ERROR)
                createSsmBlastStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun getListingCount(): Int {
        val srxStpCount = srxstp.value?.size ?: 0
        val tableLPCount = tableLP.value?.size ?: 0
        return srxStpCount.plus(tableLPCount)
    }

    override fun shouldResponseBeOccupied(response: FindListingsResponse): Boolean {
        return true
    }
}