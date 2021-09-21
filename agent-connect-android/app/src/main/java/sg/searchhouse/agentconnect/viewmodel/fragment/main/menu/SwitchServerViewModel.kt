package sg.searchhouse.agentconnect.viewmodel.fragment.main.menu

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class SwitchServerViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var authRepository: AuthRepository

    val inputServerUrl: MutableLiveData<String> = MutableLiveData()
    val errorUrl: MutableLiveData<String> = MutableLiveData()
    val logoutStatus: MutableLiveData<ApiStatus<DefaultResultResponse>> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
        inputServerUrl.value = "https://"
    }

    fun logout() {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.logout(),
            onSuccess = { logoutStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { logoutStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { logoutStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

}