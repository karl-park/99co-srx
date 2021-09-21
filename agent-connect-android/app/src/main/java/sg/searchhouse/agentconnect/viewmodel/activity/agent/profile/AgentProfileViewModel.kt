package sg.searchhouse.agentconnect.viewmodel.activity.agent.profile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.data.repository.UserProfileRepository
import sg.searchhouse.agentconnect.data.repository.WatchlistRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.agent.GetAgentFullProfileResponse
import sg.searchhouse.agentconnect.model.api.agent.GetPackageDetailsResponse
import sg.searchhouse.agentconnect.model.api.agent.UserSubscriptionPO
import sg.searchhouse.agentconnect.model.api.watchlist.GetLatestPropertyCriteriaByTypeResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.io.File
import javax.inject.Inject


class AgentProfileViewModel constructor(application: Application) :
        ApiBaseViewModel<GetAgentFullProfileResponse>(application) {

    @Inject
    lateinit var agentRepository: AgentRepository

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var applicationContext: Context

    val agentPO: MutableLiveData<AgentPO> = MutableLiveData()
    val agentFullProfile: MutableLiveData<GetAgentFullProfileResponse.AgentFullProfile> =
            MutableLiveData()
    val subscriptionStatus: MutableLiveData<String> = MutableLiveData()
    val subscription: MutableLiveData<UserSubscriptionPO> = MutableLiveData()
    val subscriptionPackageStatus: MutableLiveData<ApiStatus<GetPackageDetailsResponse>> =
            MutableLiveData()
    val getAgentCvFailed: MutableLiveData<String> = MutableLiveData()

    val viewMyClientsLabel: LiveData<String> = Transformations.map(mainResponse) {
        val clientCount = it?.data?.clientCount ?: 0
        applicationContext.getString(
                R.string.label_view_my_clients,
                NumberUtil.formatThousand(clientCount)
        )
    }

    val getWatchListsResponse = MutableLiveData<GetLatestPropertyCriteriaByTypeResponse?>()
    val watchlistStatus: MutableLiveData<ApiStatus.StatusKey> = MutableLiveData()
    val isShowWatchlistOccupied = MediatorLiveData<Boolean>()
    val isShowWatchlistEmpty = MediatorLiveData<Boolean>()
    val watchListPage = MutableLiveData<Int>()
    private val watchLists = MutableLiveData<List<WatchlistPropertyCriteriaPO>>()

    val isPropertyCriteriaHiddenIndUpdated = MutableLiveData<Boolean>()

    val shouldShowWatchList = MutableLiveData<Boolean>().apply { value = false }

    val updatePhotoStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val removePhotoStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    val isProfilePhotoUpdated = MutableLiveData<Boolean>()

    val hasGroupSubscription: Boolean = SessionUtil.getSubscriptionConfig().srx99CombinedSub

    init {
        viewModelComponent.inject(this)

        agentFullProfile.value = null

        setupWatchListMediatorLiveData()

        performGetAgentFullProfile()
    }

    private fun setupWatchListMediatorLiveData() {
        setupIsShowWatchlistOccupied()
        setupIsShowWatchlistEmpty()
    }

    private fun setupIsShowWatchlistEmpty() {
        isShowWatchlistEmpty.addSource(getWatchListsResponse) {
            isShowWatchlistEmpty.postValue(isShowWatchlistEmpty())
        }
        isShowWatchlistEmpty.addSource(watchlistStatus) {
            isShowWatchlistEmpty.postValue(isShowWatchlistEmpty())
        }
    }

    private fun setupIsShowWatchlistOccupied() {
        isShowWatchlistOccupied.addSource(getWatchListsResponse) {
            isShowWatchlistOccupied.postValue(isShowWatchlistOccupied())
        }
        isShowWatchlistOccupied.addSource(watchlistStatus) {
            isShowWatchlistOccupied.postValue(isShowWatchlistOccupied())
        }
    }

    private fun isShowWatchlistEmpty(): Boolean {
        return getWatchListsResponse.value?.let { watchlistStatus.value in ApiStatus.showOccupiedStatuses && it.total == 0 }
                ?: false
    }

    private fun isShowWatchlistOccupied(): Boolean {
        return getWatchListsResponse.value?.let { watchlistStatus.value in ApiStatus.showOccupiedStatuses && it.total > 0 }
                ?: false
    }

    fun getAgentCv(agentId: Int) {
        ApiUtil.performRequest(
                applicationContext,
                agentRepository.getAgentCv(agentId),
                onSuccess = { response ->
                    agentPO.postValue(response.data)
                },
                onFail = {
                    getAgentCvFailed.postValue(it.error)
                    Log.e("AgentProfile", it.error)
                },
                onError = {
                    Log.e("AgentProfile", "Getting error in calling get agent cv of profile")
                }
        )
    }

    fun getSubscriptionPackageDetails(type: Int) {
        ApiUtil.performRequest(
                applicationContext,
                agentRepository.getSrxCreditPackageDetails(type),
                onSuccess = {
                    subscriptionPackageStatus.postValue(ApiStatus.successInstance(it))
                },
                onFail = {
                    subscriptionPackageStatus.postValue(ApiStatus.failInstance(it))
                },
                onError = {
                    subscriptionPackageStatus.postValue(ApiStatus.errorInstance())
                }
        )
    }

    fun performGetAgentFullProfile() {
        performRequest(agentRepository.getAgentFullProfile())
    }

    fun performGetWatchLists() {
        val page = watchListPage.value ?: return
        watchlistStatus.postValue(
                when (page) {
                    1 -> ApiStatus.StatusKey.LOADING
                    else -> ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
                }
        )
        watchlistRepository.getLatestPropertyCriteriaByType(
                page = page,
                maxResult = AppConstant.DEFAULT_BATCH_SIZE,
                showHidden = true
        ).performRequest(applicationContext, onSuccess = {
            getWatchListsResponse.postValue(it)
            watchlistStatus.postValue(ApiStatus.StatusKey.SUCCESS)
        }, onFail = {
            getWatchListsResponse.postValue(null)
            watchlistStatus.postValue(ApiStatus.StatusKey.FAIL)
        }, onError = {
            getWatchListsResponse.postValue(null)
            watchlistStatus.postValue(ApiStatus.StatusKey.ERROR)
        })
    }

    fun performUpdatePhoto(newPhotoFile: File) {
        updatePhotoStatus.postValue(ApiStatus.loadingInstance())
        userProfileRepository.updatePhoto(newPhotoFile)
                .performRequest(applicationContext, onSuccess = {
                    updatePhotoStatus.postValue(ApiStatus.successInstance(it))
                }, onFail = {
                    updatePhotoStatus.postValue(ApiStatus.failInstance(it))
                }, onError = {
                    updatePhotoStatus.postValue(ApiStatus.errorInstance())
                })
    }

    fun performRemovePhoto() {
        removePhotoStatus.postValue(ApiStatus.loadingInstance())
        userProfileRepository.removePhoto()
                .performRequest(applicationContext, onSuccess = {
                    removePhotoStatus.postValue(ApiStatus.successInstance(it))
                }, onFail = {
                    removePhotoStatus.postValue(ApiStatus.failInstance(it))
                }, onError = {
                    removePhotoStatus.postValue(ApiStatus.errorInstance())
                })
    }

    fun maybeLoadMoreWatchLists() {
        if (mainStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM) {
            if (!isWatchListTotalReached()) {
                val currentPage = watchListPage.value ?: return
                watchListPage.postValue(currentPage + 1)
            }
        }
    }

    // TODO Check further
    private fun isWatchListTotalReached(): Boolean {
        val total = getWatchListsResponse.value?.watchlists?.size ?: 0
        val currentSize = watchLists.value?.size ?: 0
        return currentSize >= total
    }

    fun performUpdatePropertyCriteriaHiddenInd(
            criteria: WatchlistPropertyCriteriaPO
    ) {
        val criteriaId = criteria.id ?: throw IllegalStateException("Missing criteria ID!")
        isPropertyCriteriaHiddenIndUpdated.postValue(null)
        ApiUtil.performRequest(
                applicationContext,
                watchlistRepository.updatePropertyCriteriaHiddenInd(listOf(criteriaId)),
                onSuccess = {
                    isPropertyCriteriaHiddenIndUpdated.postValue(true)
                    // Crashes when show message
                },
                onFail = {
                    // Handled
                },
                onError = {
                    // Handled
                })
    }

    fun getCeaNumber(): String? = mainResponse.value?.data?.profile?.ceaRegNo

    override fun shouldResponseBeOccupied(response: GetAgentFullProfileResponse): Boolean {
        return true
    }

}