package sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.PortalListingRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.AppPortalType
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PortalMode
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ClientLoginResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalLoginRequest
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalLoginResponse
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class PGImportDialogViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var portalListingRepository: PortalListingRepository

    @Inject
    lateinit var applicationContext: Context

    private var isConnectBtnClicked: Boolean = false

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()

    private val portalAgentId = MutableLiveData<Int>()
    val portalMode = MutableLiveData<PortalMode>()
    val portalType = MutableLiveData<AppPortalType>()
    val isRememberMeChecked = MutableLiveData<Boolean>()
    val loginStatusLiveData = MutableLiveData<ApiStatus<PortalLoginResponse>>()
    val clientLoginStatusLiveData = MutableLiveData<ApiStatus<ClientLoginResponse>>()
    val portalApiResponse = MutableLiveData<GetPortalAPIsResponse>()

    private val btnState = MutableLiveData<ButtonState>()
    val isConnectBtnEnabled: LiveData<Boolean> =
        Transformations.map(btnState) { response ->
            return@map when (response) {
                ButtonState.SUBMITTING -> false
                else -> true
            }
        }
    val connectBtnText: LiveData<String> =
        Transformations.map(btnState) { response ->
            return@map when (response) {
                ButtonState.SUBMITTING -> {
                    applicationContext.getString(R.string.action_connecting)
                }
                else -> {
                    applicationContext.getString(R.string.action_connect)
                }
            }
        }

    init {
        viewModelComponent.inject(this)
        setupInitialValues()
    }

    private fun setupInitialValues() {
        btnState.value = ButtonState.NORMAL
        isRememberMeChecked.value = true
        portalType.value = AppPortalType.PROPERTY_GURU
    }

    fun loginWithPG() {
        isConnectBtnClicked = true
        if (validate()) {
            btnState.value = ButtonState.SUBMITTING
            if (portalMode.value == PortalMode.SERVER) {
                serverLogin()
            } else {
                portalLogin()
            }
        }
    }

    private fun serverLogin() {
        val userEmail = email.value ?: return
        val userPassword = password.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.serverLoginPortal(
                PortalLoginRequest(
                    userEmail,
                    userPassword,
                    AppPortalType.PROPERTY_GURU.value
                )
            ),
            onSuccess = {
                btnState.postValue(ButtonState.SUBMITTED)
                loginStatusLiveData.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                errorEmail.postValue(null)
                errorPassword.postValue(it.error)
                btnState.postValue(ButtonState.ERROR)
                loginStatusLiveData.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                btnState.postValue(ButtonState.ERROR)
                loginStatusLiveData.postValue(ApiStatus.errorInstance())
            })
    }

    private fun portalLogin() {
        val loginTemplate = portalApiResponse.value?.templates?.login ?: return
        loginTemplate.params?.set("email", email.value ?: "")
        loginTemplate.params?.set("password", password.value ?: "")
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.portalLogin(loginTemplate),
            onSuccess = { response ->
                if (response.containsKey("user_id")) {
                    if (response.getValue("user_id") is Double) {
                        val agentId = response.getValue("user_id") as Double
                        portalAgentId.postValue(agentId.toInt())
                        clientLogin()
                    }
                } else if (response.containsKey("errors")) {
                    errorEmail.postValue(null)
                    errorPassword.postValue(applicationContext.getString(R.string.error_msg_portal_login_info))
                    btnState.postValue(ButtonState.ERROR)
                }
            },
            onFail = { btnState.postValue(ButtonState.ERROR) },
            onError = { btnState.postValue(ButtonState.ERROR) },
            enablePrintRequestBody = false
        )
    }

    private fun clientLogin() {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.clientLoginPortal(
                email.value.toString(),
                password.value.toString(),
                AppPortalType.PROPERTY_GURU.value
            ),
            onSuccess = {
                btnState.postValue(ButtonState.SUBMITTED)
                clientLoginStatusLiveData.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                errorEmail.postValue(null)
                errorPassword.postValue(it.error)
                btnState.postValue(ButtonState.ERROR)
                clientLoginStatusLiveData.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                btnState.postValue(ButtonState.NORMAL)
                clientLoginStatusLiveData.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun onCheckedChanged(btnView: CompoundButton, isChecked: Boolean) {
        if (btnView.isPressed) {
            isRememberMeChecked.value = isChecked
        }
    }

    private fun validate(): Boolean {
        var isAllValidate = true
        btnState.value = ButtonState.NORMAL

        if (!isEmailValid(email.value)) {
            btnState.value = ButtonState.ERROR
            isAllValidate = false
        }

        if (!isPasswordValid(password.value)) {
            btnState.value = ButtonState.ERROR
            isAllValidate = false
        }

        return isAllValidate
    }

    //Text Changed Events
    fun afterTextChangedEmail(editable: Editable?) {
        val email = editable?.toString() ?: ""
        if (isConnectBtnClicked) {
            if (!isEmailValid(email) || !isPasswordValid(password.value)) {
                btnState.value = ButtonState.ERROR
            } else {
                btnState.value = ButtonState.NORMAL
            }
        }
    }

    fun afterTextChangedPassword(editable: Editable?) {
        val password = editable?.toString() ?: ""
        if (isConnectBtnClicked) {
            if (!isPasswordValid(password) || !isEmailValid(email.value)) {
                btnState.value = ButtonState.ERROR
            } else {
                btnState.value = ButtonState.NORMAL
            }
        }
    }

    //Email
    private fun isEmailValid(email: String?): Boolean {
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
            else -> errorEmail.value = null
        }
        return isAllValidate
    }

    //Password
    private fun isPasswordValid(password: String?): Boolean {
        //only need to check empty string or not
        var isAllValidate = true
        when {
            TextUtils.isEmpty(password) -> {
                isAllValidate = false
                errorPassword.value = applicationContext.getString(R.string.error_required_field)
            }
            else -> errorPassword.value = null
        }
        return isAllValidate
    }

    fun onChangePortalType(type: AppPortalType) {
        portalType.value = type
    }
}