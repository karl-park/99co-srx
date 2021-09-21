package sg.searchhouse.agentconnect.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.HomeReportRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.app.SrxNotificationChannel
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.homereport.GenerateHomeReportRequestBody
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.ExistingHomeReport
import sg.searchhouse.agentconnect.event.homereport.UpdateHomeReportUsageStatsEvent
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.HomeReportUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.NotificationUtil
import java.io.IOException
import javax.inject.Inject

// TODO Remove bottom bar related code
class GenerateHomeReportService : BaseIntentService("GenerateHomeReportService") {
    @Inject
    lateinit var homeReportRepository: HomeReportRepository

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private lateinit var requestBody: GenerateHomeReportRequestBody

    // For save history and refill form
    private lateinit var walkupResponseData: SearchWithWalkupResponse.Data

    // For save history and refill form
    private lateinit var details: ExistingHomeReport.Details

    override fun onCreate() {
        super.onCreate()
        serviceComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        requestBody =
            intent?.getSerializableExtra(EXTRA_REQUEST_BODY) as GenerateHomeReportRequestBody?
                ?: throw IllegalArgumentException("Missing `GenerateHomeReportRequestBody` in `GenerateHomeReportService`")
        walkupResponseData =
            intent?.getSerializableExtra(EXTRA_WALKUP_RESPONSE) as SearchWithWalkupResponse.Data?
                ?: throw IllegalArgumentException("Missing `walkupResponse` in `GenerateHomeReportService`")
        details =
            intent?.getSerializableExtra(EXTRA_DETAILS) as ExistingHomeReport.Details?
                ?: throw IllegalArgumentException("Missing `details` in `GenerateHomeReportService`")

        generateReport()
    }

    private fun generateReport() {
        requestBody.run {
            @Suppress("ConstantConditionIf")
            val notificationId = if (isEnableNotification) {
                showProgressNotification(
                    R.string.notification_generate_home_report_in_progress,
                    streetName
                )
            } else {
                null
            }

            homeReportRepository.generateHomeReport(
                id,
                agency,
                block,
                cdResearchSubtype,
                unit,
                streetKey,
                clientName
            ).performRequest(
                applicationContext,
                onSuccess = { response ->
                    notificationId?.run { dismissNotification(this) }
                    downloadReport(response.results.extractedLink, streetName)
                },
                onFail = {
                    notificationId?.run { dismissNotification(this) }
                    showFailNotification(
                        R.string.notification_generate_home_report_fail,
                        streetName
                    )
                },
                onError = {
                    notificationId?.run { dismissNotification(this) }
                    showFailNotification(
                        R.string.notification_generate_home_report_error,
                        streetName
                    )
                })

            CoroutineScope(Dispatchers.IO).launch {
                delay(DELAY_UPDATE_USAGE_STATS)
                RxBus.publish(UpdateHomeReportUsageStatsEvent())
            }
        }
    }

    private fun showProgressNotification(@StringRes contentResId: Int, streetName: String): Int {
        return showCustomIndeterminateProgressNotification(
            applicationContext,
            title = applicationContext.getString(contentResId),
            content = streetName
        )
    }

    private fun showCustomIndeterminateProgressNotification(
        context: Context,
        title: String?,
        content: String?,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ): Int {
        val remoteView =
            RemoteViews(context.packageName, R.layout.layout_notification_home_report_progress)
        remoteView.setTextViewText(R.id.tv_title, title)
        remoteView.setTextViewText(R.id.tv_content, content)
        return NotificationUtil.showCustomNotification(
            context,
            remoteView,
            notificationChannel,
            priority
        )
    }

    private fun showCustomResultNotification(
        context: Context,
        title: String?,
        content: String?,
        pendingIntent: PendingIntent
    ): Int {
        val remoteView =
            RemoteViews(context.packageName, R.layout.layout_notification_home_report_result)
        remoteView.setTextViewText(R.id.tv_title, title)
        remoteView.setTextViewText(R.id.tv_content, content)

        remoteView.setOnClickPendingIntent(R.id.btn_view, pendingIntent)

        return NotificationUtil.showCustomNotification(
            context,
            remoteView,
            pendingIntent = pendingIntent
        )
    }

