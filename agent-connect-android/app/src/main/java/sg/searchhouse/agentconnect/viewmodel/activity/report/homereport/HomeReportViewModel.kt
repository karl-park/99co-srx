package sg.searchhouse.agentconnect.viewmodel.activity.report.homereport

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.data.repository.HomeReportRepository
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.data.repository.XValueRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.HomeReportEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.agent.GetAgentDetailsResponse
import sg.searchhouse.agentconnect.model.api.homereport.GenerateHomeReportRequestBody
import sg.searchhouse.agentconnect.model.api.homereport.GetHomeReportUsageResponse
import sg.searchhouse.agentconnect.model.api.location.AddressPropertyTypePO
import sg.searchhouse.agentconnect.model.api.xvalue.GetProjectResponse
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.ExistingHomeReport
import sg.searchhouse.agentconnect.service.GenerateHomeReportService
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel
import java.io.IOException
import javax.inject.Inject

class HomeReportViewModel constructor(application: Application) : CoreViewModel(application) {
    val displayMode: MutableLiveData<SearchCommonViewModel.DisplayMode> =
        MutableLiveData<SearchCommonViewModel.DisplayMode>().apply {
            value = SearchCommonViewModel.DisplayMode.DEFAULT
        }

    val property = MutableLiveData<SearchWithWalkupResponse.Data>()

    val propertyType = MutableLiveData<XValueEnum.PropertyType>()

    // To fix issue where in landed property type, switch propertySubType will change main type to condo
    private val initialPropertySubType = MutableLiveData<ListingEnum.PropertySubType>()

    val propertySubType = MutableLiveData<ListingEnum.PropertySubType>()

    val tenureType =
        MutableLiveData<HomeReportEnum.Tenure>().apply { value = HomeReportEnum.Tenure.LEASEHOLD }

    val shouldPerformGetProject = MediatorLiveData<Boolean>()

    val unitFloor = MutableLiveData<String>()
    val unitNumber = MutableLiveData<String>()

    val area = MutableLiveData<Int>()

    val address = MutableLiveData<AddressPropertyTypePO>()

    val clientName = MutableLiveData<String>()

    val isPropertyTypeValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val isPropertySubTypeValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val isTenureTypeValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val isTenureTypeApplicable: LiveData<Boolean> = Transformations.map(propertySubType) {
        val mainType = PropertyTypeUtil.getPropertyMainType(it.type)
        mainType == ListingEnum.PropertyMainType.LANDED || mainType == ListingEnum.PropertyMainType.COMMERCIAL
    }

