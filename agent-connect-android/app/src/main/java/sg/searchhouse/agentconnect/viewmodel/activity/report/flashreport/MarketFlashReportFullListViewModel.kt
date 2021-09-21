package sg.searchhouse.agentconnect.viewmodel.activity.report.flashreport

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.FlashReportRepository
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.flashreport.ListAllMarketingFlashReportResponse
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.service.GenerateGeneralReportService
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel

import javax.inject.Inject

class MarketFlashReportFullListViewModel constructor(application: Application) :
    ApiBaseViewModel<ListAllMarketingFlashReportResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var repository: FlashReportRepository

    @Inject
    lateinit var okHttpClient: OkHttpClient

    val reports = arrayListOf<Any>()

    val page = MutableLiveData<Int>()
    val total = MutableLiveData<Int>()
    val orderCriteria = MutableLiveData<ReportEnum.OrderCriteria>().apply {
        value = ReportEnum.OrderCriteria.DATE_DESC
    }
    val flashReportCountLabel: LiveData<String> = Transformations.map(mainResponse) {
        val totalFlashReport = it?.marketFlashReports?.total ?: 0
        if (totalFlashReport > 0) {
            applicationContext.resources.getQuantityString(
                R.plurals.number_flash_report,
                totalFlashReport,
                NumberUtil.formatThousand(totalFlashReport)
            )
        } else {
            ""
        }
    }

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        val pageIndex = page.value ?: 1
        if (pageIndex == 1) {
            loadFlashReports(pageIndex, orderCriteria.value?.property, orderCriteria.value?.order)
        } else {
            loadMoreFlashReports(
                pageIndex,
                orderCriteria.value?.property,
                orderCriteria.value?.order
            )
        }
    }

    private fun loadFlashReports(page: Int, property: String?, order: String?) {
        performRequest(
            repository.getAllMarketingFlashReport(
                page = page,
                property = property,
                order = order
            )
        )
    }

    private fun loadMoreFlashReports(page: Int, property: String?, order: String?) {
        performNextRequest(
            repository.getAllMarketingFlashReport(
                page = page,
                property = property,
                order = order
            )
        )
    }

    fun downloadReport(report: MarketingFlashReportPO) {
        GenerateGeneralReportService.launch(
            applicationContext,
            report.reportLink,
            ReportEnum.ReportServiceType.FLASH_REPORTS,
            report.title
        )
    }

    fun canLoadNext(): Boolean {
        val size = reports.filterIsInstance<MarketingFlashReportPO>().size
        val total = total.value ?: return false
        return size < total
    }

    override fun shouldResponseBeOccupied(response: ListAllMarketingFlashReportResponse): Boolean {
        return response.marketFlashReports.results.isNotEmpty()
    }
}