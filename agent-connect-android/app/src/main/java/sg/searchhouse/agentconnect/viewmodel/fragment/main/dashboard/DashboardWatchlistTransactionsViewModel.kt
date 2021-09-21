package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.WatchlistRepository
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.api.watchlist.GetLatestPropertyCriteriaByTypeResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardWatchlistTransactionsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetLatestPropertyCriteriaByTypeResponse>(application) {

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var applicationContext: Context

    val transactionPairs =
        MutableLiveData<Map<Int, List<TableListResponse.Transactions.Result>>>()

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        performRequest(
            watchlistRepository.getLatestPropertyCriteriaByType(
                WatchlistEnum.WatchlistType.TRANSACTIONS,
                maxResult = AppConstant.BATCH_SIZE_DASHBOARD_WATCHLIST
            )
        )
    }

    fun performGetWatchlistDetails(watchList: WatchlistPropertyCriteriaPO) {
        val watchListId = watchList.id ?: throw IllegalStateException("Watchlist ID null!")
        ApiUtil.performRequest(applicationContext,
            watchlistRepository.getPropertyCriteriaLatestTransactions(
                id = watchListId,
                maxResult = TRANSACTIONS_MAX_RESULT
            ), onSuccess = { response ->
                updateTransactionPairs(watchListId, response.transactions.results)
            }, onFail = {
                updateTransactionPairs(watchListId, emptyList())
            }, onError = {
                updateTransactionPairs(watchListId, emptyList())
            })
    }

    private fun updateTransactionPairs(
        watchListId: Int,
        transactions: List<TableListResponse.Transactions.Result>
    ) {
        val mTransactionPairs = Pair(watchListId, transactions)
        val existing = transactionPairs.value ?: emptyMap()
        val updated = existing + mTransactionPairs
        transactionPairs.postValue(updated)
    }

    override fun shouldResponseBeOccupied(response: GetLatestPropertyCriteriaByTypeResponse): Boolean {
        return response.watchlists.isNotEmpty()
    }

    companion object {
        const val TRANSACTIONS_MAX_RESULT = 3
    }
}