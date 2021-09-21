package sg.searchhouse.agentconnect.viewmodel.fragment.calculator

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.data.repository.XValueRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.location.AddressPropertyTypePO
import sg.searchhouse.agentconnect.model.api.location.GetAddressPropertyTypeResponse
import sg.searchhouse.agentconnect.model.api.xvalue.CalculateResponse
import sg.searchhouse.agentconnect.model.api.xvalue.GetProjectResponse
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.event.calculator.PopulateDefaultPropertyTextBoxesEvent
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel
import javax.inject.Inject

class CalculatorPropertyDetailsDialogViewModel constructor(application: Application) :
    ApiBaseViewModel<CalculateResponse>(application) {
    var defaultWalkupResponseData: SearchWithWalkupResponse.Data? = null
    var defaultXValueEntryParams: XValueEntryParams? = null

    fun hasDefaultParams(): Boolean {
        return defaultWalkupResponseData != null && defaultXValueEntryParams != null
    }

    val displayMode: MutableLiveData<SearchCommonViewModel.DisplayMode> =
        MutableLiveData<SearchCommonViewModel.DisplayMode>().apply {
            value = SearchCommonViewModel.DisplayMode.DEFAULT
        }

    val property = MutableLiveData<SearchWithWalkupResponse.Data>()

    val propertyType = MutableLiveData<XValueEnum.PropertyType>()

    // To fix issue where in landed property type, switch propertySubType will change main type to condo
    private val initialPropertySubType = MutableLiveData<ListingEnum.PropertySubType>()

    val propertySubType = MutableLiveData<ListingEnum.PropertySubType>()

    val shouldPerformGetProject = MediatorLiveData<Boolean>()

    val unitFloor = MutableLiveData<String>()
    val unitNumber = MutableLiveData<String>()

    val areaType = MutableLiveData<XValueEnum.AreaType>()
    val tenure = MutableLiveData<XValueEnum.Tenure>()
    val builtYear = MutableLiveData<Int>()

    val area = MutableLiveData<Int>()
    val areaExt = MutableLiveData<Int>()
    val areaGfa = MutableLiveData<Int>()

    val address = MutableLiveData<AddressPropertyTypePO>()

    val shouldShowLandedForm: LiveData<Boolean> = Transformations.map(propertySubType) {
        isSubTypeLanded(it)
    }

    val page = MutableLiveData<Int>()

    val isPropertySubTypeValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
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

    // Check for landed only
    val isAreaTypeValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val isTenureValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }
    val isBuiltYearValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val listItems: MutableLiveData<List<Any>> by lazy {
        MutableLiveData<List<Any>>().apply { value = emptyList() }
    }

    val getProjectResponse = MutableLiveData<GetProjectResponse>()

    val isShowPropertyNotFoundError = MutableLiveData<Boolean>()

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var xValueRepository: XValueRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        mainStatus.value = null
        setupShouldPerformGetProject()
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
                || ListingEnum.PropertyMainType.HDB.propertySubTypes.contains(initialPropertySubType.value)
    }

    fun isCurrentSelectionHdb(): Boolean {
        return propertyType.value == XValueEnum.PropertyType.HDB
                || ListingEnum.PropertyMainType.HDB.propertySubTypes.contains(propertySubType.value)
    }

    fun isCondo(): Boolean {
        return ListingEnum.PropertyMainType.CONDO.propertySubTypes.contains(initialPropertySubType.value)
    }

    fun isLanded(): Boolean {
        return ListingEnum.PropertyMainType.LANDED.propertySubTypes.contains(initialPropertySubType.value)
    }

    private fun isSubTypeLanded(propertySubType: ListingEnum.PropertySubType?): Boolean {
        return ListingEnum.PropertyMainType.LANDED.propertySubTypes.contains(propertySubType)
    }

    fun calculateXValue() {
        if (areParamsValid()) {
            val entryParams = getEntryParams()
            val getProjectResponse =
                getProjectResponse.value
                    ?: return isShowPropertyNotFoundError.postValue(true)
            try {
                performRequest(xValueRepository.calculate(entryParams, getProjectResponse))
            } catch (e: IllegalArgumentException) {
                isShowPropertyNotFoundError.postValue(true)
            }
        }
    }

    // Call only when checked with areParamsValidMethod()
    fun getEntryParams(): XValueEntryParams {
        return XValueEntryParams(
            propertyType = propertyType.value,
            propertySubType = propertySubType.value!!, // checked in areParamsValid()
            unitFloor = unitFloor.value,
            unitNumber = unitNumber.value,
            areaType = areaType.value,
            tenure = tenure.value,
            builtYear = builtYear.value,
            area = area.value!!,
            areaExt = areaExt.value,
            areaGfa = areaGfa.value,
            address = address.value!! // checked in viewModel.areParamsValid()
        )
    }

    private fun areParamsValid(): Boolean {
        isPropertySubTypeValidated.postValue(propertySubType.value != null)
        val propertySubTypeValue = propertySubType.value
        val hasAddress = address.value != null
        val mIsAreaValidated = area.value != null
        isAreaValidated.postValue(mIsAreaValidated)

        return if (propertySubTypeValue != null && isSubTypeLanded(propertySubTypeValue)) {
            // Landed
            val mIsAreaTypeValidated = areaType.value != null
            val mIsTenureValidated = tenure.value != null
            val mIsBuiltYearValidated = builtYear.value != null

            isAreaTypeValidated.postValue(mIsAreaTypeValidated)
            isTenureValidated.postValue(mIsTenureValidated)
            isBuiltYearValidated.postValue(mIsBuiltYearValidated)

            // Set true for all opposites
            isUnitFloorValidated.postValue(true)
            isUnitNumberValidated.postValue(true)

            hasAddress && mIsAreaValidated && mIsAreaTypeValidated && mIsTenureValidated && mIsBuiltYearValidated
        } else {
            // Others
            val mIsPropertySubTypeValueValidated = propertySubTypeValue != null
            val mIsUnitFloorValidated = !TextUtils.isEmpty(unitFloor.value)
            val mIsUnitNumberValidated = !TextUtils.isEmpty(unitNumber.value)


            isUnitFloorValidated.postValue(mIsUnitFloorValidated)
            isUnitNumberValidated.postValue(mIsUnitNumberValidated)
            isAreaValidated.postValue(mIsAreaValidated)

            // Set true for all opposites
            isAreaTypeValidated.postValue(true)
            isTenureValidated.postValue(true)
            isBuiltYearValidated.postValue(true)

            hasAddress && mIsAreaValidated && mIsPropertySubTypeValueValidated && mIsUnitFloorValidated && mIsUnitNumberValidated && mIsAreaValidated
        }
    }

    fun reset() {
        tenure.postValue(null)
        unitFloor.postValue(null)
        unitNumber.postValue(null)

        areaType.postValue(null)
        builtYear.postValue(null)

        area.postValue(null)
        areaExt.postValue(null)
        areaGfa.postValue(null)

        resetValidations()
    }

    fun resetValidations() {
        isUnitFloorValidated.postValue(true)
        isUnitNumberValidated.postValue(true)
        isAreaValidated.postValue(true)

        isAreaTypeValidated.postValue(true)
        isTenureValidated.postValue(true)
        isBuiltYearValidated.postValue(true)
    }

    fun requestPropertyType(property: SearchWithWalkupResponse.Data) {
        locationSearchRepository.getAddressPropertyType(
            property.postalCode,
            true,
            property.buildingNum
        ).performRequest(applicationContext,
            onSuccess = { populateFromAddress(it) },
            onFail = { populateFromAddress(null) },
            onError = { populateFromAddress(null) }
        )
    }

    private fun populateFromAddress(getAddressPropertyTypeResponse: GetAddressPropertyTypeResponse?) {
        val addressPropertyType = getAddressPropertyTypeResponse?.addressPropertyType
        address.postValue(addressPropertyType)
        propertyType.postValue(addressPropertyType?.let {
            XValueEnum.PropertyType.values().find { it.value == addressPropertyType.propertyType }
        })
        initialPropertySubType.postValue(getAddressPropertyTypeResponse?.let {
            when {
                addressPropertyType?.propertySubType ?: 0 > 0 -> {
                    ListingEnum.PropertySubType.values()
                        .find { it.type == addressPropertyType?.propertySubType }
                }
                addressPropertyType?.propertySubTypeList?.isNotEmpty() == true -> {
                    ListingEnum.PropertySubType.values()
                        .find { it.type == addressPropertyType.propertySubTypeList.first() }
                }
                else -> null
            }
        })

        if (hasDefaultParams()) {
            val entryParams = defaultXValueEntryParams!!  // Checked in `hasDefaultParams()`

            propertySubType.postValue(entryParams.propertySubType)
            areaType.postValue(entryParams.areaType)
            tenure.postValue(entryParams.tenure)
            builtYear.postValue(entryParams.builtYear)
            unitFloor.postValue(entryParams.unitFloor)
            unitNumber.postValue(entryParams.unitNumber)

            RxBus.publish(PopulateDefaultPropertyTextBoxesEvent(entryParams))

            defaultWalkupResponseData = null
            defaultXValueEntryParams = null
        } else {
            propertySubType.postValue(getAddressPropertyTypeResponse?.let {
                ListingEnum.PropertySubType.values()
                    .find { it.type == addressPropertyType?.propertySubType }
            })
            reset()
        }
    }

    // Purpose: Populate built area text box
    fun performGetProject() {
        val postalCode = address.value?.postalCode ?: return
        val propertySubType = propertySubType.value ?: return
        val unitFloor = when {
            PropertyTypeUtil.isLanded(propertySubType.type) -> unitFloor.value ?: ""
            else -> unitFloor.value ?: return
        }
        val unitNumber = when {
            PropertyTypeUtil.isLanded(propertySubType.type) -> unitNumber.value ?: ""
            else -> unitNumber.value ?: return
        }
        val buildingNum = address.value?.buildingNum ?: ""
        xValueRepository.getProject(
            postal = postalCode,
            propertySubType = propertySubType,
            floor = unitFloor,
            unit = unitNumber,
            buildingNum = buildingNum
        ).performRequest(applicationContext,
            onSuccess = {
                getProjectResponse.postValue(it)
            }, onFail = {
                // Do nothing
            }, onError = {
                // Do nothing
            })
    }

    override fun shouldResponseBeOccupied(response: CalculateResponse): Boolean {
        return true
    }
}