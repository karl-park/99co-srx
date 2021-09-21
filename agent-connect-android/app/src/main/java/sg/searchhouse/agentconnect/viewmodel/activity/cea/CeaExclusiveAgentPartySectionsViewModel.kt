package sg.searchhouse.agentconnect.viewmodel.activity.cea

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CeaExclusiveRepository
import sg.searchhouse.agentconnect.dependency.component.DaggerViewModelComponent
import sg.searchhouse.agentconnect.dependency.module.AppModule
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormRowTypeKeyValue.*
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.model.api.location.GetAddressPropertyTypeResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import javax.inject.Inject


class CeaExclusiveAgentPartySectionsViewModel constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var ceaExclusiveRepository: CeaExclusiveRepository

    var representerKeyValues = listOf(
        REPRESENTER_NAME.value,
        REPRESENTER_NRIC.value,
        REPRESENTER_CONTACT.value,
        REPRESENTER_EMAIL.value,
        REPRESNETER_POSTAL_CODE.value,
        REPRESENTER_ADDRESS.value,
        REPRESENTER_BLOCK_NUM.value,
        REPRESENTER_FLOOR.value,
        REPRESENTER_UNIT.value
    )
    var ceaSection: CeaSection? = null
    var ceaClient: CeaFormClientPO? = null
    var ceaClientSignatureKey: String? = null
    var ceaAgentSignatureKey: String? = null
    var ceaEstateAgent: CeaFormEstateAgentPO? = null
    var selectedPostalCodeType: CeaFormRowTypeKeyValue? = null
    val formType = MutableLiveData<CeaFormType>()
    val template = MutableLiveData<CeaFormTemplatePO>()
    val agentPartySections = MutableLiveData<ArrayList<Any>>()
    val ceaSubmission = MutableLiveData<CeaFormSubmissionPO>()
    private val actionButtonState = MutableLiveData<ButtonState>()

    val updateFormResponse = MutableLiveData<ApiStatus<GetFormTemplateResponse>>()
    val getAddressResponse = MutableLiveData<ApiStatus<GetAddressPropertyTypeResponse>>()
    val isActionButtonEnabled: LiveData<Boolean> =
        Transformations.map(actionButtonState) { buttonState ->
            return@map when (buttonState) {
                ButtonState.SUBMITTING -> false
                else -> true
            }
        }
    val actionButtonLabel: LiveData<String> =
        Transformations.map(actionButtonState) { buttonState ->
            return@map when (buttonState) {
                ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_saving)
                else -> applicationContext.getString(R.string.action_save_and_continue)
            }
        }

    val hasSignature = MutableLiveData<Boolean>().apply { value = false }
    val signaturePair = MutableLiveData<Pair<String, Bitmap>>()
    val isEstateAgentPO = MutableLiveData<Boolean>()
    val imageBitmap: LiveData<Bitmap> =
        Transformations.map(signaturePair) { signaturePair -> signaturePair.second }

    init {
        val viewModelComponent =
            DaggerViewModelComponent.builder().appModule(AppModule(application)).build()
        viewModelComponent.inject(this)
        actionButtonState.value = ButtonState.NORMAL
    }

    fun getAddressByPostalCode(postalCode: Int?) {
        val value = postalCode ?: return
        ApiUtil.performRequest(
            applicationContext,
            ceaExclusiveRepository.getAddressByPropertyType(value),
            onSuccess = { getAddressResponse.postValue(ApiStatus.successInstance(it)) },
            onFail = { getAddressResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { getAddressResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun createCeaSubmission() {
        val section = ceaSection ?: return
        if (section == CeaSection.PARTY_SECTION) {
            submissionPartySections()
        } else if (section == CeaSection.AGENT_SECTION) {
            submissionAgentSections()
        }
    }

    private fun submissionPartySections() {
        val clientList = template.value?.clients?.toMutableList() ?: return
        val submissionPO = ceaSubmission.value ?: return
        val items = agentPartySections.value ?: return
        val ceaRowValues = items.filterIsInstance<CeaFormRowPO>().associateBy { it.keyValue }

        val newCeaClient = CeaFormClientPO(
            partyType = ceaRowValues[PARTY_TYPE.value]?.rowValue ?: "",
            partyNric = ceaRowValues[PARTY_NRIC.value]?.rowValue ?: "",
            partyName = ceaRowValues[PARTY_NAME.value]?.rowValue ?: "",
            partyContact = ceaRowValues[PARTY_CONTACT.value]?.rowValue ?: "",
            partyEmail = ceaRowValues[PARTY_EMAIL.value]?.rowValue ?: "",
            partyAddress = ceaRowValues[PARTY_ADDRESS.value]?.rowValue ?: "",
            representerContact = ceaRowValues[REPRESENTER_CONTACT.value]?.rowValue ?: "",
            representerEmail = ceaRowValues[REPRESENTER_EMAIL.value]?.rowValue ?: "",
            representerAddress = ceaRowValues[REPRESENTER_ADDRESS.value]?.rowValue ?: "",
            representerName = ceaRowValues[REPRESENTER_NAME.value]?.rowValue ?: "",
            representerNric = ceaRowValues[REPRESENTER_NRIC.value]?.rowValue ?: "",
            representing = ceaRowValues[REPRESENTING_SELLER.value]?.rowValue ?: "",
            nationality = ceaRowValues[NATIONALITY.value]?.rowValue ?: "",
            partyRace = ceaRowValues[PARTY_RACE.value]?.rowValue ?: "",
            salutation = ceaRowValues[SALUTATION.value]?.rowValue ?: "",
            citizenship = ceaRowValues[CITIZENSHIP.value]?.rowValue ?: "",
            postalCode = ceaRowValues[POSTAL_CODE.value]?.rowValue ?: "",
            address = ceaRowValues[ADDRESS.value]?.rowValue ?: "",
            blkNo = ceaRowValues[BLK_NO.value]?.rowValue ?: "",
            floor = ceaRowValues[FLOOR.value]?.rowValue ?: "",
            unit = ceaRowValues[UNIT.value]?.rowValue ?: "",
            representerPostalCode = ceaRowValues[REPRESNETER_POSTAL_CODE.value]?.rowValue ?: "",
            representerBlkNo = ceaRowValues[REPRESENTER_BLOCK_NUM.value]?.rowValue ?: "",
            representerFloor = ceaRowValues[REPRESENTER_FLOOR.value]?.rowValue ?: "",
            representerUnit = ceaRowValues[REPRESENTER_UNIT.value]?.rowValue ?: "",
            isCompany = ceaRowValues[IS_COMPANY.value]?.rowValue ?: "",
            partySignature = "",
            photoUrl = ""
        )
        ceaClientSignatureKey = newCeaClient.getSignatureKey()

        //Clear first and then all add
        submissionPO.clients?.clear()

        if (ceaClient == null) {
            submissionPO.clients?.addAll(clientList)
            submissionPO.clients?.add(newCeaClient)
        } else {
            val nric = ceaClient?.partyNric ?: return
            if (!TextUtils.isEmpty(nric)) {
                val position = clientList.indexOfFirst { it.partyNric == nric }
                //replace object by position
                clientList[position] = newCeaClient
                submissionPO.clients?.addAll(clientList)
            }
        }
        createUpdateForm(submissionPO)
    }

    private fun submissionAgentSections() {
        val submissionPO = ceaSubmission.value ?: return
        val estateAgentPO = ceaSubmission.value?.estateAgent ?: return
        val items = agentPartySections.value ?: return
        val ceaRowValues: Map<String, CeaFormRowPO> =
            items.filterIsInstance<CeaFormRowPO>().associateBy { it.keyValue }

        estateAgentPO.cdAgency = ceaRowValues[AGENT_CD_AGENCY.value]?.rowValue ?: ""
        estateAgentPO.ceaRegNo = ceaRowValues[AGENT_CEA_REG_NO.value]?.rowValue ?: ""
        estateAgentPO.partyAgencyAddress = ceaRowValues[PARTY_AGENCY_ADDRESS.value]?.rowValue ?: ""
        estateAgentPO.partyAgencyLicense = ceaRowValues[PARTY_AGENCY_LICENSE.value]?.rowValue ?: ""
        estateAgentPO.partyAgencyName = ceaRowValues[PARTY_AGENCY_NAME.value]?.rowValue ?: ""
        estateAgentPO.partyContact = ceaRowValues[AGENT_PARTY_CONTACT.value]?.rowValue ?: ""
        estateAgentPO.partyName = ceaRowValues[AGENT_PARTY_NAME.value]?.rowValue ?: ""
        estateAgentPO.partyNric = ceaRowValues[AGENT_PARTY_NRIC.value]?.rowValue ?: ""
        estateAgentPO.partyType = ceaRowValues[AGENT_PARTY_TYPE.value]?.rowValue ?: ""

        ceaAgentSignatureKey = estateAgentPO.getAgentSignatureKey()

        submissionPO.estateAgent = estateAgentPO

        createUpdateForm(submissionPO)
    }

    private fun createUpdateForm(ceaFormSubmissionPO: CeaFormSubmissionPO) {
        actionButtonState.value = ButtonState.SUBMITTING
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