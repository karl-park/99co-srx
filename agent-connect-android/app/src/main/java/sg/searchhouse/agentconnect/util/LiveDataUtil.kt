package sg.searchhouse.agentconnect.util

import androidx.lifecycle.MutableLiveData

object LiveDataUtil {
    fun toggleBoolean(booleanLiveData: MutableLiveData<Boolean>) {
        booleanLiveData.postValue(booleanLiveData.value != true)
    }
}