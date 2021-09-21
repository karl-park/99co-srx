package sg.searchhouse.agentconnect.viewmodel.activity.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.auth.LoginResponse
import sg.searchhouse.agentconnect.model.api.auth.LoginWithEmailRequest
import sg.searchhouse.agentconnect.model.api.auth.ResetDeviceRequest
import sg.searchhouse.agentconnect.model.api.auth.ResetDeviceResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class SplashViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var authRepository: AuthRepository

    var email: String = ""
    var password: String = ""

    val loginStatus = MutableLiveData<ApiStatus<LoginResponse>>()
    val resetAccountStatus = MutableLiveData<ApiStatus<ResetDeviceResponse>>()

    init {
        viewModelComponent.inject(this)
    }

    fun loginWithEmail(email: String, password: String) {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.loginWithEmail(LoginWithEmailRequest(email, password)),
            onSuccess = { loginStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { loginStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { loginStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun resetAccount(userId: String) {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.resetDevice(ResetDeviceRequest(userId = userId)),
            onSuccess = { resetAccountStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { apiError -> resetAccountStatus.postValue(ApiStatus.failInstance(apiError)) },
            onError = { resetAccountStatus.postValue(ApiStatus.errorInstance()) }
        )
    }
}