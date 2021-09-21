package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.PropertyIndexEnum
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchGraphResponse
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchIndicesResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyIndexRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun loadMarketWatchIndices(): Call<LoadMarketWatchIndicesResponse> {
        return srxDataSource.loadMarketWatchIndices()
    }

    fun loadMarketWatchGraph(
        propertyType: PropertyIndexEnum.PropertyType,
        transactionType: PropertyIndexEnum.TransactionType
    ): Call<LoadMarketWatchGraphResponse> {
        return srxDataSource.loadMarketWatchGraph(propertyType.value, transactionType.value)
    }
}