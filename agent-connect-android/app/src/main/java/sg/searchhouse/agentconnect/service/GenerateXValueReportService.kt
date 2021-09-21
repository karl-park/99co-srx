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
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.XValueRepository
import sg.searchhouse.agentconnect.model.api.xvalue.GenerateXValueReportRequestBody
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.NotificationUtil
import java.io.IOException
import javax.inject.Inject

class GenerateXValueReportService : BaseIntentService("GenerateXValueReportService") {
    @Inject
    lateinit var xValueRepository: XValueRepository

    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        serviceComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        val requestBody =
            intent?.getSerializableExtra(EXTRA_REQUEST_BODY) as GenerateXValueReportRequestBody?
                ?: throw IllegalArgumentException("Missing generate X value report request body in service")
        generateReport(requestBody)
    }

    private fun generateReport(requestBody: GenerateXValueReportRequestBody) {
        requestBody.run {
            val notificationId = NotificationUtil.showIndeterminateProgressNotification(
                applicationContext,
                title = applicationContext.getString(
                    R.string.notification_generate_x_value_report_in_progress,
                    shortAddress
                ),
                content = null
            )

            ApiUtil.performRequest(
                applicationContext,
                xValueRepository.generateXvaluePropertyReport(
                    requestId = srxValuationRequestId,
                    crunchResearchStreetId = crunchResearchStreetId,
                    showFullReport = showFullReport
                ),
                onSuccess = { response ->
                    NotificationUtil.dismiss(applicationContext, notificationId)
                    downloadReport(response.url, shortAddress)
                },
                onFail = {
                    NotificationUtil.dismiss(applicationContext, notificationId)
                    showFailNotification(
                        R.string.notification_generate_x_value_report_fail,
                        shortAddress
                    )
                },
                onError = {
                    NotificationUtil.dismiss(applicationContext, notificationId)
                    showFailNotification(
                        R.string.notification_generate_x_value_report_error,
                        shortAddress
                    )
                })
        }
    }

    private fun showFailNotification(@StringRes title: Int, streetName: String) {
        applicationContext?.run {
            NotificationUtil.showNotification(
                this,
                getString(title, streetName),
                getString(R.string.error_generic_contact_srx)
            )
        }
    }

    fun downloadReport(url: String, streetName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val notificationId = NotificationUtil.showIndeterminateProgressNotification(
                applicationContext,
                title = applicationContext.getString(
                    R.string.notification_download_x_value_report_in_progress,
                    streetName
                ),
                content = null
            )

            val fileName = async(Dispatchers.IO) {
                val timeStamp = DateTimeUtil.getCurrentFileNameTimeStamp()
                val fileName = "x_value_report_$timeStamp.pdf"

                try {
                    IntentUtil.downloadFile(
                        applicationContext, okHttpClient, url, fileName,
                        AppConstant.DIR_X_VALUE_REPORT
                    )
                } catch (e: IOException) {
                    return@async null
                }

                fileName
            }

            fileName.await()?.run {
                NotificationUtil.dismiss(applicationContext, notificationId)
                NotificationUtil.showNotification(
                    applicationContext,
                    applicationContext.getString(
                        R.string.notification_download_x_value_report_success,
                        streetName
                    ),
                    applicationContext.getString(R.string.notification_download_x_value_report_content_success),
                    pendingIntent = getViewReportPendingIntent(this)
                )
            } ?: run {
                NotificationUtil.dismiss(applicationContext, notificationId)
                showFailNotification(
                    R.string.notification_download_x_value_report_error,
                    streetName
                )
            }
        }
    }

    private fun getViewReportPendingIntent(fileName: String): PendingIntent {
        val intent =
            IntentUtil.getViewPdfIntent(
                applicationContext,
                fileName,
                AppConstant.DIR_X_VALUE_REPORT
            )
        return PendingIntent.getActivity(applicationContext, 0, intent, 0)
    }

    companion object {
        private const val EXTRA_REQUEST_BODY = "EXTRA_REQUEST_BODY"

        fun launch(
            context: Context,
            shortAddress: String,
            srxValuationRequestId: Int,
            crunchResearchStreetId: Int
        ) {
            val requestBody = GenerateXValueReportRequestBody(
                shortAddress,
                srxValuationRequestId,
                crunchResearchStreetId
            )

            val extras = Bundle()
            extras.putSerializable(EXTRA_REQUEST_BODY, requestBody)
            IntentUtil.startService(
                context.applicationContext,
                GenerateXValueReportService::class.java,
                extras
            )
        }
    }
}