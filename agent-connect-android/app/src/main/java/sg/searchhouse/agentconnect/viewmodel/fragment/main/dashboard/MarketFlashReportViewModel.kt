package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.service.GenerateGeneralReportService
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class MarketFlashReportViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var okHttpClient: OkHttpClient

    val marketingFlashReportPO = MutableLiveData<MarketingFlashReportPO>()

    init {
        viewModelComponent.inject(this)
    }

    fun downloadReport() {
        val report = marketingFlashReportPO.value ?: return
        GenerateGeneralReportService.launch(
            applicationContext,
            report.reportLink,
            ReportEnum.ReportServiceType.FLASH_REPORTS,
            report.title
        )
    }
}