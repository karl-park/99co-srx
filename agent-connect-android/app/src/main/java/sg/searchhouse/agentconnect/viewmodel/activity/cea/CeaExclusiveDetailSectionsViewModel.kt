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
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import javax.inject.Inject

class CeaExclusiveDetailSectionsViewModel constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var ceaExclusiveRepository: CeaExclusiveRepository

    val commissionRates = linkedMapOf<String, CeaFormRowPO>()
    val renewalCommissionRates = linkedMapOf<String, CeaFormRowPO>()
    val formType = MutableLiveData<CeaFormType>()
    val template = MutableLiveData<CeaFormTemplatePO>()
    val detailSections = MutableLiveData<ArrayList<Any>>()
    val ceaSubmissionPO = MutableLiveData<CeaFormSubmissionPO>()
    val selfDefinedTerms = MutableLiveData<ArrayList<CeaFormTermPO>>()
    //API Response
    val updateFormResponse: MutableLiveData<ApiStatus<GetFormTemplateResponse>> = MutableLiveData()
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

    fun updateCeaSubmission() {
        actionButtonState.value = ButtonState.SUBMITTING

        val ceaSubmission = ceaSubmissionPO.value ?: return
        val items = detailSections.value ?: return
        val ceaRowValues: Map<String, CeaFormRowPO> =
            items.filterIsInstance<CeaFormRowPO>().associateBy { it.keyValue }

        ceaSubmission.formId = template.value?.formId
        ceaSubmission.formType = formType.value?.value
        ceaSubmission.estateAgent = template.value?.estateAgent
        ceaSubmission.agreementDate =
            ceaRowValues[CeaFormRowTypeKeyValue.AGREEMENT_DATE.value]?.rowValue
        ceaSubmission.commencementDate =
            ceaRowValues[CeaFormRowTypeKeyValue.COMMENCEMENT_DATE.value]?.rowValue
        ceaSubmission.expiryDate = ceaRowValues[CeaFormRowTypeKeyValue.EXPIRY_DATE.value]?.rowValue
        ceaSubmission.price =
            ceaRowValues[CeaFormRowTypeKeyValue.PRICE.value]?.rowValue?.toIntOrNull()
        ceaSubmission.listPrice =
            ceaRowValues[CeaFormRowTypeKeyValue.LIST_PRICE.value]?.rowValue?.toIntOrNull()
        ceaSubmission.priceCommission =
            ceaRowValues[CeaFormRowTypeKeyValue.PRICE_COMMISSION.value]?.rowValue?.toDoubleOrNull()
        ceaSubmission.rateCommission =
            ceaRowValues[CeaFormRowTypeKeyValue.RATE_COMMISSION.value]?.rowValue
        ceaSubmission.gstPayable =
            ceaRowValues[CeaFormRowTypeKeyValue.GST_PAYABLE.value]?.rowValue
        ceaSubmission.gstInclusive =
            ceaRowValues[CeaFormRowTypeKeyValue.GST_INCLUSIVE.value]?.rowValue
        ceaSubmission.cobroke =
            ceaRowValues[CeaFormRowTypeKeyValue.COBROKE.value]?.rowValue
        ceaSubmission.isOwner = ceaRowValues[CeaFormRowTypeKeyValue.IS_OWNER.value]?.isOwner()
        ceaSubmission.conflictOfInterest =
            ceaRowValues[CeaFormRowTypeKeyValue.CONFLICT_OF_INTEREST.value]?.rowValue
        ceaSubmission.estateAgentDisclosure =
            ceaRowValues[CeaFormRowTypeKeyValue.ESTATE_AGENT_DISCLOSURE.value]?.rowValue
        ceaSubmission.leaseDuration =
            ceaRowValues[CeaFormRowTypeKeyValue.LEASE_DURATION.value]?.getLeaseDurationValue()
        ceaSubmission.rentalFrequency =
            ceaRowValues[CeaFormRowTypeKeyValue.RENTAL_FREQUENCY.value]?.getRentalFrequency()
        ceaSubmission.renewalCommissionLiable =
            ceaRowValues[CeaFormRowTypeKeyValue.RENEWAL_COMMISSION_LIABLE.value]?.rowValue?.toBoolean()
        ceaSubmission.renewalCommissionTime =
            ceaRowValues[CeaFormRowTypeKeyValue.RENEWAL_TIME.value]?.rowValue
        ceaSubmission.priceRenewalCommission =
            ceaRowValues[CeaFormRowTypeKeyValue.PRICE_RENEWAL_COMMISSION.value]?.rowValue?.toDoubleOrNull()
        ceaSubmission.rateRenewalCommission =
            ceaRowValues[CeaFormRowTypeKeyValue.RATE_RENEWAL_COMMISSION.value]?.rowValue

        //update self defined terms
        ceaSubmission.termsArray = (selfDefinedTerms.value
            ?: arrayListOf()).map { CeaFormSubmissionPO.Term(term = it.term) }

        createUpdateForm(ceaSubmission)
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
}