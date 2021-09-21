package sg.searchhouse.agentconnect.viewmodel.fragment.listing.user

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.NotificationUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import java.io.IOException
import javax.inject.Inject

class FeaturedListingTypesDialogViewModel constructor(application: Application) :
    CoreViewModel(application) {
    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
    }

    @Throws(IOException::class)
    fun downloadProductInfoSheet(
        fileName: String,
        directory: String,
        onFileDownloaded: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val notificationId = NotificationUtil.showIndeterminateProgressNotification(
                applicationContext,
                title = applicationContext.getString(R.string.notification_download_file_in_progress),
                content = null
            )
            withContext(Dispatchers.IO) {
                IntentUtil.downloadFile(
                    applicationContext,
                    okHttpClient,
                    AppConstant.URL_FEATURED_LISTING_TYPES,
                    fileName,
                    directory
                )
            }
            NotificationUtil.dismiss(applicationContext, notificationId)
            NotificationUtil.showNotification(
                applicationContext,
                R.string.notification_download_featured_listing_success,
                R.string.notification_download_featured_listing_content_success,
                pendingIntent = getViewFilePendingIntent(fileName, directory)
            )
            onFileDownloaded.invoke()
        }
    }

    private fun getViewFilePendingIntent(fileName: String, directory: String): PendingIntent {
        val intent =
            IntentUtil.getViewPdfIntent(
                applicationContext,
                fileName,
                directory
            )
        return PendingIntent.getActivity(applicationContext, 0, intent, 0)
    }
}
