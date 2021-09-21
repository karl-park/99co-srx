package sg.searchhouse.agentconnect.viewmodel.fragment.auth

import android.app.Application
import android.content.Context
import android.widget.CompoundButton
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.model.api.auth.ScheduleCallbackRequest
import sg.searchhouse.agentconnect.model.api.auth.ScheduleCallbackResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class NonSubscriberViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var applicationContext: Context

    var selectedDate: String = ""
    var selectedTime: String = ""
    val displayDate = MutableLiveData<String>()
    val displayTime = MutableLiveData<String>()

    val isPrivacyPolicyChecked = MutableLiveData<Boolean>()
    val buttonState = MutableLiveData<ButtonState>()
    val actionName = MutableLiveData<String>()
    val purpose = MutableLiveData<Purpose>()

    val scheduleCallbackShowed = MutableLiveData<Boolean>().apply { value = false }
    val scheduleCallbackStatus = MutableLiveData<ApiStatus<ScheduleCallbackResponse>>()
    val allowDismiss = MutableLiveData<Boolean>().apply { value = false }
    val module = MutableLiveData<AccessibilityEnum.AdvisorModule>()
    val msgNonSubscriberPrompt = MediatorLiveData<String>()

    init {
        viewModelComponent.inject(this)
        setupInitialData()
        setupMsgNonSubscriberPrompt()
    }

    private fun setupInitialData() {
        isPrivacyPolicyChecked.postValue(false)
        buttonState.postValue(ButtonState.SUBMITTING)
        actionName.postValue(applicationContext.getString(R.string.action_submit))
    }

    private fun setupMsgNonSubscriberPrompt() {
        msgNonSubscriberPrompt.addSource(module) { populateMsgNonSubscriberPrompt() }
    }

    private fun populateMsgNonSubscriberPrompt() {
        val message = when {
            module.value != null -> {
                applicationContext.getString(R.string.msg_subscription_level)
            }
            else -> {
                applicationContext.getString(R.string.msg_non_subscriber_dialog)
            }
        }
        msgNonSubscriberPrompt.postValue(message)
    }

    fun needHelpToBeSubscriber() {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.scheduleCallback(
                ScheduleCallbackRequest(
                    epochTimeSeconds = 0L,
                    purpose = Purpose.HELP_I_AM_A_SUBSCRIBER.value
                )
            ),
            onSuccess = { scheduleCallbackStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { scheduleCallbackStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { scheduleCallbackStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun submit(purpose: Int) {
        val isChecked = isPrivacyPolicyChecked.value ?: false
        if (isChecked) {
            buttonState.postValue(ButtonState.SUBMITTING)
            actionName.postValue(applicationContext.getString(R.string.action_submitting))

            ApiUtil.performRequest(
                applicationContext,
                authRepository.scheduleCallback(
                    ScheduleCallbackRequest(
                        DateTimeUtil.getUnixTimeStamp(
                            "$selectedDate $selectedTime",
                            DateTimeUtil.FORMAT_DATE_TIME_FULL
                        ),
                        purpose
                    )
                ),
                onSuccess = { response ->
                    buttonState.postValue(ButtonState.SUBMITTED)
                    actionName.postValue(applicationContext.getString(R.string.action_submit))
                    scheduleCallbackStatus.postValue(ApiStatus.successInstance(response))
                },
                onFail = { apiError ->
                    buttonState.postValue(ButtonState.NORMAL)
                    actionName.postValue(applicationContext.getString(R.string.action_submit))
                    scheduleCallbackStatus.postValue(ApiStatus.failInstance(apiError))
                },
                onError = {
                    buttonState.postValue(ButtonState.NORMAL)
                    actionName.postValue(applicationContext.getString(R.string.action_submit))
                    scheduleCallbackStatus.postValue(ApiStatus.errorInstance())
                }
            )
        }
    }

    fun onCheckedChanged(btnView: CompoundButton, isChecked: Boolean) {
        if (btnView.isPressed) {
            //Disable enable submit button depend on check box
            if (isChecked) {
                buttonState.postValue(ButtonState.NORMAL)
            } else {
                buttonState.postValue(ButtonState.SUBMITTING)
            }
            actionName.postValue(applicationContext.getString(R.string.action_submit))
            isPrivacyPolicyChecked.value = isChecked
        }
    }

    fun onClickScheduleCallback() {
        scheduleCallbackShowed.value = scheduleCallbackShowed.value != true
        if (scheduleCallbackShowed.value == false) {
            purpose.postValue(null)
        } else {
            purpose.postValue(Purpose.BECOME_A_SUBSCRIBER)
        }
    }

    enum class Purpose(val value: Int) {
        BECOME_A_SUBSCRIBER(0),
        HELP_I_AM_A_SUBSCRIBER(1)
    }
}