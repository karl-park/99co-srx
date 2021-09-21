package sg.searchhouse.agentconnect.dsl

import android.content.Context
import retrofit2.Call
import sg.searchhouse.agentconnect.model.api.ApiError
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil

fun <T> Call<T>.performRequest(
    context: Context,
    onSuccess: (T) -> Unit,
    onFail: (ApiError) -> Unit,
    onError: () -> Unit,
    enablePrintRequestBody: Boolean = true,
    isShowErrorToast: Boolean = true
) = ApiUtil.performRequest(context, this, onSuccess, onFail, onError, enablePrintRequestBody, isShowErrorToast)

fun <T> List<Call<T>>.performRequests(
    context: Context,
    onSuccess: (List<T>) -> Unit,
    onFail: (ApiError) -> Unit,
    onError: () -> Unit,
    enablePrintRequestBody: Boolean = true,
    isShowErrorToast: Boolean = true
) = ApiUtil.performRequests(context, this, onSuccess, onFail, onError, enablePrintRequestBody, isShowErrorToast)

fun <T> List<Call<T>>.performRequestsLenient(
    context: Context,
    onSuccess: (List<T?>) -> Unit,
    enablePrintRequestBody: Boolean = true,
    isShowErrorToast: Boolean = true
) = ApiUtil.performRequestsLenient(context, this, onSuccess, enablePrintRequestBody, isShowErrorToast)

fun <T> List<Call<T>>.performRequestsLenientOneByOne(
    context: Context,
    onItemSuccess: (List<T?>) -> Unit,
    enablePrintRequestBody: Boolean = true,
    isShowErrorToast: Boolean = true
) = ApiUtil.performRequestsLenientOneByOne(context, this, onItemSuccess, enablePrintRequestBody, isShowErrorToast)

fun ApiStatus.StatusKey.isOccupied(): Boolean = ApiStatus.showOccupiedStatuses.contains(this)