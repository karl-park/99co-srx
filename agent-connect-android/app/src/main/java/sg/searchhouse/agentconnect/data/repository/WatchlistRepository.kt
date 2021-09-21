package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.api.watchlist.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/999653396/Watchlist+V1+API
 */
@Singleton
class WatchlistRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getPropertyCriteria(id: Int): Call<GetPropertyCriteriaResponse> {
        return srxDataSource.getPropertyCriteria(id)
    }

    fun savePropertyCriteria(watchlistPropertyCriteriaPO: WatchlistPropertyCriteriaPO): Call<SavePropertyCriteriaResponse> {
        return srxDataSource.savePropertyCriteria(watchlistPropertyCriteriaPO)
    }

    fun deletePropertyCriteria(id: Int): Call<DeletePropertyCriteriaResponse> {
        return srxDataSource.deletePropertyCriteria(listOf(id))
    }

    fun markPropertyCriteriaAsRead(ids: List<Int>): Call<DefaultResultResponse> {
        return srxDataSource.markPropertyCriteriaAsRead(ids)
    }

    fun updatePropertyCriteriaHiddenInd(ids: List<Int>): Call<DefaultResultResponse> {
        return srxDataSource.updatePropertyCriteriaHiddenInd(ids)
    }

    fun getLatestPropertyCriteriaByType(
        type: WatchlistEnum.WatchlistType? = null,
        page: Int = 1,
        maxResult: Int,
        showHidden: Boolean = false
    ): Call<GetLatestPropertyCriteriaByTypeResponse> {
        return srxDataSource.getLatestPropertyCriteriaByType(
            type?.value,
            page,
            maxResult,
            showHidden
        )
    }

    fun getPropertyCriteriaLatestListings(
        id: Int,
        page: Int = 1,
        maxResult: Int = AppConstant.DEFAULT_BATCH_SIZE
    ): Call<GetPropertyCriteriaLatestListingsResponse> {
        return srxDataSource.getPropertyCriteriaLatestListings(id, page, maxResult)
    }

    fun getPropertyCriteriaLatestTransactions(
        id: Int,
        page: Int = 1,
        maxResult: Int = AppConstant.BATCH_SIZE_DOUBLE
    ): Call<TableListResponse> {
        return srxDataSource.getPropertyCriteriaLatestTransactions(id, page, maxResult)
    }
}