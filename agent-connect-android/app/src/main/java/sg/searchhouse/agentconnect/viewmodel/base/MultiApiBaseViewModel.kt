package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import androidx.lifecycle.*
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
abstract class MultiApiBaseViewModel<T> constructor(application: Application) : CoreViewModel(application) {
    // Server response
    val mainResponse: MutableLiveData<List<T>> = MutableLiveData()

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
    abstract fun shouldResponseBeOccupied(responses: List<T>): Boolean

    fun performRequests(calls: List<Call<T>>) {
        mainStatus.postValue(LOADING)
        performRequestsCore(calls)
    }

    private fun performRequestsCore(calls: List<Call<T>>) {
        val applicationContext = getApplication<AgentConnectApplication>().applicationContext
        ApiUtil.performRequests(applicationContext, calls,
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
            if (status !in listOf(SUCCESS, LOADING_NEXT_LIST_ITEM)) {
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