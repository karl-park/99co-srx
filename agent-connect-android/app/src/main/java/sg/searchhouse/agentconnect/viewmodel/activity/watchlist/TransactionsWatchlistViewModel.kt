package sg.searchhouse.agentconnect.viewmodel.activity.watchlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.WatchlistRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.api.watchlist.GetLatestPropertyCriteriaByTypeResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.model.app.WatchlistHeader
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.viewmodel.base.PaginatedApiBaseViewModel
import javax.inject.Inject

class TransactionsWatchlistViewModel constructor(application: Application) :
    PaginatedApiBaseViewModel<GetLatestPropertyCriteriaByTypeResponse, WatchlistPropertyCriteriaPO>(
        application
    ) {

    val selectedCriteria = MutableLiveData<WatchlistPropertyCriteriaPO>()

    private val transactionsTotal = MutableLiveData<Int>()
    val transactionsPage = MutableLiveData<Int>()
    val transactions = MutableLiveData<List<TableListResponse.Transactions.Result>>() // TODO
    val transactionsStatus = MutableLiveData<ApiStatus.StatusKey>()

    val isShowTransactionsOccupied = MediatorLiveData<Boolean>()
    val isShowTransactionsEmpty = MediatorLiveData<Boolean>()

    val watchlistHeader =
        MediatorLiveData<WatchlistHeader>().apply { value = WatchlistHeader(0, 0) }

    val ownershipType = MediatorLiveData<ListingEnum.OwnershipType>().apply {
        value = ListingEnum.OwnershipType.SALE
    }
    val propertyMainType = MediatorLiveData<ListingEnum.PropertyMainType>().apply {
        value = ListingEnum.PropertyMainType.CONDO
    }

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupShowTransactions()
    }

    private fun setupShowTransactions() {
        setupIsShowTransactionsOccupied()
        setupIsShowTransactionsEmpty()
    }

    private fun setupIsShowTransactionsEmpty() {
        isShowTransactionsEmpty.addSource(transactions) {
            isShowTransactionsEmpty.postValue(checkIsShowTransactionsEmpty())
        }
        isShowTransactionsEmpty.addSource(transactionsStatus) {
            isShowTransactionsEmpty.postValue(checkIsShowTransactionsEmpty())
        }
    }

    private fun setupIsShowTransactionsOccupied() {
        isShowTransactionsOccupied.addSource(transactions) {
            isShowTransactionsOccupied.postValue(checkIsShowTransactionsOccupied())
        }
        isShowTransactionsOccupied.addSource(transactionsStatus) {
            isShowTransactionsOccupied.postValue(checkIsShowTransactionsOccupied())
        }
    }

    private fun checkIsShowTransactionsOccupied(): Boolean {
        return transactions.value?.isNotEmpty() == true && transactionsStatus.value in ApiStatus.showOccupiedStatuses
    }

    private fun checkIsShowTransactionsEmpty(): Boolean {
        return transactions.value?.isNotEmpty() != true && transactionsStatus.value in ApiStatus.showOccupiedStatuses
    }

    fun performGetTransactions() {
        val criteriaId = selectedCriteria.value?.id ?: return
        val page = transactionsPage.value ?: return
        transactionsStatus.postValue(
            when (page) {
                1 -> ApiStatus.StatusKey.LOADING
                else -> ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
            }
        )
        watchlistRepository.getPropertyCriteriaLatestTransactions(criteriaId, page)
            .performRequest(applicationContext, onSuccess = {
                transactionsStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                transactionsTotal.postValue(it.transactions.total)
                transactions.postValue(
                    when (page) {
                        1 -> it.transactions.results
                        else -> {
                            val newTransactions =
                                (transactions.value ?: emptyList()) + it.transactions.results
                            newTransactions
                        }
                    }
                )
            }, onFail = {
                transactionsStatus.postValue(ApiStatus.StatusKey.FAIL)
            }, onError = {
                transactionsStatus.postValue(ApiStatus.StatusKey.ERROR)
            })
    }

    fun maybeAddTransactionPage() {
        if (transactionsStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM) {
            if (!isTransactionsTotalReached()) {
                val currentPage = transactionsPage.value ?: return
                transactionsPage.postValue(currentPage + 1)
            }
        }
    }

    private fun isTransactionsTotalReached(): Boolean {
        val total = transactionsTotal.value ?: 0
        val currentSize = transactions.value?.size ?: 0
        return currentSize >= total
    }

    override fun getResponseTotal(): Int {
        return mainResponse.value?.total ?: 0
    }

    override fun getResponseListItems(): List<WatchlistPropertyCriteriaPO> {
        return mainResponse.value?.watchlists?.map {
            it.type = WatchlistEnum.WatchlistType.TRANSACTIONS.value
            it
        } ?: emptyList()
    }

    override fun getRequest(page: Int): Call<GetLatestPropertyCriteriaByTypeResponse> {
        return watchlistRepository.getLatestPropertyCriteriaByType(
            WatchlistEnum.WatchlistType.TRANSACTIONS,
            page = page,
            maxResult = AppConstant.DEFAULT_BATCH_SIZE
        )
    }

    override fun getFirstPage(): Int {
        return 1
    }
}