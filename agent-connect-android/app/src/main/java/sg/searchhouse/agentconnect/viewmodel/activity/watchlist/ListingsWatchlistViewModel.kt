package sg.searchhouse.agentconnect.viewmodel.activity.watchlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.WatchlistRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.watchlist.GetLatestPropertyCriteriaByTypeResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.model.app.WatchlistHeader
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.viewmodel.base.PaginatedApiBaseViewModel
import javax.inject.Inject

class ListingsWatchlistViewModel constructor(application: Application) :
    PaginatedApiBaseViewModel<GetLatestPropertyCriteriaByTypeResponse, WatchlistPropertyCriteriaPO>(
        application
    ) {

    val selectedCriteria = MutableLiveData<WatchlistPropertyCriteriaPO>()

    private val listingsTotal = MutableLiveData<Int>()
    val listingsPage = MutableLiveData<Int>()
    val listings = MutableLiveData<List<ListingPO>>()
    val listingsStatus = MutableLiveData<ApiStatus.StatusKey>()

    val isShowListingsOccupied = MediatorLiveData<Boolean>()
    val isShowListingsEmpty = MediatorLiveData<Boolean>()

    val watchlistHeader =
        MediatorLiveData<WatchlistHeader>().apply { value = WatchlistHeader(0, 0) }

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupShowListings()
    }

    private fun setupShowListings() {
        setupIsShowListingsOccupied()
        setupIsShowListingsEmpty()
    }

    private fun setupIsShowListingsEmpty() {
        isShowListingsEmpty.addSource(listings) {
            isShowListingsEmpty.postValue(checkIsShowListingsEmpty())
        }
        isShowListingsEmpty.addSource(listingsStatus) {
            isShowListingsEmpty.postValue(checkIsShowListingsEmpty())
        }
    }

    private fun setupIsShowListingsOccupied() {
        isShowListingsOccupied.addSource(listings) {
            isShowListingsOccupied.postValue(checkIsShowListingsOccupied())
        }
        isShowListingsOccupied.addSource(listingsStatus) {
            isShowListingsOccupied.postValue(checkIsShowListingsOccupied())
        }
    }

    private fun checkIsShowListingsOccupied(): Boolean {
        return listings.value?.isNotEmpty() == true && listingsStatus.value in ApiStatus.showOccupiedStatuses
    }

    private fun checkIsShowListingsEmpty(): Boolean {
        return listings.value?.isNotEmpty() != true && listingsStatus.value in ApiStatus.showOccupiedStatuses
    }

    fun performGetListings() {
        val criteriaId = selectedCriteria.value?.id ?: return
        val page = listingsPage.value ?: return
        listingsStatus.postValue(
            when (page) {
                1 -> ApiStatus.StatusKey.LOADING
                else -> ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
            }
        )
        watchlistRepository.getPropertyCriteriaLatestListings(criteriaId, page)
            .performRequest(applicationContext, onSuccess = {
                listingsStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                listingsTotal.postValue(it.listings.getGrandTotal())
                listings.postValue(
                    when (page) {
                        1 -> it.listings.getListingPOs()
                        else -> {
                            val newListings =
                                (listings.value ?: emptyList()) + it.listings.getListingPOs()
                            newListings
                        }
                    }
                )
            }, onFail = {
                listingsStatus.postValue(ApiStatus.StatusKey.FAIL)
            }, onError = {
                listingsStatus.postValue(ApiStatus.StatusKey.ERROR)
            })
    }

    fun maybeAddListingPage() {
        if (listingsStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM) {
            if (!isListingsTotalReached()) {
                val currentPage = listingsPage.value ?: return
                listingsPage.postValue(currentPage + 1)
            }
        }
    }

    private fun isListingsTotalReached(): Boolean {
        val total = listingsTotal.value ?: 0
        val currentSize = listings.value?.size ?: 0
        return currentSize >= total
    }

    override fun getResponseTotal(): Int {
        return mainResponse.value?.total ?: 0
    }

    override fun getResponseListItems(): List<WatchlistPropertyCriteriaPO> {
        return mainResponse.value?.watchlists?.map {
            it.type = WatchlistEnum.WatchlistType.LISTINGS.value
            it
        } ?: emptyList()
    }

    override fun getRequest(page: Int): Call<GetLatestPropertyCriteriaByTypeResponse> {
        return watchlistRepository.getLatestPropertyCriteriaByType(
            WatchlistEnum.WatchlistType.LISTINGS,
            page = page,
            maxResult = AppConstant.DEFAULT_BATCH_SIZE
        )
    }

    override fun getFirstPage(): Int {
        return 1
    }
}