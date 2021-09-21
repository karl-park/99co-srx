package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.flashreport.ListActiveMarketingFlashReportResponse
import sg.searchhouse.agentconnect.model.api.flashreport.ListAllMarketingFlashReportResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashReportRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    fun getAllActiveMarketingFlashReports(): Call<ListActiveMarketingFlashReportResponse> {
        return srxDataSource.listActiveMarketingFlashReport()
    }

    fun getAllMarketingFlashReport(
        page: Int? = null,
        property: String? = null,
        order: String? = null
    ): Call<ListAllMarketingFlashReportResponse> {
        return srxDataSource.listAllMarketingFlashReport(
            page = page,
            pageSize = AppConstant.BATCH_SIZE_DOUBLE,
            property = property,
            order = order
        )
    }
}