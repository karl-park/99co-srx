package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import retrofit2.Call
import sg.searchhouse.agentconnect.AgentConnectApplication
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.ApiUtil

// Reusable base view model to populate view based on API response
//
// Use isShowOccupied flag to decide display of occupied layout
// Use isShowEmpty flag to decide display of empty layout
// Use mainStatus key to decide display of loading, fail, and/or error layouts
// Call performRequest in this class to call API that populate this view
// mainResponse is the view populating API result
@Deprecated("Use `ApiBaseViewModelV2`")
abstract class ApiBaseViewModel<T> constructor(application: Application) :
    CoreViewModel(application) {
    // Server response
    val mainResponse: MutableLiveData<T> = MutableLiveData()

    // Handle loading, normal, and error states
    val mainStatus: MutableLiveData<ApiStatus.StatusKey> = MutableLiveData()

    // Occupied means list items > 0
    private var isResponseOccupied: LiveData<Boolean>

    val isShowOccupied: MediatorLiveData<Boolean> = MediatorLiveData()
    val isShowEmpty: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        isResponseOccupied = Transformations.map(mainResponse) { response ->
            response?.let {
                shouldResponseBeOccupied(response)
            } ?: run {
                false
            }
        }

        mainResponse.value = null

        setupIsShowOccupied()
        setupIsShowEmpty()

        mainStatus.value = LOADING
    }

    // Return true when there are intended items in the response, isShowOccupied will be true when response available
    // Return false to show empty view, isShowEmpty will be true when response available
    abstract fun shouldResponseBeOccupied(response: T): Boolean

    fun performRequest(call: Call<T>) {
        mainStatus.postValue(LOADING)
        performRequestCore(call)
    }

    fun performNextRequest(call: Call<T>) {
        mainStatus.postValue(LOADING_NEXT_LIST_ITEM)
        performRequestCore(call)
    }

    private fun performRequestCore(call: Call<T>) {
        val applicationContext = getApplication<AgentConnectApplication>().applicationContext
        ApiUtil.performRequest(applicationContext, call,
            onSuccess = { response ->
                mainResponse.postValue(response)
                mainStatus.postValue(SUCCESS)
            }, onFail = {
                mainStatus.postValue(FAIL)
            }, onError = {
                mainStatus.postValue(ERROR)
            })
    }

    private fun setupIsShowOccupied() {
        isShowOccupied.value = false
        isShowOccupied.addSource(isResponseOccupied) { isPresent ->
            isShowOccupied.postValue(isPresent)
        }
        isShowOccupied.addSource(mainStatus) { status ->
            if (status !in ApiStatus.showOccupiedStatuses) {
                isShowOccupied.postValue(false)
            }
        }
    }

    private fun setupIsShowEmpty() {
        isShowEmpty.value = false
        isShowEmpty.addSource(isResponseOccupied) { isPresent ->
            isShowEmpty.postValue(!isPresent)
        }
        isShowEmpty.addSource(mainStatus) { status ->
            if (status != SUCCESS) {
                isShowEmpty.postValue(false)
            }
        }
    }
}