package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.homereport.GenerateHomeReportResponse
import sg.searchhouse.agentconnect.model.api.homereport.GetHomeReportUsageResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/752746541/Home+Report+V1+API
 */
@Singleton
class HomeReportRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun generateHomeReport(
        id: Int,
        agency: String,
        block: String,
        cdResearchSubtype: Int,
        unit: String,
        streetKey: String,
        clientName: String? = null
    ): Call<GenerateHomeReportResponse> {
        return srxDataSource.generateHomeReport(
            id,
            agency,
            block,
            cdResearchSubtype,
            unit,
            streetKey,
            preparedFor = clientName
        )
    }

    fun getHomeReportUsage(): Call<GetHomeReportUsageResponse> {
        return srxDataSource.getHomeReportUsage()
    }
}
