package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.data.repository.FlashReportRepository
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.flashreport.ListActiveMarketingFlashReportResponse
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.service.GenerateGeneralReportService
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardMarketFlashReportViewModel constructor(application: Application) :
    ApiBaseViewModel<ListActiveMarketingFlashReportResponse>(application) {

    @Inject
    lateinit var flashReportRepository: FlashReportRepository

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var okHttpClient: OkHttpClient

    val reports = arrayListOf<MarketingFlashReportPO>()
    val hasHistoricalAccess = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        performRequest(flashReportRepository.getAllActiveMarketingFlashReports())
    }

    fun downloadReport(report: MarketingFlashReportPO) {
        GenerateGeneralReportService.launch(
            applicationContext,
            report.reportLink,
            ReportEnum.ReportServiceType.FLASH_REPORTS,
            report.title
        )
    }

    override fun shouldResponseBeOccupied(response: ListActiveMarketingFlashReportResponse): Boolean {
        return response.marketFlashReports.isNotEmpty()
    }
}