    // Check for non-landed only
    val isUnitFloorValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }
    val isUnitNumberValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }
    val isAreaValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val listItems: MutableLiveData<List<Any>> by lazy {
        MutableLiveData<List<Any>>().apply { value = emptyList() }
    }

    val getProjectResponse = MutableLiveData<GetProjectResponse>()

    private val getAgentDetailsResponse = MutableLiveData<GetAgentDetailsResponse>()

    val reportLocalFileName = MutableLiveData<String>()

    val isReportGenerated = MutableLiveData<Boolean>()

    val displayAddress = MutableLiveData<String>()

    // Field values of existing home report
    private var existingDetails: ExistingHomeReport.Details? = null

    val getHomeReportUsageResponse = MutableLiveData<GetHomeReportUsageResponse>()

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var xValueRepository: XValueRepository

    @Inject
    lateinit var homeReportRepository: HomeReportRepository

    @Inject
    lateinit var agentRepository: AgentRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupShouldPerformGetProject()
        performGetAgentDetails()
        performGetHomeReportUsage()
    }

    fun performGetHomeReportUsage() {
        homeReportRepository.getHomeReportUsage().performRequest(
            applicationContext,
            onSuccess = {
                getHomeReportUsageResponse.postValue(it)
            }, onFail = {
                // Do nothing
            }, onError = {
                // Do nothing
            })
    }

    private fun shouldGetProject(): Boolean {
        val propertySubType = propertySubType.value ?: return false
        val hasFloorAndUnit =
            !TextUtils.isEmpty(unitFloor.value) && !TextUtils.isEmpty(unitNumber.value)
        val isLanded = PropertyTypeUtil.isLanded(propertySubType.type)
        return address.value != null && (isLanded || hasFloorAndUnit)
    }

    private fun setupShouldPerformGetProject() {
        shouldPerformGetProject.addSource(address) {
            shouldPerformGetProject.postValue(shouldGetProject())
        }

        shouldPerformGetProject.addSource(unitFloor) {
            shouldPerformGetProject.postValue(shouldGetProject())
        }

        shouldPerformGetProject.addSource(unitNumber) {
            shouldPerformGetProject.postValue(shouldGetProject())
        }

        shouldPerformGetProject.addSource(propertySubType) {
            shouldPerformGetProject.postValue(shouldGetProject())
        }
    }

    fun isHdb(): Boolean {
        return propertyType.value == XValueEnum.PropertyType.HDB
                || ListingEnum.PropertyMainType.HDB.propertySubTypes.contains(propertySubType.value)
    }

    fun isCurrentSelectionHdb(): Boolean {
        return propertyType.value == XValueEnum.PropertyType.HDB
                || ListingEnum.PropertyMainType.HDB.propertySubTypes.contains(propertySubType.value)
    }

    fun isCondo(): Boolean {
        return ListingEnum.PropertyMainType.CONDO.propertySubTypes.contains(propertySubType.value)
    }

    fun isLanded(): Boolean {
        return ListingEnum.PropertyMainType.LANDED.propertySubTypes.contains(propertySubType.value)
    }

    fun areParamsValid(): Boolean {
        val mIsPropertyTypeValidated = isPropertyTypeValidated.value == true

        isPropertySubTypeValidated.postValue(propertySubType.value != null)
        val propertySubTypeValue = propertySubType.value
        val hasAddress = address.value != null
        val mIsAreaValidated = area.value != null && (area.value ?: 0) > 0
        isAreaValidated.postValue(mIsAreaValidated)

        val mIsPropertySubTypeValueValidated = propertySubTypeValue != null
        val mIsUnitFloorValidated = !TextUtils.isEmpty(unitFloor.value) || isLanded() || isCondo()
        val mIsUnitNumberValidated = !TextUtils.isEmpty(unitNumber.value) || isLanded() || isCondo()

        isUnitFloorValidated.postValue(mIsUnitFloorValidated)
        isUnitNumberValidated.postValue(mIsUnitNumberValidated)
        isAreaValidated.postValue(mIsAreaValidated)

        return mIsPropertyTypeValidated && hasAddress && mIsAreaValidated && mIsPropertySubTypeValueValidated && mIsUnitFloorValidated && mIsUnitNumberValidated && mIsAreaValidated
    }

    private fun resetForm() {
        unitFloor.postValue(null)
        unitNumber.postValue(null)
        area.postValue(null)
        resetValidations()
    }

    fun resetValidations() {
        isUnitFloorValidated.postValue(true)
        isUnitNumberValidated.postValue(true)
        isAreaValidated.postValue(true)
    }

    fun requestPropertyType(property: SearchWithWalkupResponse.Data) {
        ApiUtil.performRequest(
            applicationContext,
            locationSearchRepository.getAddressPropertyType(
                property.postalCode,
                false,
                property.buildingNum
            ),
            onSuccess = { populatePropertyAddressAndTypeResetForm(it.addressPropertyType) },
            onFail = { populatePropertyAddressAndTypeResetForm(null) },
            onError = { populatePropertyAddressAndTypeResetForm(null) }
        )
    }

    private fun populatePropertyAddressType(
        addressPropertyType: AddressPropertyTypePO?,
        isAfterGenerateReport: Boolean = false
    ) {
        address.postValue(addressPropertyType)
        val thisPropertyType =
            XValueEnum.PropertyType.values().find { it.value == addressPropertyType?.propertyType }
        propertyType.postValue(thisPropertyType)
        isPropertyTypeValidated.postValue(thisPropertyType != null || isAfterGenerateReport)

        val preSelectedPropertySubType = when {
            addressPropertyType?.propertySubType ?: 0 > 0 -> {
                ListingEnum.PropertySubType.values()
                    .find { it.type == addressPropertyType?.propertySubType }
            }
            addressPropertyType?.propertySubTypeList?.isNotEmpty() == true -> {
                ListingEnum.PropertySubType.values()
                    .find { it.type == addressPropertyType.propertySubTypeList.first() }
            }
            thisPropertyType != null -> {
                when (thisPropertyType) {
                    XValueEnum.PropertyType.HDB -> {
                        ListingEnum.PropertyMainType.HDB.propertySubTypes.first()
                    }
                    XValueEnum.PropertyType.PRIVATE_RESIDENTIAL -> {
                        ListingEnum.PropertyMainType.CONDO.propertySubTypes.first()
                    }
                    XValueEnum.PropertyType.COMMERCIAL_INDUSTRIAL -> {
                        ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes.first()
                    }
                    XValueEnum.PropertyType.ANY -> null
                }
            }
            else -> null
        } ?: ListingEnum.PropertyMainType.CONDO.propertySubTypes.first()

        initialPropertySubType.postValue(preSelectedPropertySubType)
        propertySubType.postValue(preSelectedPropertySubType)

        // Haven't submit form, so assume it valid
        isPropertySubTypeValidated.postValue(true)
    }

    fun populatePropertyAddressAndTypeResetForm(
        addressPropertyType: AddressPropertyTypePO?,
        isAfterGenerateReport: Boolean = false
    ) {
        populatePropertyAddressType(addressPropertyType, isAfterGenerateReport)
        existingDetails?.run {
            populateForm(this)
            existingDetails = null // Reset after use
        } ?: resetForm()
    }

    fun performGetProject() {
        val postalCode = address.value?.postalCode ?: return
        val propertySubType = propertySubType.value ?: return
        val unitFloor = when {
            PropertyTypeUtil.isLanded(propertySubType.type) -> unitFloor.value ?: ""
            PropertyTypeUtil.isCondo(propertySubType.type) -> unitFloor.value ?: ""
            else -> unitFloor.value ?: return
        }
        val unitNumber = when {
            PropertyTypeUtil.isLanded(propertySubType.type) -> unitNumber.value ?: ""
            PropertyTypeUtil.isCondo(propertySubType.type) -> unitNumber.value ?: ""
            else -> unitNumber.value ?: return
        }
        val buildingNum = address.value?.buildingNum ?: ""

        ApiUtil.performRequest(applicationContext, xValueRepository.getProject(
            postal = postalCode,
            propertySubType = propertySubType,
            floor = unitFloor,
            unit = unitNumber,
            buildingNum = buildingNum
        ), onSuccess = {
            getProjectResponse.postValue(it)
        }, onFail = {
            // Do nothing
        }, onError = {
            // Do nothing
        })
    }

    private fun performGetAgentDetails() {
        ApiUtil.performRequest(applicationContext, agentRepository.getAgentDetails(), onSuccess = {
            getAgentDetailsResponse.postValue(it)
        }, onFail = {
            // Do nothing
        }, onError = {
            // Do nothing
        })
    }

    // TODO Download from background
    fun downloadReport(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val timeStamp = DateTimeUtil.getCurrentFileNameTimeStamp()
                val fileName = "home_report_$timeStamp.pdf"
                try {
                    IntentUtil.downloadFile(
                        applicationContext, okHttpClient, url, fileName,
                        AppConstant.DIR_HOME_REPORT
                    )
                } catch (e: IOException) {
                    return@withContext ErrorUtil.handleError(applicationContext.getString(R.string.toast_download_report_failed_generic))
                }
                reportLocalFileName.postValue(fileName)
            }
        }
    }

    fun generateReport() {
        val property = property.value ?: return

        val firstProject =
            getProjectResponse.value?.data?.firstOrNull() ?: return ViewUtil.showMessage(
                R.string.toast_home_report_project_not_found
            )
        val agentPO =
            getAgentDetailsResponse.value?.data ?: return ErrorUtil.handleError("Missing agentPO")

        val address = address.value
            ?: return ViewUtil.showMessage(R.string.toast_home_report_address_not_found)

        val unitFloor = unitFloor.value
        val unitNumber = unitNumber.value
        val propertySubType = propertySubType.value ?: return
        val area = area.value ?: return

        val id = firstProject.streetId
        val agency =
            agentPO.agencyId?.toString() ?: return ErrorUtil.handleError("Missing agency ID")
        val block = firstProject.block
        val cdResearchSubtype = propertySubType.type
        val areaUnit = if (PropertyTypeUtil.isHDB(cdResearchSubtype)) {
            "sqm"
        } else {
            "sqft"
        }

        val isMissingFloorOrUnit = StringUtil.isEmpty(unitFloor) || StringUtil.isEmpty(unitNumber)
        var unit = if (PropertyTypeUtil.isLanded(propertySubType.type) && isMissingFloorOrUnit) {
            "($area$areaUnit)"
        } else {
            "floor${unitFloor}unit${unitNumber} ($area$areaUnit)"
        }

        if (isTenureTypeApplicable.value == true) {
            tenureType.value?.run { unit += " [$value]" }
        }

        val streetKey = address.streetKey

        val clientName = clientName.value

        val requestBody =
            GenerateHomeReportRequestBody(
                id,
                agency,
                block,
                cdResearchSubtype,
                unit,
                streetKey,
                address.streetName,
                clientName
            )

        val details = ExistingHomeReport.Details(floor = unitFloor, unit = unitNumber, area = area)
        GenerateHomeReportService.launch(
            applicationContext, requestBody,
            property,
            details
        )
        ViewUtil.showMessage(R.string.toast_generating_home_report)

        isReportGenerated.postValue(true)
    }

    // Set property address, type, and form from history
    fun populatePropertyAddressTypeAndForm(
        existingHomeReport: ExistingHomeReport
    ) {
        existingDetails = existingHomeReport.details // Set form values of existing home report
        property.postValue(existingHomeReport.walkupResponseData)
    }

    // Auto-populate text fields from the history
    private fun populateForm(homeReportDetails: ExistingHomeReport.Details) {
        unitFloor.postValue(homeReportDetails.floor)
        unitNumber.postValue(homeReportDetails.unit)
        area.postValue(homeReportDetails.area)
        resetValidations()
    }

    fun getAvailableSubTypes(): List<ListingEnum.PropertySubType> {
        return ListingEnum.PropertyMainType.HDB.propertySubTypes +
                ListingEnum.PropertyMainType.CONDO.propertySubTypes +
                ListingEnum.PropertyMainType.LANDED.propertySubTypes +
                ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes
    }
}