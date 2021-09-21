package sg.searchhouse.agentconnect.viewmodel.fragment.search

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
import sg.searchhouse.agentconnect.model.api.location.AddressPropertyTypePO
import sg.searchhouse.agentconnect.model.api.location.GetAddressPropertyTypeResponse
import sg.searchhouse.agentconnect.model.api.xvalue.GetExistingXValuesResponse
import sg.searchhouse.agentconnect.model.api.xvalue.GetProjectResponse
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SearchXValueViewModel constructor(application: Application) :
    ApiBaseViewModel<GetExistingXValuesResponse>(application) {
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

    val sort: MutableLiveData<XValueEnum.GetExistingXValuesOrder> by lazy {
        MutableLiveData<XValueEnum.GetExistingXValuesOrder>().apply {
            value = XValueEnum.GetExistingXValuesOrder.DESC
        }
    }

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var xValueRepository: XValueRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupShouldPerformGetProject()
    }

    private fun shouldGetProject(): Boolean {
        val propertySubType = propertySubType.value ?: return false
        val hasFloorAndUnit =
            !TextUtils.isEmpty(unitFloor.value) && !TextUtils.isEmpty(unitNumber.value)
        val isLanded = PropertyTypeUtil.isLanded(propertySubType.type)
        val isCondo = PropertyTypeUtil.isCondo(propertySubType.type)
        return address.value != null && (isLanded || isCondo || hasFloorAndUnit)
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

    fun areParamsValid(): Boolean {
        isPropertySubTypeValidated.postValue(propertySubType.value != null)

        val mIsAreaValidated = (area.value ?: 0) > 0
        isAreaValidated.postValue(mIsAreaValidated)
        if (!mIsAreaValidated) return false

        val propertySubTypeValue = propertySubType.value
        val isSubTypeCondo =
            propertySubTypeValue?.run { PropertyTypeUtil.isCondo(this.type) } == true
        return when {
            propertySubTypeValue != null && isSubTypeLanded(propertySubTypeValue) -> validateLandedParams()
            propertySubTypeValue != null && isSubTypeCondo -> validateCondoParams()
            else -> validateHdbCommercialParams()
        }
    }

    private fun validateHdbCommercialParams(): Boolean {
        val propertySubTypeValue = propertySubType.value
        val hasAddress = address.value != null
        val mIsAreaValidated = area.value != null

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

        return hasAddress && mIsAreaValidated && mIsPropertySubTypeValueValidated && mIsUnitFloorValidated && mIsUnitNumberValidated && mIsAreaValidated
    }

    private fun validateLandedParams(): Boolean {
        val hasAddress = address.value != null
        val mIsAreaValidated = area.value != null

        val mIsAreaTypeValidated = areaType.value != null
        val mIsTenureValidated = tenure.value != null
        val mIsBuiltYearValidated = builtYear.value != null

        isAreaTypeValidated.postValue(mIsAreaTypeValidated)
        isTenureValidated.postValue(mIsTenureValidated)
        isBuiltYearValidated.postValue(mIsBuiltYearValidated)

        // Set true for all opposites
        isUnitFloorValidated.postValue(true)
        isUnitNumberValidated.postValue(true)

        return hasAddress && mIsAreaValidated && mIsAreaTypeValidated && mIsTenureValidated && mIsBuiltYearValidated
    }

    private fun validateCondoParams(): Boolean {
        val hasAddress = address.value != null
        val mIsAreaValidated = area.value != null

        // Landed related
        isAreaTypeValidated.postValue(true)
        isTenureValidated.postValue(true)
        isBuiltYearValidated.postValue(true)

        // No check floor and unit for condo because there are exception for walk-up apartment without floor and unit number
        isUnitFloorValidated.postValue(true)
        isUnitNumberValidated.postValue(true)

        return hasAddress && mIsAreaValidated
    }

    fun reset() {
        unitFloor.postValue(null)
        unitNumber.postValue(null)

        areaType.postValue(null)
        tenure.postValue(null)
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
            onSuccess = { populateFromResponse(it) },
            onFail = { populateFromResponse(null) },
            onError = { populateFromResponse(null) }
        )
    }

    private fun populateFromResponse(response: GetAddressPropertyTypeResponse?) {
        val addressPropertyType = response?.addressPropertyType
        address.postValue(addressPropertyType)
        propertyType.postValue(addressPropertyType?.let {
            XValueEnum.PropertyType.values().find { it.value == addressPropertyType.propertyType }
        })
        initialPropertySubType.postValue(response?.let {
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
        propertySubType.postValue(response?.let {
            ListingEnum.PropertySubType.values()
                .find { it.type == addressPropertyType?.propertySubType }
        })
        reset()
    }

    fun performGetExistingXValues() {
        val sort = sort.value ?: return
        val page = page.value ?: 1
        performRequest(
            xValueRepository.getExistingXValues(
                search = "",
                page = page,
                property = XValueEnum.GetExistingXValuesProperty.DATE,
                order = sort
            )
        )
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

    fun canLoadNext(): Boolean {
        val total = mainResponse.value?.xvalues?.total ?: return false
        val currentSize = listItems.value?.size ?: 0
        val isLoading = listItems.value?.last() is Loading
        return total > currentSize && !isLoading
    }

    override fun shouldResponseBeOccupied(response: GetExistingXValuesResponse): Boolean {
        return response.xvalues.results.isNotEmpty()
    }
}