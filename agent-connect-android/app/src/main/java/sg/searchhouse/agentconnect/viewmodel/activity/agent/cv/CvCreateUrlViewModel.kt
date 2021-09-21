package sg.searchhouse.agentconnect.viewmodel.activity.agent.cv

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.model.api.agent.SaveOrUpdateAgentCvResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class CvCreateUrlViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var agentRepository: AgentRepository

    val baseUrl = MutableLiveData<String>()
    val cvUrl = MutableLiveData<String>()

    val btnState = MutableLiveData<ButtonState>()
    val isSendBtnEnabled: LiveData<Boolean> =
        Transformations.map(btnState) { btnState ->
            return@map when (btnState) {
                ButtonState.SUBMITTING, ButtonState.ERROR -> false
                else -> true
            }

        }
    val sendBtnLabel: LiveData<String> = Transformations.map(btnState) { buttonState ->
        return@map when (buttonState) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_saving)
            else -> applicationContext.getString(R.string.action_save_and_continue)
        }
    }

    val checkCvUrlStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val saveAgentStatus = MutableLiveData<ApiStatus<SaveOrUpdateAgentCvResponse>>()
    val showUnavailableError = MutableLiveData<Boolean>().apply { value = false }

    init {
        viewModelComponent.inject(this)
        setupInitialData()
    }

    private fun setupInitialData() {
        baseUrl.value = "${ApiUtil.getBaseUrl(applicationContext)}/"
        //note: start btn state error -> since want to disable button when page start
        btnState.value = ButtonState.ERROR
    }

    private fun saveAgentCv() {
        val url = cvUrl.value ?: return
        val cv = AgentCvPO(
            appointment = "",
            aboutMe = "",
            testimonials = arrayListOf(),
            showAboutMe = true,
            showListings = false,
            showProfile = true,
            showTestimonials = true,
            showTransactions = false,
            publicProfileUrl = url,
            changedPublicUrlInd = true
        )

        ApiUtil.performRequest(
            applicationContext,
            agentRepository.saveOrUpdateUserAgentCV(cv),
            onSuccess = { response ->
                btnState.postValue(ButtonState.SUBMITTED)
                saveAgentStatus.postValue(ApiStatus.successInstance(response))
            },
            onFail = { apiError ->
                btnState.postValue(ButtonState.NORMAL)
                saveAgentStatus.postValue(ApiStatus.failInstance(apiError))
            },
            onError = {
                btnState.postValue(ButtonState.NORMAL)
                saveAgentStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun checkIsPublicUrlAvailable() {
        if (isValidate()) {
            val url = cvUrl.value ?: return

            btnState.value = ButtonState.SUBMITTING
            ApiUtil.performRequest(
                applicationContext,
                agentRepository.checkIsPublicUrlAvailable(url),
                onSuccess = {
                    if (it.result.toBoolean()) {
                        showUnavailableError.postValue(false)
                        saveAgentCv()
                    } else {
                        showUnavailableError.postValue(true)
                        btnState.postValue(ButtonState.SUBMITTED)
                    }
                },
                onFail = {
                    btnState.postValue(ButtonState.NORMAL)
                    checkCvUrlStatus.postValue(ApiStatus.failInstance(it))
                },
                onError = {
                    btnState.postValue(ButtonState.NORMAL)
                    checkCvUrlStatus.postValue(ApiStatus.errorInstance())
                }
            )
        } else {
            btnState.postValue(ButtonState.ERROR)
        }
    }

    fun afterTextChangedURL(editable: Editable?) {
        val url = editable?.toString() ?: ""
        if (!TextUtils.isEmpty(url.trim())) {
            showUnavailableError.value = false
            btnState.value = ButtonState.NORMAL
        } else {
            btnState.value = ButtonState.ERROR
        }
    }

    private fun isValidate(): Boolean {
        return cvUrl.value?.isNotEmpty() == true
    }
}