    private fun showCustomFailNotification(
        context: Context,
        title: String?,
        content: String?,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ): Int {
        val remoteView =
            RemoteViews(context.packageName, R.layout.layout_notification_home_report_fail)
        remoteView.setTextViewText(R.id.tv_title, title)
        remoteView.setTextViewText(R.id.tv_content, content)
        return NotificationUtil.showCustomNotification(
            context,
            remoteView,
            notificationChannel,
            priority
        )
    }

    private fun dismissNotification(notificationId: Int) {
        @Suppress("ConstantConditionIf")
        if (!isEnableNotification) return

        NotificationUtil.dismiss(applicationContext, notificationId)
    }

    private fun showFailNotification(@StringRes title: Int, streetName: String) {
        @Suppress("ConstantConditionIf")
        if (!isEnableNotification) return

        showCustomFailNotification(
            applicationContext,
            getString(title),
            streetName
        )
    }

    fun downloadReport(url: String, streetName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            @Suppress("ConstantConditionIf")
            val notificationId = if (isEnableNotification) {
                showProgressNotification(
                    R.string.notification_download_home_report_in_progress,
                    streetName
                )
            } else {
                null
            }

            val fileName = async(Dispatchers.IO) {
                val timeStamp = DateTimeUtil.getCurrentFileNameTimeStamp()
                val fileName = "home_report_$timeStamp.pdf"

                try {
                    IntentUtil.downloadFile(
                        applicationContext, okHttpClient, url, fileName,
                        AppConstant.DIR_HOME_REPORT
                    )
                } catch (e: IOException) {
                    return@async null
                }

                HomeReportUtil.saveNewHomeReport(
                    streetName,
                    url,
                    fileName,
                    requestBody,
                    walkupResponseData,
                    details
                )
                fileName
            }

            fileName.await()?.run {
                notificationId?.run { dismissNotification(this) }
                showReportReadyNotification(streetName, this)
            } ?: run {
                notificationId?.run { dismissNotification(this) }
                showFailNotification(R.string.notification_download_home_report_error, streetName)
            }
        }
    }

    private fun showReportReadyNotification(streetName: String, fileName: String) {
        @Suppress("ConstantConditionIf")
        if (isEnableNotification) {
            showCustomResultNotification(
                applicationContext,
                applicationContext.getString(R.string.notification_download_home_report_success),
                streetName,
                getViewReportPendingIntent(fileName)
            )
        }
    }

    private fun getViewReportPendingIntent(fileName: String): PendingIntent {
        val intent =
            IntentUtil.getViewPdfIntent(applicationContext, fileName, AppConstant.DIR_HOME_REPORT)
        return PendingIntent.getActivity(applicationContext, 0, intent, 0)
    }

    companion object {
        // TODO Remove notification module if confirmed not required
        private const val isEnableNotification = true

        private const val EXTRA_REQUEST_BODY = "EXTRA_REQUEST_BODY"
        private const val EXTRA_WALKUP_RESPONSE = "EXTRA_WALKUP_RESPONSE"
        private const val EXTRA_DETAILS = "EXTRA_DETAILS"

        private const val DELAY_UPDATE_USAGE_STATS: Long = 3000

        fun launch(
            context: Context,
            requestBody: GenerateHomeReportRequestBody,
            walkupResponse: SearchWithWalkupResponse.Data,
            details: ExistingHomeReport.Details
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_REQUEST_BODY, requestBody)
            extras.putSerializable(EXTRA_WALKUP_RESPONSE, walkupResponse)
            extras.putSerializable(EXTRA_DETAILS, details)
            IntentUtil.startService(
                context.applicationContext,
                GenerateHomeReportService::class.java,
                extras
            )
        }
    }
}