package sg.searchhouse.agentconnect.viewmodel.fragment.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.auth.LoginResponseData
import sg.searchhouse.agentconnect.model.api.auth.VerifyOtpResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class MobileVerificationViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var authRepository: AuthRepository
    @Inject
    lateinit var applicationContext: Context

    val loginResponseData = MutableLiveData<LoginResponseData>()
    val verifyMobileStatus = MutableLiveData<ApiStatus<VerifyOtpResponse>>()
    val resendOTPStatus = MutableLiveData<ApiStatus<VerifyOtpResponse>>()

    init {
        viewModelComponent.inject(this)
    }

    fun resendOTP() {
        val responseData = loginResponseData.value ?: return
        resendOTPStatus.postValue(ApiStatus.loadingInstance())
        ApiUtil.performRequest(applicationContext,
            authRepository.resendOTP(responseData),
            onSuccess = { verifyOtpResponse ->
                resendOTPStatus.postValue(
                    ApiStatus.successInstance(
                        verifyOtpResponse
                    )
                )
            },
            onFail = { apiError -> resendOTPStatus.postValue(ApiStatus.failInstance(apiError)) },
            onError = { resendOTPStatus.postValue(ApiStatus.errorInstance()) })
    }

    fun otpComplete(otp: String) {
        if (otp.length == 6) {
            loginResponseData.value?.otp = otp
            verifyOTP()
        }
    }

    private fun verifyOTP() {
        val requestData = loginResponseData.value ?: return
        CoroutineScope(Dispatchers.IO).launch {
            delay(200L)
            verifyMobileStatus.postValue(ApiStatus.loadingInstance())
            ApiUtil.performRequest(
                applicationContext,
                authRepository.verifyOTP(requestData),
                onSuccess = { verifyMobileStatus.postValue(ApiStatus.successInstance(it)) },
                onFail = { verifyMobileStatus.postValue(ApiStatus.failInstance(it)) },
                onError = { verifyMobileStatus.postValue(ApiStatus.errorInstance()) })
        }
    }
}