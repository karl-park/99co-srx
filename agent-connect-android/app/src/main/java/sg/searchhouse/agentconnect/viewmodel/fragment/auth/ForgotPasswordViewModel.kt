package sg.searchhouse.agentconnect.viewmodel.fragment.auth

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.auth.ForgotPasswordRequest
import sg.searchhouse.agentconnect.model.api.auth.ResetPasswordResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class ForgotPasswordViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var userAuthRepository: AuthRepository

    @Inject
    lateinit var applicationContext: Context

    private var isUpdateBtnClicked: Boolean = false
    val email = MutableLiveData<String>()
    val errorEmail = MutableLiveData<String>()
    private val updateBtnState = MutableLiveData<ButtonState>()
    val apiStatusLiveData = MutableLiveData<ApiStatus<ResetPasswordResponse>>()
    val actionBtnText: LiveData<String> = Transformations.map(updateBtnState) { btnState ->
        when (btnState) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_submitting)
            else -> applicationContext.getString(R.string.action_submit)
        }
    }
    val isActionBtnEnabled: LiveData<Boolean> = Transformations.map(updateBtnState) { btnState ->
        when (btnState) {
            ButtonState.SUBMITTING, ButtonState.ERROR -> false
            else -> true
        }
    }

    init {
        viewModelComponent.inject(this)
        updateBtnState.value = ButtonState.NORMAL
    }

    fun onSubmit() {
        isUpdateBtnClicked = true
        if (validate()) {
            resetPassword()
        }
    }

    private fun resetPassword() {
        updateBtnState.postValue(ButtonState.SUBMITTING)
        apiStatusLiveData.postValue(ApiStatus.loadingInstance())

        ApiUtil.performRequest(applicationContext,
            userAuthRepository.resetPassword(
                ForgotPasswordRequest(email.value.toString())
            ),
            onSuccess = { resetPwdResponseData ->
                updateBtnState.postValue(ButtonState.SUBMITTED)
                apiStatusLiveData.postValue(ApiStatus.successInstance(resetPwdResponseData))
            },
            onFail = { apiError ->
                errorEmail.postValue(apiError.error)
                updateBtnState.postValue(ButtonState.NORMAL)
            },
            onError = {
                updateBtnState.postValue(ButtonState.NORMAL)
            })
    }

    fun afterTextChangedEmail(editable: Editable?) {
        val email = editable?.toString() ?: ""
        if (isUpdateBtnClicked) {
            if (!checkEmail(email)) {
                updateBtnState.value = ButtonState.ERROR
            } else {
                updateBtnState.value = ButtonState.NORMAL
            }
        }
    }

    private fun validate(): Boolean {
        updateBtnState.value = ButtonState.NORMAL
        if (!checkEmail(email.value?.toString() ?: "")) {
            updateBtnState.value = ButtonState.ERROR
            return false
        }
        return true
    }

    private fun checkEmail(email: String): Boolean {
        var isAllValidate = true
        when {
            TextUtils.isEmpty(email.trim()) -> {
                isAllValidate = false
                errorEmail.postValue(applicationContext.getString(R.string.error_required_field))
            }
            !StringUtil.isEmailValid(email.trim()) -> {
                isAllValidate = false
                errorEmail.postValue(applicationContext.getString(R.string.error_invalid_email))
            }
            else -> errorEmail.postValue(null)
        }
        return isAllValidate
    }
}