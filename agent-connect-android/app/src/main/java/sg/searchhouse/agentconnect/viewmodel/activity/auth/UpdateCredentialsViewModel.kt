package sg.searchhouse.agentconnect.viewmodel.activity.auth

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.auth.CreateAccountRequest
import sg.searchhouse.agentconnect.model.api.auth.CreateAccountResponse
import sg.searchhouse.agentconnect.model.api.auth.VerifyOtpResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class UpdateCredentialsViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var userAuthRepository: AuthRepository

    @Inject
    lateinit var applicationContext: Context

    private var updateBtnCount: Boolean = false

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val isAgreementChecked = MutableLiveData<Boolean>()
    val verifyOTPResponse = MutableLiveData<VerifyOtpResponse>()

    val createAccountStatus = MutableLiveData<ApiStatus<CreateAccountResponse>>()

    val updateBtnState = MutableLiveData<ButtonState>()
    val actionBtnText: LiveData<String> = Transformations.map(updateBtnState) { btnState ->
        return@map when (btnState) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_updating)
            else -> applicationContext.getString(R.string.action_update)
        }
    }
    val isActionBtnEnabled: LiveData<Boolean> = Transformations.map(updateBtnState) { btnState ->
        return@map when (btnState) {
            ButtonState.NORMAL, ButtonState.SUBMITTED -> true
            else -> false
        }
    }

    init {
        viewModelComponent.inject(this)
        isAgreementChecked.value = false
    }

    //update account creation
    fun update() {
        updateBtnCount = true

        if (isValidate()) {
            updateBtnState.value = ButtonState.SUBMITTING
            createAccountStatus.postValue(ApiStatus.loadingInstance())

            val createAccountRequest = verifyOTPResponse.value?.let {
                CreateAccountRequest(
                    it.data.otp,
                    email.value.toString(),
                    password.value.toString(),
                    it.data.ceaRegNo,
                    it.data.mobileNum
                )
            }
            ApiUtil.performRequest(
                applicationContext,
                userAuthRepository.createAccount(createAccountRequest!!),
                onSuccess = {
                    createAccountStatus.postValue(ApiStatus.successInstance(it))
                },
                onFail = {
                    createAccountStatus.postValue(ApiStatus.failInstance(it))
                },
                onError = {
                    createAccountStatus.postValue(ApiStatus.errorInstance())
                }
            ) //end of perform request
        } else {
            updateBtnState.value = ButtonState.ERROR
        }
    }

    fun afterTextChangedEmail(editable: Editable?) {
        val email = editable?.toString() ?: ""
        if (updateBtnCount) {
            if (!checkEmail(email) || !checkPassword(password.value)) {
                updateBtnState.value = ButtonState.ERROR
            } else {
                updateBtnState.value = ButtonState.NORMAL
            }
        }
    }

    fun afterTextChangedPassword(editable: Editable?) {
        val password = editable?.toString() ?: ""
        if (updateBtnCount) {
            if (!checkPassword(password) || !checkEmail(email.value)) {
                updateBtnState.value = ButtonState.ERROR
            } else {
                updateBtnState.value = ButtonState.NORMAL
            }
        }
    }

    //Validation
    private fun isValidate(): Boolean {
        //Login -> email and password
        var isAllValidate = true

        if (isAgreementChecked.value != true) {
            isAllValidate = false
        }

        if (!checkEmail(email.value)) {
            isAllValidate = false
        }

        if (!checkPassword(password.value)) {
            isAllValidate = false
        }

        return isAllValidate
    }

    fun onCheckedChanged(btnView: CompoundButton, isChecked: Boolean) {
        if (btnView.isPressed) {
            isAgreementChecked.value = isChecked
        }
    }

    //Email
    private fun checkEmail(email: String?): Boolean {
        var isAllValidate = true
        when {
            TextUtils.isEmpty(email) -> {
                isAllValidate = false
                errorEmail.value = applicationContext.getString(R.string.error_required_field)
            }
            !StringUtil.isEmailValid(email.toString()) -> {
                isAllValidate = false
                errorEmail.value = applicationContext.getString(R.string.error_invalid_email)
            }
            else -> errorEmail.value = ""
        }
        return isAllValidate
    }

    //Password
    private fun checkPassword(password: String?): Boolean {
        //only need to check empty string or not
        var isAllValidate = true
        when {
            TextUtils.isEmpty(password) -> {
                isAllValidate = false
                errorPassword.value = applicationContext.getString(R.string.error_required_field)
            }
            !StringUtil.isPasswordLengthValid(password.toString()) -> {
                isAllValidate = false
                errorPassword.value = applicationContext.getString(R.string.error_invalid_password)
            }
            else -> errorPassword.value = ""
        }
        return isAllValidate
    }
}