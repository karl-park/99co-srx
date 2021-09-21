package sg.searchhouse.agentconnect.util

import android.content.Context
import androidx.annotation.StringRes
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.BuildConfig
import sg.searchhouse.agentconnect.R

object ErrorUtil {
    fun handleError(
        context: Context, @StringRes errorMessageResId: Int, endpoint: String? = null,
        isShowToast: Boolean = true
    ) {
        handleError(context.getString(errorMessageResId), endpoint, isShowToast)
    }

    fun handleError(
        errorMessage: String, throwable: Throwable, endpoint: String? = null,
        isShowToast: Boolean = true
    ) {
        handleErrorCore(errorMessage, throwable, endpoint, isShowToast = isShowToast)
    }

    fun handleError(
        context: Context,
        @StringRes errorMessageResId: Int,
        throwable: Throwable,
        endpoint: String? = null,
        errorBody: String? = null,
        httpCode: Int? = null,
        isShowToast: Boolean = true
    ) {
        handleErrorCore(
            context.getString(errorMessageResId),
            throwable,
            endpoint,
            errorBody,
            httpCode, isShowToast
        )
    }

    fun handleError(errorMessage: String, endpoint: String? = null, isShowToast: Boolean = true) {
        handleErrorCore(errorMessage, null, endpoint, isShowToast = isShowToast)
    }

    fun handleError(
        context: Context,
        errorMessage: String?,
        throwable: Throwable,
        endpoint: String? = null,
        isShowToast: Boolean = true
    ) {
        val thisErrorMessage = errorMessage ?: context.getString(R.string.error_empty_message)
        handleErrorCore(thisErrorMessage, throwable, endpoint, isShowToast = isShowToast)
    }

    private fun handleErrorCore(
        errorMessage: String,
        throwable: Throwable?,
        endpoint: String? = null,
        errorBody: String? = null,
        httpCode: Int? = null,
        isShowToast: Boolean
    ) {
        if (isShowToast) {
            showToast(errorMessage)
        }
        logStackTrace(errorMessage, throwable, endpoint, errorBody, httpCode)
    }

    private fun logStackTrace(
        errorMessage: String,
        throwable: Throwable?,
        endpoint: String? = null,
        errorBody: String? = null,
        httpCode: Int? = null
    ) {
        throwable?.let {
            logStackTraceCore(it, endpoint, errorBody, httpCode)
        } ?: run {
            // Enable inspect error
            try {
                throw Exception(errorMessage)
            } catch (e: java.lang.Exception) {
                logStackTraceCore(e, endpoint, errorBody, httpCode)
            }
        }
    }

    private fun showToast(errorMessage: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (BuildConfig.DEBUG) {
                ViewUtil.showMessage(errorMessage)
            } else {
                ViewUtil.showMessage(R.string.error_generic_contact_srx)
            }
        }
    }

    // Log stack trace into console and logging app (when available)
    private fun logStackTraceCore(
        throwable: Throwable,
        endpoint: String? = null,
        errorBody: String? = null,
        httpCode: Int? = null
    ) {
        print(throwable.printStackTrace())
        if (!BuildConfig.DEBUG) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.recordException(throwable)

            val email = SessionUtil.getCurrentUser()?.email ?: "(Not logged in)"
            crashlytics.log(throwable.printStackTrace().toString())

            crashlytics.log("Email = $email")
            endpoint?.run { crashlytics.log("Endpoint = $this") }
            errorBody?.run { crashlytics.log("Error body = $this") }
            httpCode?.run { crashlytics.log("Http code = $this") }
        }
    }
}