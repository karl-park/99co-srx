package sg.searchhouse.agentconnect.viewmodel.activity.cea

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CeaExclusiveRepository
import sg.searchhouse.agentconnect.dependency.component.DaggerViewModelComponent
import sg.searchhouse.agentconnect.dependency.module.AppModule
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import javax.inject.Inject

class CeaExclusiveAgentOwnerSectionsViewModel constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var ceaExclusiveRepository: CeaExclusiveRepository

    var removedClientPosition: Int? = null
    val formType = MutableLiveData<CeaExclusiveEnum.CeaFormType>()
    val template = MutableLiveData<CeaFormTemplatePO>()
    val ceaSubmission = MutableLiveData<CeaFormSubmissionPO>()
    val clients = MutableLiveData<List<CeaFormClientPO>>()
    val partyTerms = MutableLiveData<List<CeaFormSectionPO>>()
    val signatureMaps = MutableLiveData<LinkedHashMap<String, Bitmap>>()
    val agentSignature = MutableLiveData<Bitmap>()
    val isAgentSignatureAdded = MutableLiveData<Boolean>()

    val isAddOwnerDetailShowed: LiveData<Boolean> = Transformations.map(template) {
        it?.let { ceaTemplate ->
            ceaTemplate.clients.size < 5
        } ?: true
    }

    val estateAgent: LiveData<CeaFormEstateAgentPO> =
        Transformations.map(template) {
            it?.estateAgent
        }
    val updateFormResponse = MutableLiveData<ApiStatus<GetFormTemplateResponse>>()
    val deleteClientResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    private val actionButtonState: MutableLiveData<ButtonState> = MutableLiveData()
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
                ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_saving)
                else -> applicationContext.getString(R.string.action_save_and_continue)
            }
        }

    init {
        val viewModelComponent =
            DaggerViewModelComponent.builder().appModule(AppModule(application)).build()
        viewModelComponent.inject(this)
        actionButtonState.value = ButtonState.NORMAL
    }

    fun createFormSubmission() {
        val submission = ceaSubmission.value ?: return
        val ceaClients = clients.value ?: listOf()

        //update clients first
        submission.clients?.clear()
        submission.clients?.addAll(ceaClients)

        //update button state
        actionButtonState.value = ButtonState.SUBMITTING

        createUpdateForm(submission)
    }

    private fun createUpdateForm(ceaFormSubmissionPO: CeaFormSubmissionPO) {
        ApiUtil.performRequest(
            applicationContext,
            ceaExclusiveRepository.createUpdateForm(ceaFormSubmissionPO),
            onSuccess = {
                updateFormResponse.postValue(ApiStatus.successInstance(it))
                actionButtonState.postValue(ButtonState.SUBMITTED)
            },
            onFail = {
                updateFormResponse.postValue(ApiStatus.failInstance(it))
                actionButtonState.postValue(ButtonState.NORMAL)
            },
            onError = {
                updateFormResponse.postValue(ApiStatus.errorInstance())
                actionButtonState.postValue(ButtonState.ERROR)
            }
        )
    }

    fun deleteClient(position: Int) {
        val formId = template.value?.formId ?: return
        ApiUtil.performRequest(
            applicationContext,
            ceaExclusiveRepository.deleteClient(formId, position),
            onSuccess = { deleteClientResponse.postValue(ApiStatus.successInstance(it)) },
            onFail = { deleteClientResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { deleteClientResponse.postValue(ApiStatus.errorInstance()) }
        )
    }
}
