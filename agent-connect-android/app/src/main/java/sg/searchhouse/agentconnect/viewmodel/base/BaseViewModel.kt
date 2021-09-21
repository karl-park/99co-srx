package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.dsl.performAsyncQueriesLenient
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.auth.GetConfigResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import javax.inject.Inject

// For use of BaseActivity ONLY, not for inheritance
class BaseViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var applicationContext: Context

    val apiCallResult = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val registerTokenResult = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val getConfigStatus = MutableLiveData<ApiStatus<GetConfigResponse>>()

    init {
        viewModelComponent.inject(this)
    }

    fun registerToken(token: String) {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.registerToken(token),
            onSuccess = { registerTokenResult.postValue(ApiStatus.successInstance(it)) },
            onFail = { registerTokenResult.postValue(ApiStatus.failInstance(it)) },
            onError = { registerTokenResult.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun getConfig() {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.getConfig(),
            onSuccess = { getConfigStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { getConfigStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { getConfigStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun deleteAllTables() {
        chatDbRepository.deleteAllTables().performAsyncQueriesLenient {}
    }

    fun logout() {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.logout(),
            onSuccess = { apiCallResult.postValue(ApiStatus.successInstance(it)) },
            onFail = { apiCallResult.postValue(ApiStatus.failInstance(it)) },
            onError = { apiCallResult.postValue(ApiStatus.errorInstance()) }
        )
    }

}