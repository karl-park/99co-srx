package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus

// Reusable base view model to populate list with states but without API in itself
abstract class ListBaseViewModel<T> constructor(application: Application) :
    CoreViewModel(application) {
    val listItems =
        MutableLiveData<List<T>>()

    val mainStatus = MutableLiveData<ApiStatus.StatusKey>()

    val isShowEmpty: MediatorLiveData<Boolean> = MediatorLiveData()

    val isShowOccupied: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        mainStatus.value = ApiStatus.StatusKey.LOADING
        setupIsShowOccupied()
        setupIsShowEmpty()
    }

    private fun setupIsShowOccupied() {
        isShowOccupied.value = false
        isShowOccupied.addSource(listItems) { listItems ->
            if (mainStatus.value == ApiStatus.StatusKey.SUCCESS) {
                isShowOccupied.postValue(listItems?.isNotEmpty() == true)
            }
        }
        isShowOccupied.addSource(mainStatus) { status ->
            if (status !in listOf(
                    ApiStatus.StatusKey.SUCCESS,
                    ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
                )
            ) {
                isShowOccupied.postValue(false)
            }
        }
    }

    private fun setupIsShowEmpty() {
        isShowEmpty.value = false
        isShowEmpty.addSource(listItems) { listItems ->
            if (mainStatus.value == ApiStatus.StatusKey.SUCCESS) {
                isShowEmpty.postValue(listItems?.isNotEmpty() != true)
            }
        }
        isShowEmpty.addSource(mainStatus) { status ->
            if (status != ApiStatus.StatusKey.SUCCESS) {
                isShowEmpty.postValue(false)
            }
        }
    }
}
