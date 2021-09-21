package sg.searchhouse.agentconnect.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.NotificationUtil
import java.io.IOException
import java.lang.IllegalArgumentException
import javax.inject.Inject

class GenerateGeneralReportService : BaseIntentService("GenerateGeneralReportService") {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        serviceComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        val reportUrl = intent?.getStringExtra(EXTRA_KEY_REPORT_URL)
            ?: throw IllegalArgumentException("Missing report url in service")
        val reportType =
            intent.getSerializableExtra(EXTRA_KEY_REPORT_TYPE) as ReportEnum.ReportServiceType?
                ?: throw IllegalArgumentException("Missing report type in generate general service")
        val reportName = intent.getStringExtra(EXTRA_KEY_REPORT_NAME)
            ?: throw IllegalArgumentException("Missing report name in generate general service")
        downloadReport(reportUrl, reportType, reportName)
    }

    fun downloadReport(
        reportUrl: String,
        reportType: ReportEnum.ReportServiceType,
        reportName: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val notificationId = NotificationUtil.showIndeterminateProgressNotification(
                applicationContext,
                title = applicationContext.getString(
                    reportType.downloadProgressMessage,
                    reportName
                ),
                content = null
            )

            val fileName = async(Dispatchers.IO) {
                val fileName = when {
                    reportType.initialFileName != null -> {
                        val timeStamp = DateTimeUtil.getCurrentFileNameTimeStamp()
                        "${reportType.initialFileName}${timeStamp}.pdf"
                    }
                    reportType.fullFileName != null -> {
                        reportType.fullFileName
                    }
                    else -> {
                        throw Throwable("Missing file name in generate general report service class")
                    }
                }

                try {
                    IntentUtil.downloadFile(
                        applicationContext,
                        okHttpClient,
                        url = reportUrl,
                        fileName = fileName,
                        directoryName = reportType.dir
                    )
                } catch (e: IOException) {
                    return@async null
                }
                return@async fileName
            }

            fileName.await()?.run {
                NotificationUtil.dismiss(applicationContext, notificationId)
                return@run NotificationUtil.showNotification(
                    applicationContext,
                    applicationContext.getString(reportType.downloadSuccessMessage, reportName),
                    applicationContext.getString(R.string.msg_click_to_view_pdf),
                    pendingIntent = getViewReportPendingIntent(this, reportType.dir)
                )
            } ?: run {
                NotificationUtil.dismiss(applicationContext, notificationId)
                showFailNotification(reportType.failedErrorMessage, reportName)
            }

        }
    }

    private fun getViewReportPendingIntent(fileName: String, directory: String): PendingIntent {
        val intent =
            IntentUtil.getViewPdfIntent(
                applicationContext,
                fileName,
                directory
            )
        return PendingIntent.getActivity(applicationContext, 0, intent, 0)
    }

    private fun showFailNotification(@StringRes title: Int, reportName: String) {
        applicationContext?.run {
            NotificationUtil.showNotification(
                this,
                getString(title, reportName),
                getString(R.string.error_generic_contact_srx)
            )
        }
    }


    companion object {
        private const val EXTRA_KEY_REPORT_URL = "EXTRA_KEY_REPORT_URL"
        private const val EXTRA_KEY_REPORT_NAME = "EXTRA_KEY_REPORT_NAME"
        private const val EXTRA_KEY_REPORT_TYPE = "EXTRA_KEY_REPORT_TYPE"

        fun launch(
            context: Context,
            reportUrl: String,
            reportType: ReportEnum.ReportServiceType,
            reportName: String
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_KEY_REPORT_URL, reportUrl)
            extras.putSerializable(EXTRA_KEY_REPORT_TYPE, reportType)
            extras.putSerializable(EXTRA_KEY_REPORT_NAME, reportName)
            IntentUtil.startService(
                context.applicationContext,
                GenerateGeneralReportService::class.java,
                extras
            )
        }
    }
}