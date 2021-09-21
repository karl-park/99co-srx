package sg.searchhouse.agentconnect.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import sg.searchhouse.agentconnect.BuildConfig
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.constant.AppConstant.SERVER_INSTANCE_CUSTOM
import sg.searchhouse.agentconnect.constant.AppConstant.SERVER_INSTANCE_DEFAULT
import sg.searchhouse.agentconnect.constant.AppConstant.SERVER_INSTANCE_PRODUCTION
import sg.searchhouse.agentconnect.constant.AppConstant.SERVER_INSTANCE_STAGING
import sg.searchhouse.agentconnect.constant.ErrorCode
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_WHICH_SERVER
import sg.searchhouse.agentconnect.model.api.ApiError
import sg.searchhouse.agentconnect.model.app.Domain
import java.io.IOException
import java.net.HttpURLConnection.*

object ApiUtil {
    //Base URL
    fun getBaseUrl(context: Context, isRunningTest: Boolean = false): String {
        return if (!isRunningTest) {
            when (Prefs.getInt(PREF_WHICH_SERVER, SERVER_INSTANCE_DEFAULT)) {
                // For manual toggle base URL
                SERVER_INSTANCE_STAGING -> context.getString(R.string.base_url_staging)
                SERVER_INSTANCE_PRODUCTION -> context.getString(R.string.base_url_production)
                SERVER_INSTANCE_CUSTOM -> getDebugBaseUrl(context)
                else -> getDefaultBaseUrl(context)
            }
        } else {
            AppConstant.BASE_URL_TEST
        }
    }

    // 99.co Base URL
    fun get99BaseUrl(context: Context, isRunningTest: Boolean = false): String {
        return if (!isRunningTest) {
            when (Prefs.getInt(PREF_WHICH_SERVER, SERVER_INSTANCE_DEFAULT)) {
                // For manual toggle base URL
                SERVER_INSTANCE_STAGING -> context.getString(R.string.base_url_99_staging)
                SERVER_INSTANCE_PRODUCTION -> context.getString(R.string.base_url_99_production)
                SERVER_INSTANCE_CUSTOM -> getDebug99BaseUrl(context)
                else -> getDefault99BaseUrl(context)
            }
        } else {
            // TODO Assign 99 test URL when needed
            AppConstant.BASE_URL_TEST
        }
    }

    //For Street Sine user
    private fun getDebug99BaseUrl(context: Context): String {
        val srxUrl = SessionUtil.getDebugServerBaseUrl()
        return context.getString(when (srxUrl) {
            context.getString(R.string.base_url_production) -> R.string.base_url_99_production
            else -> R.string.base_url_99_staging
        })
    }

    // TODO Should throw exception when `SessionUtil#getDebugServerBaseUrl` not available
    //For Street Sine user
    private fun getDebugBaseUrl(context: Context): String {
        return SessionUtil.getDebugServerBaseUrl()
            ?: getDefaultBaseUrl(context)
    }

    private fun getDefaultBaseUrl(context: Context): String {
        return if (PackageUtil.isStagingVersion()) {
            // Whenever you append -staging to the app name,
            // it will not point to production
            context.getString(R.string.base_url_staging)
        } else {
            // Depends on build type
            // Debug build -> Staging
            // Release build -> Production
            context.getString(R.string.base_url)
        }
    }

    private fun getDefault99BaseUrl(context: Context): String {
        return if (PackageUtil.isStagingVersion()) {
            // Whenever you append -staging to the app name,
            // it will not point to production
            context.getString(R.string.base_url_99_staging)
        } else {
            // Depends on build type
            // Debug build -> Staging
            // Release build -> Production
            context.getString(R.string.base_url_99)
        }
    }

    //Api Error
    @Throws(JsonSyntaxException::class, NullPointerException::class, IllegalStateException::class)
    fun <E> parseError(errorString: String?, eClass: Class<E>): E {
        return Gson().fromJson(errorString, eClass)
            ?: throw NullPointerException("Null error object!")
    }

    // Perform request for usual SRX endpoints with standard error of class `ApiError`
    fun <T> performRequest(
        context: Context,
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onFail: (ApiError) -> Unit,
        onError: () -> Unit,
        enablePrintRequestBody: Boolean = true,
        isShowErrorToast: Boolean = true
    ) {
        performRequestCore(
            context,
            call,
            onSuccess,
            onFail,
            onError,
            enablePrintRequestBody,
            ApiError::class.java,
            isShowErrorToast
        )
    }

