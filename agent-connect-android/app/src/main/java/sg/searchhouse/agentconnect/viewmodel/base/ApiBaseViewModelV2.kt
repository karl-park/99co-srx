package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import retrofit2.Call
import sg.searchhouse.agentconnect.AgentConnectApplication
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.SUCCESS

/**
 *  Reusable base view model to populate view based on API response
 *  Use `isShowOccupied` flag to decide display of occupied layout
 *  Use `isShowEmpty` flag to decide display of empty layout
 *  Use `mainStatus` key to decide display of loading, fail, and/or error layouts
 *  Call `performRequest` in this class to call API that populate this view
 *  `mainResponse` is the view populating API result
 */
abstract class ApiBaseViewModelV2<T>(application: Application) : CoreViewModel(application) {
    // Server response
    val mainResponse = MutableLiveData<ApiStatus<T>>()
    val mainResponseBody: LiveData<T> = Transformations.map(mainResponse) { it?.body }
    val mainStatus: LiveData<ApiStatus.StatusKey> = Transformations.map(mainResponse) { it?.key }

    val isShowOccupied: LiveData<Boolean> = Transformations.map(mainResponse) { response ->
        val isSuccessAndOccupied = response?.key == SUCCESS && response.getIsOccupied()
        val isLoadingNext = response?.key == LOADING_NEXT_LIST_ITEM
        isSuccessAndOccupied || isLoadingNext
    }

    val isShowEmpty: LiveData<Boolean> = Transformations.map(mainResponse) { response ->
        response?.key == SUCCESS && !response.getIsOccupied()
    }

    private fun ApiStatus<T>.getIsOccupied(): Boolean {
        return body?.let { shouldResponseBeOccupied(it) } ?: false
    }

    // Return true when there are intended items in the response, isShowOccupied will be true when response available
    // Return false to show empty view, isShowEmpty will be true when response available
    abstract fun shouldResponseBeOccupied(response: T): Boolean

    fun performRequest(call: Call<T>) {
        mainResponse.postValue(ApiStatus.loadingInstance(mainResponse.value))
        performRequestCore(call)
    }

    fun performNextRequest(call: Call<T>) {
        mainResponse.postValue(ApiStatus.loadingNextInstance(mainResponse.value))
        performRequestCore(call)
    }

    private fun getApplicationContext(): Context {
        return getApplication<AgentConnectApplication>().applicationContext
    }

    private fun isRequestRepeated(): Boolean {
        return isCheckOverlapRequests() && mainResponse.value?.isRequestRepeated() == true
    }

    private fun performRequestCore(call: Call<T>) {
        call.performRequest(getApplicationContext(),
            onSuccess = { response ->
                if (isRequestRepeated()) return@performRequest
                mainResponse.postValue(ApiStatus.successInstance(response))
            },
            onFail = {
                if (isRequestRepeated()) return@performRequest
                mainResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                if (isRequestRepeated()) return@performRequest
                mainResponse.postValue(ApiStatus.errorInstance())
            })
    }

    /**
     * If true, when the API request of `mainResponse` called multiple times simultaneously,
     * ignore all previous results, only take the last one.
     */
    abstract fun isCheckOverlapRequests(): Boolean
}