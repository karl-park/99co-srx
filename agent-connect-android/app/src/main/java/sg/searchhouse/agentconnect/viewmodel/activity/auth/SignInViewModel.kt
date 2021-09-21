package sg.searchhouse.agentconnect.viewmodel.activity.auth

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.auth.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class SignInViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var applicationContext: Context

    //Layout Type -> Cea and Mobile Layout & Email and Password Layout
    var layoutType = MutableLiveData<LayoutType>().apply { value = LayoutType.LOGIN }

    //Login with Cea and mobile
    val ceaNo = MutableLiveData<String>()
    val mobile = MutableLiveData<String>()
    val errorCeaNo = MutableLiveData<String>()
    val errorMobile = MutableLiveData<String>()

    //Login with email and password
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()

    private var isLoginBtnClicked: Boolean = false
    private val loginBtnState = MutableLiveData<ButtonState>()
    val loginStatusLiveData = MutableLiveData<ApiStatus<LoginResponse>>()
    val resetDeviceStatus = MutableLiveData<ApiStatus<ResetDeviceResponse>>()

    val actionBtnText: LiveData<String> = Transformations.map(loginBtnState) { btnState ->
        when (btnState) {
            ButtonState.SUBMITTING -> {
                applicationContext.getString(R.string.action_signing_in)
            }
            else -> {
                applicationContext.getString(R.string.action_sign_in)
            }
        }
    }
    val isActionBtnEnabled: LiveData<Boolean> = Transformations.map(loginBtnState) { btnState ->
        when (btnState) {
            ButtonState.SUBMITTING, ButtonState.ERROR -> false
            else -> true
        }
    }

    init {
        viewModelComponent.inject(this)
        loginBtnState.value = ButtonState.NORMAL
    }

    //Toggle sign up and sign in screens
    fun toggleLayout() {
        if (layoutType.value == LayoutType.SIGN_UP) {
            resetSignUpForm()
            layoutType.value = LayoutType.LOGIN
        } else {
            resetLoginForm()
            layoutType.value = LayoutType.SIGN_UP
        }
    }

    fun loginAction() {
        isLoginBtnClicked = true
        if (validate()) {
            loginBtnState.value = ButtonState.SUBMITTING
            val loginCall = if (layoutType.value == LayoutType.SIGN_UP) {
                authRepository.loginWithCea(
                    LoginWithCeaRequest(
                        ceaNo.value.toString(),
                        mobile.value.toString()
                    )
                )
            } else {
                authRepository.loginWithEmail(
                    LoginWithEmailRequest(
                        email.value.toString(),
                        password.value.toString()
                    )
                )
            }

            ApiUtil.performRequest(
                applicationContext,
                loginCall,
                onSuccess = { loginResponse ->
                    loginBtnState.postValue(ButtonState.SUBMITTED)
                    loginStatusLiveData.postValue(ApiStatus.successInstance(loginResponse))
                }, onFail = { apiError ->
                    if (layoutType.value == LayoutType.SIGN_UP) {
                        errorCeaNo.postValue(" ")
                        errorMobile.postValue(apiError.error)
                    } else {
                        errorEmail.postValue(" ")
                        errorPassword.postValue(apiError.error)
                    }
                    loginBtnState.postValue(ButtonState.NORMAL)
                }, onError = {
                    loginBtnState.postValue(ButtonState.NORMAL)
                })
        }
    }

    fun resetAccount(userId: String) {
        //disable login button when reset
        loginBtnState.postValue(ButtonState.ERROR)

        //Reset Device by userId -> if no user Id, reset with email
        val resetDeviceRequest = if (!TextUtils.isEmpty(userId)) {
            ResetDeviceRequest(userId = userId)
        } else {
            ResetDeviceRequest(email = email.value.toString())
        }
        //start calling api...
        ApiUtil.performRequest(
            applicationContext,
            authRepository.resetDevice(resetDeviceRequest),
            onSuccess = { response ->
                loginBtnState.postValue(ButtonState.NORMAL)
                resetDeviceStatus.postValue(ApiStatus.successInstance(response))
            },
            onFail = { apiError ->
                loginBtnState.postValue(ButtonState.NORMAL)
                resetDeviceStatus.postValue(ApiStatus.failInstance(apiError))
            },
            onError = {
                loginBtnState.postValue(ButtonState.NORMAL)
                resetDeviceStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun validate(): Boolean {
        var isAllValidate = true
        loginBtnState.value = ButtonState.NORMAL

        if (layoutType.value == LayoutType.SIGN_UP) {
            //CEA & Mobile
            if (!checkCeaNo(ceaNo.value?.toString() ?: "")) {
                loginBtnState.value = ButtonState.ERROR
                isAllValidate = false
            }

            if (!checkMobile(mobile.value?.toString() ?: "")) {
                loginBtnState.value = ButtonState.ERROR
                isAllValidate = false
            }
        } else {
            //Email & Password
            if (!checkEmail(email.value?.toString() ?: "")) {
                loginBtnState.value = ButtonState.ERROR
                isAllValidate = false
            }

            if (!checkPassword(password.value?.toString() ?: "")) {
                loginBtnState.value = ButtonState.ERROR
                isAllValidate = false
            }
        }
        return isAllValidate
    }

    //Text Changed Events
    fun afterTextChangedCeaNo(editable: Editable?) {
        val ceaNo = editable?.toString() ?: ""
        if (isLoginBtnClicked) {
            if (!checkCeaNo(ceaNo) || !checkMobile(mobile.value ?: "")) {
                loginBtnState.value = ButtonState.ERROR
            } else {
                loginBtnState.value = ButtonState.NORMAL
            }
        }
    }

    fun afterTextChangedMobile(editable: Editable?) {
        val mobile = editable?.toString() ?: ""
        if (isLoginBtnClicked) {
            if (!checkMobile(mobile) || !checkCeaNo(ceaNo.value ?: "")) {
                loginBtnState.value = ButtonState.ERROR
            } else {
                loginBtnState.value = ButtonState.NORMAL
            }
        }
    }

    fun afterTextChangedEmail(editable: Editable?) {
        val email = editable?.toString() ?: ""
        if (isLoginBtnClicked) {
            if (!checkEmail(email) || !checkPassword(password.value ?: "")) {
                loginBtnState.value = ButtonState.ERROR
            } else {
                loginBtnState.value = ButtonState.NORMAL
            }
        }
    }

    fun afterTextChangedPassword(editable: Editable?) {
        val password = editable?.toString() ?: ""
        if (isLoginBtnClicked) {
            if (!checkPassword(password) || !checkEmail(email.value ?: "")) {
                loginBtnState.value = ButtonState.ERROR
            } else {
                loginBtnState.value = ButtonState.NORMAL
            }
        }
    }

    //CEA No
    private fun checkCeaNo(ceaNo: String): Boolean {
        var isAllValidate = true
        when {
            TextUtils.isEmpty(ceaNo.trim()) -> {
                isAllValidate = false
                errorCeaNo.value = applicationContext.getString(R.string.error_required_field)
            }
            !StringUtil.isCeaNoValid(ceaNo) -> {
                isAllValidate = false
                errorCeaNo.value = applicationContext.getString(R.string.error_invalid_cea)
            }
            else -> errorCeaNo.value = null
        }
        return isAllValidate
    }

    //Mobile
    private fun checkMobile(mobile: String): Boolean {
        var isAllValidate = true
        when {
            TextUtils.isEmpty(mobile.trim()) -> {
                isAllValidate = false
                errorMobile.value = applicationContext.getString(R.string.error_required_field)
            }
            !StringUtil.isMobileNoValid(mobile) -> {
                isAllValidate = false
                errorMobile.value = applicationContext.getString(R.string.error_invalid_mobile)
            }
            else -> errorMobile.value = null
        }
        return isAllValidate
    }

    //Email
    private fun checkEmail(email: String): Boolean {
        var isAllValidate = true
        when {
            TextUtils.isEmpty(email.trim()) -> {
                isAllValidate = false
                errorEmail.value = applicationContext.getString(R.string.error_required_field)
            }
            !StringUtil.isEmailValid(email.trim()) -> {
                isAllValidate = false
                errorEmail.value = applicationContext.getString(R.string.error_invalid_email)
            }
            else -> errorEmail.value = null
        }
        return isAllValidate
    }

    //Password
    private fun checkPassword(password: String): Boolean {
        //only need to check empty string or not
        var isAllValidate = true
        when {
            TextUtils.isEmpty(password.trim()) -> {
                isAllValidate = false
                errorPassword.value = applicationContext.getString(R.string.error_required_field)
            }
            else -> errorPassword.value = null
        }
        return isAllValidate
    }

    //Reset sign up form
    private fun resetSignUpForm() {
        ceaNo.value = null
        mobile.value = null
        errorCeaNo.value = null
        errorMobile.value = null
        isLoginBtnClicked = false
        loginBtnState.value = ButtonState.NORMAL
    }

    private fun resetLoginForm() {
        email.value = null
        password.value = null
        errorEmail.value = null
        errorPassword.value = null
        isLoginBtnClicked = false
        loginBtnState.value = ButtonState.NORMAL
    }

    enum class LayoutType(val value: String) {
        SIGN_UP("sign_up"),
        LOGIN("login")
    }
}