    // Perform request for endpoint with different definition of `errorClass`
    fun <T, E> performExternalRequest(
        context: Context,
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        enablePrintRequestBody: Boolean = true,
        errorClass: Class<E>,
        isShowErrorToast: Boolean = true
    ) {
        performRequestCore(
            context,
            call,
            onSuccess,
            onFail,
            onError,
            enablePrintRequestBody,
            errorClass = errorClass,
            isShowErrorToast = isShowErrorToast
        )
    }

    // Convenient wrapper to make API call
    // When un-expected exception* happened, this method already handle for you. You no need to show error message.
    // un-expected exception*: Exceptions that not returned by the server result
    private fun <T, E> performRequestCore(
        context: Context,
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        enablePrintRequestBody: Boolean = true,
        errorClass: Class<E>,
        isShowErrorToast: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                executeCall(context, call, onError, enablePrintRequestBody) ?: return@launch
            maybePrintResponse(call.request(), response)
            handleResponse(
                context,
                response,
                onSuccess,
                onFail,
                onError,
                errorClass,
                isShowErrorToast
            )
        }
    }

    // Convenient wrapper to make multiple API calls
    // It is not lenient, i.e. only return success when all calls are success
    // When un-expected exception* happened, this method already handle for you. You no need to show error message.
    // un-expected exception*: Exceptions that not returned by the server result
    //
    // Create `performExternalRequests` using `performRequestsCore` with different error class if necessary
    fun <T> performRequests(
        context: Context,
        calls: List<Call<T>>,
        onSuccess: (List<T>) -> Unit,
        onFail: (ApiError) -> Unit,
        onError: () -> Unit,
        enablePrintRequestBody: Boolean = true,
        isShowErrorToast: Boolean = true
    ) {
        performRequestsCore(
            context,
            calls,
            onSuccess,
            onFail,
            onError,
            enablePrintRequestBody,
            ApiError::class.java,
            isShowErrorToast
        )
    }

    private fun <T, E> performRequestsCore(
        context: Context,
        calls: List<Call<T>>,
        onSuccess: (List<T>) -> Unit,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        enablePrintRequestBody: Boolean = true,
        errorClass: Class<E>,
        isShowErrorToast: Boolean = true
    ) {
        if (calls.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            val responses = calls.map { call ->
                executeCall(context, call, onError, enablePrintRequestBody) ?: return@launch
            }
            val bodies = responses.map { response ->
                if (response.isSuccessful) {
                    getSuccessBodyOrHandleError(context, response, onError, isShowErrorToast)
                        ?: return@launch
                } else {
                    return@launch handleFailAndError(
                        context,
                        response,
                        onFail,
                        onError,
                        errorClass
                    )
                }
            }
            onSuccess.invoke(bodies)
        }
    }

    // Convenient wrapper to make multiple API calls, lenient version
    // When fail, this method will not show any error, instead only return empty item(s) in the list of response
    fun <T> performRequestsLenient(
        context: Context,
        calls: List<Call<T>>,
        onSuccess: (List<T?>) -> Unit,
        enablePrintRequestBody: Boolean = true,
        isShowErrorToast: Boolean = true
    ) {
        if (calls.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            val responses = calls.map { call ->
                executeCall(context, call, onError = {
                    // Do nothing
                }, enablePrintRequestBody = enablePrintRequestBody)
            }
            val bodies = responses.map { response ->
                if (response?.isSuccessful == true) {
                    getSuccessBodyOrHandleError(context, response, onError = {
                        // Do nothing
                    }, isShowErrorToast = isShowErrorToast)
                } else {
                    null
                }
            }
            onSuccess.invoke(bodies)
        }
    }

    // Convenient wrapper to make multiple API calls, lenient version
    // When fail, this method will not show any error, instead only return empty item(s) in the list of response
    // `onItemSuccess` is triggered one by one instead of after all completed
    fun <T> performRequestsLenientOneByOne(
        context: Context,
        calls: List<Call<T>>,
        onItemSuccess: (List<T?>) -> Unit,
        enablePrintRequestBody: Boolean = true,
        isShowErrorToast: Boolean = true
    ) {
        if (calls.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            var resultResponses: List<T?> = calls.map { null }
            calls.mapIndexed { index, call ->
                val response = executeCall(context, call, onError = {
                    // Do nothing
                }, enablePrintRequestBody = enablePrintRequestBody)
                if (response?.isSuccessful == true) {
                    resultResponses = resultResponses.mapIndexed { thisIndex, t ->
                        when (thisIndex) {
                            index -> getSuccessBodyOrHandleError(context, response, onError = {
                                // Do nothing
                            }, isShowErrorToast = isShowErrorToast)
                            else -> t
                        }
                    }
                    onItemSuccess.invoke(resultResponses)
                } else {
                    onItemSuccess.invoke(resultResponses)
                }
            }
        }
    }

    // Set enablePrintRequestBody as false if request body causes ConcurrentModificationException
    // e.g. when contain bulky file
    private fun <T> executeCall(
        context: Context,
        call: Call<T>,
        onError: () -> Unit,
        enablePrintRequestBody: Boolean = true
    ): Response<T>? {
        // Print body for POST request
        if (enablePrintRequestBody) {
            maybePrintRequestBody(call)
        }

        return try {
            call.execute()
        } catch (e: JsonSyntaxException) {
            ErrorUtil.handleError(context, R.string.exception_fail_parse_success_response, e)
            onError.invoke()
            null
        } catch (e: IOException) {
            onError.invoke()
            null
        }
    }

    private fun <T, E> handleResponse(
        context: Context, response: Response<T>, onSuccess: (T) -> Unit,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        errorClass: Class<E>,
        isShowErrorToast: Boolean
    ) {
        if (response.isSuccessful) {
            handleSuccess(context, response, onSuccess, onError, isShowErrorToast)
        } else {
            handleFailAndError(context, response, onFail, onError, errorClass, isShowErrorToast)
        }
    }

    // Print response in JSON format
    private fun <T> maybePrintResponse(request: Request, response: Response<T>) {
        if (!BuildConfig.DEBUG) return
        val jsonResponse = Gson().toJson(response.body())
        println("okhttp Response of ${getRequestName(request)}:")
        // Android Studio log cut off response if more than 4000, hence got to chunk
        jsonResponse.chunked(3000).map {
            println(it)
        }
    }

    private fun getRequestName(request: Request): String {
        val url = request.url
        val method = request.method
        return "$method $url"
    }

    private fun <T> handleSuccess(
        context: Context,
        response: Response<T>,
        onSuccess: (T) -> Unit,
        onError: () -> Unit,
        isShowErrorToast: Boolean
    ) {
        val successBody = getSuccessBodyOrHandleError(
            context,
            response,
            onError,
            isShowErrorToast = isShowErrorToast
        )
        if (successBody != null) {
            onSuccess.invoke(successBody)
        }
    }

    private fun <T> getSuccessBodyOrHandleError(
        context: Context,
        response: Response<T>,
        onError: () -> Unit,
        isShowErrorToast: Boolean
    ): T? {
        val body = response.body()
        return if (body != null) {
            body
        } else {
            // Success but body empty (unexpected)
            ErrorUtil.handleError(
                context, R.string.exception_empty_response_body,
                isShowToast = isShowErrorToast
            )
            onError.invoke()
            null
        }
    }

    private fun <T> getEndpoint(response: Response<T>): String {
        val url = response.raw().request.url
        val method = response.raw().request.method
        return "$method $url"
    }

    private fun <T, E> handleFailAndError(
        context: Context,
        response: Response<T>,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        errorClass: Class<E>,
        isShowErrorToast: Boolean = true
    ) {
        val endpoint = getEndpoint(response)
        when (val httpCode = response.code()) {
            HTTP_NOT_FOUND -> {
                ErrorUtil.handleError(
                    context,
                    R.string.exception_page_not_found,
                    endpoint,
                    isShowToast = isShowErrorToast
                )
                onError.invoke()
            }
            HTTP_FORBIDDEN -> {
                ErrorUtil.handleError(
                    context,
                    R.string.exception_forbidden,
                    endpoint,
                    isShowToast = isShowErrorToast
                )
                onError.invoke()
            }
            HTTP_BAD_REQUEST -> {
                handleFailAndErrorBadRequest(
                    context,
                    httpCode,
                    endpoint,
                    onFail,
                    onError,
                    response,
                    errorClass,
                    isShowErrorToast
                )
            }
            HTTP_INTERNAL_ERROR -> {
                ErrorUtil.handleError(
                    context,
                    R.string.exception_internal_server_error,
                    endpoint, isShowToast = isShowErrorToast
                )
                onError.invoke()
            }
            HTTP_UNAVAILABLE -> {
                ErrorUtil.handleError(
                    context,
                    R.string.exception_server_unavailable,
                    endpoint, isShowToast = isShowErrorToast
                )
                onError.invoke()
            }
            HTTP_NOT_IMPLEMENTED, HTTP_BAD_GATEWAY, HTTP_GATEWAY_TIMEOUT, HTTP_VERSION -> {
                val errorMessage = context.getString(
                    R.string.exception_server_error,
                    response.code().toString()
                )
                ErrorUtil.handleError(errorMessage, endpoint, isShowToast = isShowErrorToast)
                onError.invoke()
            }
            else -> {
                handleFailAndErrorGeneric(
                    context,
                    httpCode,
                    endpoint,
                    onFail,
                    onError,
                    response,
                    errorClass,
                    isShowErrorToast
                )
            }
        }
    }

    private fun <T, E> handleFailAndErrorGeneric(
        context: Context,
        httpCode: Int,
        endpoint: String,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        response: Response<T>,
        errorClass: Class<E>,
        isShowErrorToast: Boolean = true
    ) {
        val error: E
        val errorBodyString = response.errorBody()?.string()
        try {
            error = parseError(errorBodyString, errorClass)
        } catch (e: JsonSyntaxException) {
            ErrorUtil.handleError(
                context,
                R.string.exception_fail_parse_exception,
                e,
                endpoint,
                errorBodyString,
                httpCode,
                isShowToast = isShowErrorToast
            )
            onError.invoke()
            return
        } catch (e: IllegalStateException) {
            ErrorUtil.handleError(
                context,
                R.string.exception_parse_error_illegal_state,
                e,
                endpoint,
                errorBodyString,
                httpCode,
                isShowToast = isShowErrorToast
            )
            onError.invoke()
            return
        } catch (e: NullPointerException) {
            ErrorUtil.handleError(
                context,
                R.string.exception_fail_no_object,
                e,
                endpoint,
                errorBodyString,
                httpCode,
                isShowToast = isShowErrorToast
            )
            onError.invoke()
            return
        }
        onFail.invoke(error)
    }

    private fun <T, E> handleFailAndErrorBadRequest(
        context: Context,
        httpCode: Int,
        endpoint: String,
        onFail: (E) -> Unit,
        onError: () -> Unit,
        response: Response<T>,
        errorClass: Class<E>,
        isShowErrorToast: Boolean = true
    ) {
        val error: E
        val errorBodyString = response.errorBody()?.string()
        try {
            error = parseError(errorBodyString, errorClass)
        } catch (e: JsonSyntaxException) {
            // To fix useless session bug where clients encounter 400 error for all requests due to unknown reason
            // The common response of this particular bug is that all of them have HTML body
            return SessionUtil.signOut()
        } catch (e: IllegalStateException) {
            ErrorUtil.handleError(
                context,
                R.string.exception_parse_error_illegal_state,
                e,
                endpoint,
                errorBodyString,
                httpCode,
                isShowToast = isShowErrorToast
            )
            onError.invoke()
            return
        } catch (e: NullPointerException) {
            ErrorUtil.handleError(
                context,
                R.string.exception_fail_no_object,
                e,
                endpoint,
                errorBodyString,
                httpCode,
                isShowToast = isShowErrorToast
            )
            onError.invoke()
            return
        }
        if (error is ApiError && error.errorCode == ErrorCode.NO_LOGGED_IN_USER) {
            SessionUtil.signOut()
        } else {
            onFail.invoke(error)
        }
    }

    private fun <T> maybePrintRequestBody(call: Call<T>) {
        if (!BuildConfig.DEBUG) return
        val request = call.request()
        val requestBody = request.body
        if (requestBody != null) {
            val requestBodyString = getRequestBodyString(requestBody)
            if (requestBodyString != null) {
                println("okhttp Request body of ${getRequestName(request)}:")
                println("okhttp $requestBodyString")
            }
        }
    }

    private fun getRequestBodyString(requestBody: RequestBody): String? {
        return try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            null
        }
    }

    fun createRetrofit(domain: Domain): Retrofit {
        return Retrofit.Builder()
            .client(domain.getOkHttpClient())
            .baseUrl(domain.getBaseUrl())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}