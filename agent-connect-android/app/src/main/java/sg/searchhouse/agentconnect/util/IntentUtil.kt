package sg.searchhouse.agentconnect.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Source
import okio.buffer
import okio.sink
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.constant.GoogleEndpoint
import sg.searchhouse.agentconnect.constant.PackageName
import sg.searchhouse.agentconnect.enumeration.app.MimeType
import sg.searchhouse.agentconnect.model.app.DeviceContact
import java.io.File
import java.io.IOException
import java.util.*

object IntentUtil {
    // NOTE: phoneNumber must be in format e.g. `+6587654321` instead of `87654321`
    // May use `StringUtil#getSanitizedMobileNumberWithSgCountryCode`
    fun openWhatsApp(mContext: Context, phoneNumber: String, message: String) {
        mContext.startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$message")
        })
    }

    fun dialPhoneNumber(activity: Activity, phoneNumber: String) {
        if (PermissionUtil.requestCallPermission(activity)) {
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:${phoneNumber}")
                    )
                )
            } catch (e: SecurityException) {
                ErrorUtil.handleError("Security exception when attempt to dial phone number", e)
            }
        }
    }

    fun openPhoneBook(activity: Activity, requestCode: Int) {
        //TODO: don't need permission
        if (PermissionUtil.requestReadContactsPermission(activity)) {
            try {
                //TODO: to enable multi select
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                activity.startActivityForResult(intent, requestCode)
            } catch (e: SecurityException) {
                ErrorUtil.handleError("Security Exception when attempt to open phone book", e)
            }
        }
    }

    @SuppressLint("Recycle")
    fun getDeviceContactListFromIntentData(
        contentResolver: ContentResolver,
        uri: Uri
    ): List<DeviceContact> {
        var contacts = listOf<DeviceContact>()
        contentResolver.query(uri, null, null, null, null)?.let { cursor ->
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNum =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contacts = contacts + DeviceContact(name, phoneNum)
            }
            cursor.close()
        }

        return contacts
    }

    fun sendSms(context: Context, phoneNumber: String, message: String? = null) {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        context.startActivity(intent)
    }

    fun sendEmail(
        context: Context,
        emails: Array<String>,
        body: String? = null,
        subject: String? = null
    ) {
        val msgBody = body ?: ""
        val msgSubject = subject ?: ""

        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, emails)
        intent.putExtra(Intent.EXTRA_SUBJECT, msgSubject)
        intent.putExtra(Intent.EXTRA_TEXT, msgBody)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.label_choose_mail)
            )
        )
    }

    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(intent)
    }

    fun visitPlayStoreApp(context: Context) {
        visitUrl(
            context,
            "${GoogleEndpoint.GOOGLE_PLAY_STORE_URL}${context.packageName}"
        )
    }

    fun encodeAndVisitUrl(context: Context, url: String) {
        visitUrl(context, StringUtil.encodeUrl(url))
    }

    fun visitUrl(context: Context, url: String) {
        try {
            visitValidUrl(context, url)
        } catch (e: IllegalArgumentException) {
            if (url.isEmpty()) {
                ViewUtil.showMessage(R.string.toast_error_missing_url)
            } else {
                ViewUtil.showMessage(context.getString(R.string.toast_error_invalid_url_2, url))
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    fun visitValidUrl(context: Context, url: String) {
        if (!StringUtil.isWebUrlValid(url)) {
            throw IllegalArgumentException("Invalid URL! The input URL is $url.")
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    // Visit url from our backend via external browser
    // `relativeUrl`: Relative URL defined in `constant#Endpoint`, e.g. `/terms-of-use`
    fun visitSrxUrl(context: Context, relativeUrl: String) {
        return visitUrl(context, "${ApiUtil.getBaseUrl(context)}$relativeUrl")
    }

    fun viewPdf(context: Context, fileName: String, directory: String? = null) {
        try {
            viewFile(context, fileName, directory, MimeType.PDF)
        } catch (e: ActivityNotFoundException) {
            showDownloadDefaultPdfViewerDialog(context)
        }
    }

    fun viewCsv(context: Context, fileName: String, directory: String? = null) {
        try {
            viewFile(context, fileName, directory, MimeType.CSV)
        } catch (e: ActivityNotFoundException) {
            showDownloadDefaultCsvViewerDialog(context)
        }
    }

    fun getViewPdfIntent(context: Context, fileName: String, directory: String? = null): Intent {
        return getFileIntent(context, fileName, directory, MimeType.PDF)
    }

    @Throws(ActivityNotFoundException::class)
    fun viewFile(
        context: Context,
        fileName: String,
        directory: String? = null,
        mimeType: MimeType
    ) {
        context.startActivity(getFileIntent(context, fileName, directory, mimeType))
    }

    private fun getFileIntent(
        context: Context,
        fileName: String,
        directory: String? = null,
        mimeType: MimeType
    ): Intent {
        val file = File(context.getExternalFilesDir(directory), fileName)

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, mimeType.value)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        return intent
    }

    private fun showDownloadDefaultPdfViewerDialog(context: Context) {
        DialogUtil(context).showActionDialog(
            R.string.dialog_message_download_pdf_viewer,
            R.string.dialog_title_download_pdf_viewer
        ) {
            visitUrl(context, AppConstant.URL_DEFAULT_PDF_READER)
        }
    }

    private fun showDownloadDefaultCsvViewerDialog(context: Context) {
        DialogUtil(context).showActionDialog(
            R.string.dialog_message_csv_viewer_not_found,
            R.string.dialog_title_csv_viewer_not_found
        ) {
            visitUrl(context, AppConstant.URL_GOOGLE_PLAY_SPREADSHEET)
        }
    }

    @Throws(IOException::class)
    @WorkerThread
    fun downloadFile(
        context: Context,
        okHttpClient: OkHttpClient,
        url: String,
        fileName: String,
        directoryName: String? = null
    ): String {
        val request = Request.Builder().url(url).get().build()
        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Download file $url failed!")
        }
        val directory = context.getExternalFilesDir(directoryName)
        val file = File(directory, fileName)
        // Assume override
        if (file.exists()) {
            file.delete()
        }
        response.body?.source().use { bufferedSource ->
            val bufferedSink = file.sink().buffer()
            bufferedSink.writeAll(bufferedSource as Source)
            bufferedSink.close()
        }
        return file.path
    }

    fun getSmsIntent(): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:")
        return intent
    }

    /**
     * Return SMS recipients delimiter according to applications. Most of them user the default ";".
     *
     * @param packageName the package name of a SMS application.
     * @return the delimiter.
     */
    fun getSmsRecipientDelimiter(packageName: String): String {
        var delimiter = ";"

        /* For SAMSUNG phone. */
        if (Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("samsung")) {
            /* The SAMSUNG native android SMS application use "," as delimiter. */
            if (packageName == PackageName.NATIVE_SMS) {
                delimiter = ","
            }
        }

        return delimiter
    }

    fun startService(context: Context, serviceClass: Class<*>, extras: Bundle? = null) {
        val intent = Intent(context, serviceClass)
        extras?.let { intent.putExtras(it) }
        context.startService(intent)
    }

    fun shareTwitter(context: Context, text: String) {
        val url =
            "https://twitter.com/intent/tweet?source=webclient&text=${text}"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }


    fun openGallery(activity: Activity, requestCode: Int, allowMultiple: Boolean = true) {
        if (PermissionUtil.requestReadWriteExternalStoragePermission(activity)) {
            try {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
                intent.action = Intent.ACTION_GET_CONTENT
                activity.startActivityForResult(
                    Intent.createChooser(
                        intent, activity.resources.getString(R.string.label_choose_picture)
                    ),
                    requestCode
                )
            } catch (e: SecurityException) {
                ErrorUtil.handleError("Security exception when attempt to open gallery", e)
            }
        }
    }

    /**
     * Reference:
     * https://stackoverflow.com/questions/10377783/low-picture-image-quality-when-capture-from-camera
     */
    fun openCamera(activity: Activity, requestCode: Int, imageFileUri: Uri? = null) {
        if (PermissionUtil.requestReadWriteExternalStoragePermission(activity) &&
            PermissionUtil.requestCameraPermission(activity)
        ) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                imageFileUri?.run {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, this)
                }
                activity.startActivityForResult(intent, requestCode)
            } catch (e: SecurityException) {
                ErrorUtil.handleError("Security exception when attempt to open gallery", e)
            }
        }
    }
}