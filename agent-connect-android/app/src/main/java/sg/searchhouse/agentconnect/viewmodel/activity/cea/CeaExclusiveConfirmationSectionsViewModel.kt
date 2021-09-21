package sg.searchhouse.agentconnect.viewmodel.activity.cea

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CeaExclusiveRepository
import sg.searchhouse.agentconnect.dependency.component.DaggerViewModelComponent
import sg.searchhouse.agentconnect.dependency.module.AppModule
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSubmissionPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTemplatePO
import sg.searchhouse.agentconnect.model.api.cea.CeaSubmitFormResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import java.io.File
import javax.inject.Inject

class CeaExclusiveConfirmationSectionsViewModel constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var ceaRepository: CeaExclusiveRepository

    val formType = MutableLiveData<CeaExclusiveEnum.CeaFormType>()
    val template = MutableLiveData<CeaFormTemplatePO>()
    val confirmationSections = MutableLiveData<List<Any>>()
    val ceaSubmission = MutableLiveData<CeaFormSubmissionPO>()
    val signatures = MutableLiveData<Map<String, File>>()

    val submitFormResponse = MutableLiveData<ApiStatus<CeaSubmitFormResponse>>()

    private val actionButtonState = MutableLiveData<ButtonState>()
    val isActionButtonEnabled: LiveData<Boolean> =
        Transformations.map(actionButtonState) { buttonState ->
            when (buttonState) {
                ButtonState.SUBMITTING -> false
                else -> true
            }
        }
    val actionButtonLabel: LiveData<String> =
        Transformations.map(actionButtonState) { buttonState ->
            when (buttonState) {
                ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_submitting)
                else -> applicationContext.getString(R.string.action_create_cea_exclusive)
            }
        }

    init {
        val viewModelComponent =
            DaggerViewModelComponent.builder().appModule(AppModule(application)).build()
        viewModelComponent.inject(this)
        actionButtonState.value = ButtonState.NORMAL
    }

    fun createSubmission() {
        val formId = template.value?.formId ?: return
        val estateAgent = template.value?.estateAgent ?: return
        val clients = ceaSubmission.value?.clients ?: return
        val signatureFiles = signatures.value ?: mapOf()

        val signatureItems = ArrayList<File?>()
        signatureItems.add(signatureFiles[estateAgent.getAgentSignatureKey()])
        clients.forEach { client ->
            signatureItems.add(signatureFiles[client.getSignatureKey()])
        }

        actionButtonState.value = ButtonState.SUBMITTING
        submitForm(formId = formId, signatures = signatureItems.filterNotNull())
    }

    private fun submitForm(formId: Int, signatures: List<File>) {
        ApiUtil.performRequest(
            applicationContext,
            ceaRepository.submitForm(formId = formId, files = signatures),
            onSuccess = {
                submitFormResponse.postValue(ApiStatus.successInstance(it))
                actionButtonState.postValue(ButtonState.SUBMITTED)
            },
            onFail = {
                submitFormResponse.postValue(ApiStatus.failInstance(it))
                actionButtonState.postValue(ButtonState.NORMAL)
            },
            onError = {
                submitFormResponse.postValue(ApiStatus.errorInstance())
                actionButtonState.postValue(ButtonState.ERROR)
            }
        )
    }
}