package sg.searchhouse.agentconnect.viewmodel.activity.cea

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CeaExclusiveRepository
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.dependency.component.DaggerViewModelComponent
import sg.searchhouse.agentconnect.dependency.module.AppModule
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormRowTypeKeyValue
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormType
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.model.api.location.GetAddressPropertyTypeResponse
import sg.searchhouse.agentconnect.model.api.location.GetPropertyDataResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CeaExclusiveMainSectionsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetFormTemplateResponse>(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var ceaRepository: CeaExclusiveRepository
    @Inject
    lateinit var lookupRepository: LookupRepository
    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    val sizeParams = listOf(
        CeaFormRowTypeKeyValue.POSTAL_CODE.value,
        CeaFormRowTypeKeyValue.BLOCK_NUMBER.value,
        CeaFormRowTypeKeyValue.CD_RESEARCH_SUB_TYPE.value,
        CeaFormRowTypeKeyValue.UNIT.value,
        CeaFormRowTypeKeyValue.FLOOR.value
    )
    var ceaFormSubmissionPO: CeaFormSubmissionPO? = null
    val formType = MutableLiveData<CeaFormType>()
    val formId = MutableLiveData<Int>()
    val mainSections = MutableLiveData<List<Any>>()
    val formValues = MutableLiveData<Map<String, CeaFormRowPO>>()

    val getAddressByPostalCodeResponse =
        MutableLiveData<ApiStatus<GetAddressPropertyTypeResponse>>()
    val getPropertySizeResponse = MutableLiveData<ApiStatus<GetPropertyDataResponse>>()
    val models = MutableLiveData<HashMap<Int, ArrayList<String>>>()
    val getSizeParams = MutableLiveData<LinkedHashMap<String, String>>()
    val createUpdateFormResponse = MutableLiveData<ApiStatus<GetFormTemplateResponse>>()
    private val actionButtonState = MutableLiveData<ButtonState>()

    val template: LiveData<CeaFormTemplatePO> = Transformations.map(mainResponse) { it?.template }
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

    fun getFormTemplate(formType: CeaFormType) {
        performRequest(ceaRepository.getFormTemplate(formType.value, formId.value))
    }

    fun getAddressByPostalCode(postalCode: Int?) {
        postalCode?.let { value ->
            ApiUtil.performRequest(
                applicationContext,
                ceaRepository.getAddressByPropertyType(value),
                onSuccess = { getAddressByPostalCodeResponse.postValue(ApiStatus.successInstance(it)) },
                onFail = { getAddressByPostalCodeResponse.postValue(ApiStatus.failInstance(it)) },
                onError = { getAddressByPostalCodeResponse.postValue(ApiStatus.errorInstance()) }
            )
        }
    }

    fun getModels() {
        ApiUtil.performRequest(
            applicationContext,
            lookupRepository.lookupModels(),
            onSuccess = { models.postValue(it.models) },
            onFail = { Log.e("CeaExclusive", "Failed in calling lookup models api") },
            onError = { Log.e("CeaExclusive", "Error in calling lookup models api") })
    }

    fun getPropertySize() {
        //getting postal code value
        val postalCodeTemp =
            getSizeParams.value?.get(CeaFormRowTypeKeyValue.POSTAL_CODE.value) ?: ""
        var postalCode = 0
        if (NumberUtil.isNaturalNumber(postalCodeTemp)) {
            postalCode = postalCodeTemp.toInt()
        }
        val blockNumber = getSizeParams.value?.get(CeaFormRowTypeKeyValue.BLOCK_NUMBER.value) ?: ""

        val cdResearchSubTypeTemp =
            getSizeParams.value?.get(CeaFormRowTypeKeyValue.CD_RESEARCH_SUB_TYPE.value) ?: ""
        var cdSearchSubType = 0
        if (NumberUtil.isNaturalNumber(cdResearchSubTypeTemp)) {
            cdSearchSubType = cdResearchSubTypeTemp.toInt()
        }

        val floorNumber = getSizeParams.value?.get(CeaFormRowTypeKeyValue.FLOOR.value) ?: ""
        val unitNumber = getSizeParams.value?.get(CeaFormRowTypeKeyValue.UNIT.value) ?: ""

        ApiUtil.performRequest(
            applicationContext,
            locationSearchRepository.getPropertySize(
                postalCode = postalCode,
                blk = blockNumber,
                cdResearchSubtype = cdSearchSubType,
                floorNo = floorNumber,
                unitNo = unitNumber
            ),
            onSuccess = { getPropertySizeResponse.postValue(ApiStatus.successInstance(it)) },
            onFail = { getPropertySizeResponse.postValue(ApiStatus.failInstance(it)) },
            onError = { getPropertySizeResponse.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun createCeaSubmission() {
        actionButtonState.value = ButtonState.SUBMITTING
        //start initializing data
        val items = mainSections.value ?: listOf()
        val ceaRowValues: Map<String, CeaFormRowPO> =
            items.filterIsInstance<CeaFormRowPO>().associateBy { it.keyValue }

        //Add previous values
        //TODO: temp solution -> need to check with backend again
        val ceaSubmissionPO =
            CeaFormSubmissionPO().createCeaSubmissionFromValues(
                formValues.value ?: mapOf(),
                template.value?.selfDefinedTerms?.map { CeaFormSubmissionPO.Term(term = it.term) }
            )

        //Form ID
        ceaSubmissionPO.formId = formId.value
        //Form Type
        ceaSubmissionPO.formType = formType.value?.value ?: return
        //Trial Submission
        ceaSubmissionPO.test =
            ceaRowValues[CeaFormRowTypeKeyValue.TEST.value]?.getTrialSubmissionValue()
        //Renewal Sequence
        ceaSubmissionPO.renewalSeq =
            ceaRowValues[CeaFormRowTypeKeyValue.RENEWAL.value]?.getRenewalKeyIntByValue()
        //property description
        ceaSubmissionPO.propertyDescription =
            ceaRowValues[CeaFormRowTypeKeyValue.PROPERTY_DESC.value]?.rowValue
        //Estate agent
        ceaSubmissionPO.estateAgent = template.value?.estateAgent
        //room rental
        ceaSubmissionPO.roomRental =
            ceaRowValues[CeaFormRowTypeKeyValue.ROOM_RENTAL.value]?.rowValue?.toBoolean()
        //Cd research sub types
        ceaRowValues[CeaFormRowTypeKeyValue.CD_RESEARCH_SUB_TYPE.value]?.let { subTypeRow ->
            val cdResearchSubTypes = subTypeRow.mapper?.get(subTypeRow.rowValue) ?: ""
            if (NumberUtil.isNaturalNumber(cdResearchSubTypes)) {
                //PropertyType
                ceaSubmissionPO.cdResearchSubtype = cdResearchSubTypes.toInt()
                //Model
                ceaSubmissionPO.model =
                    ceaRowValues[CeaFormRowTypeKeyValue.MODEL.value]?.rowValue
                //POSTAL CODE
                ceaSubmissionPO.postalCode =
                    ceaRowValues[CeaFormRowTypeKeyValue.POSTAL_CODE.value]?.rowValue
                //BUILT MIN
                ceaSubmissionPO.builtMin =
                    ceaRowValues[CeaFormRowTypeKeyValue.BUILT_MIN.value]?.rowValue
                //BEDROOM
                ceaSubmissionPO.bedroomMin =
                    ceaRowValues[CeaFormRowTypeKeyValue.BEDROOM.value]?.getBedroomKeyIntByValue()
                //TENANTED
                ceaSubmissionPO.tenanted =
                    ceaRowValues[CeaFormRowTypeKeyValue.TENANTED.value]?.getTenantedValue()
                //TENANTED AMOUNT
                ceaSubmissionPO.tenantedAmount =
                    ceaRowValues[CeaFormRowTypeKeyValue.TENANTED_AMOUNT.value]?.rowValue?.toIntOrNull()
                        ?: 0
                //VALUATION
                ceaSubmissionPO.valuation =
                    ceaRowValues[CeaFormRowTypeKeyValue.VALUATION.value]?.rowValue?.toIntOrNull()
                        ?: 0
                when {
                    PropertyTypeUtil.isHDB(cdResearchSubTypes.toInt()) -> {
                        //FLOOR
                        ceaSubmissionPO.floor =
                            ceaRowValues[CeaFormRowTypeKeyValue.FLOOR.value]?.rowValue
                        //UNIT
                        ceaSubmissionPO.unit =
                            ceaRowValues[CeaFormRowTypeKeyValue.UNIT.value]?.rowValue
                    }
                    PropertyTypeUtil.isLanded(cdResearchSubTypes.toInt()) -> {
                        //LAND MIN
                        ceaSubmissionPO.landMin =
                            ceaRowValues[CeaFormRowTypeKeyValue.LAND_MIN.value]?.rowValue
                        //TENURE
                        ceaSubmissionPO.tenure =
                            ceaRowValues[CeaFormRowTypeKeyValue.TENURE.value]?.getTenureKeyIntByValue()
                        //NUMBER OF STOREY
                        ceaSubmissionPO.numOfStorey =
                            ceaRowValues[CeaFormRowTypeKeyValue.NUM_OF_STOREY.value]?.getNumberOfStory()
                    }
                    PropertyTypeUtil.isCondo(cdResearchSubTypes.toInt()) -> {
                        //FLOOR
                        ceaSubmissionPO.floor =
                            ceaRowValues[CeaFormRowTypeKeyValue.FLOOR.value]?.rowValue
                        //UNIT
                        ceaSubmissionPO.unit =
                            ceaRowValues[CeaFormRowTypeKeyValue.UNIT.value]?.rowValue
                        //TENURE
                        ceaSubmissionPO.tenure =
                            ceaRowValues[CeaFormRowTypeKeyValue.TENURE.value]?.getTenureKeyIntByValue()
                    }
                }
            }
        }
        createUpdateForm(ceaSubmissionPO)
    }

    private fun createUpdateForm(ceaFormSubmissionPO: CeaFormSubmissionPO) {
        ApiUtil.performRequest(applicationContext,
            ceaRepository.createUpdateForm(ceaFormSubmissionPO),
            onSuccess = { response ->
                this.ceaFormSubmissionPO = ceaFormSubmissionPO
                createUpdateFormResponse.postValue(
                    ApiStatus.successInstance(
                        response
                    )
                )
                actionButtonState.postValue(ButtonState.SUBMITTED)
            },
            onFail = {
                createUpdateFormResponse.postValue(ApiStatus.failInstance(it))
                actionButtonState.postValue(ButtonState.NORMAL)
            },
            onError = {
                createUpdateFormResponse.postValue(ApiStatus.errorInstance())
                actionButtonState.postValue(ButtonState.ERROR)
            }
        )
    }

    override fun shouldResponseBeOccupied(response: GetFormTemplateResponse): Boolean {
        return true
    }
}