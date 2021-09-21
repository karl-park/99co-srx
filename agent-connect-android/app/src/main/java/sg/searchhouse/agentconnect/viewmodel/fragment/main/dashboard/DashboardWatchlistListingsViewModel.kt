package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.WatchlistRepository
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.watchlist.GetLatestPropertyCriteriaByTypeResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardWatchlistListingsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetLatestPropertyCriteriaByTypeResponse>(application) {

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var applicationContext: Context

    // Watchlist id, listings
    val listingPairs = MutableLiveData<Map<Int, List<ListingPO>>>()

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        performRequest(
            watchlistRepository.getLatestPropertyCriteriaByType(
                WatchlistEnum.WatchlistType.LISTINGS,
                maxResult = AppConstant.BATCH_SIZE_DASHBOARD_WATCHLIST
            )
        )
    }

    fun performGetWatchlistDetails(watchList: WatchlistPropertyCriteriaPO) {
        val watchListId = watchList.id ?: throw IllegalStateException("Watchlist ID null!")
        ApiUtil.performRequest(applicationContext,
            watchlistRepository.getPropertyCriteriaLatestListings(
                id = watchListId,
                maxResult = LISTINGS_MAX_RESULT
            ), onSuccess = { response ->
                updateListingPairs(watchListId, response.listings.getListingPOs())
            }, onFail = {
                updateListingPairs(watchListId, emptyList())
            }, onError = {
                updateListingPairs(watchListId, emptyList())
            })
    }

    private fun updateListingPairs(watchListId: Int, listingPOs: List<ListingPO>) {
        val mListingPairs = Pair(watchListId, listingPOs)
        val existing = listingPairs.value ?: emptyMap()
        val updated = existing + mListingPairs
        listingPairs.postValue(updated)
    }

    override fun shouldResponseBeOccupied(response: GetLatestPropertyCriteriaByTypeResponse): Boolean {
        return response.watchlists.isNotEmpty()
    }

    companion object {
        const val LISTINGS_MAX_RESULT = 3
    }
}