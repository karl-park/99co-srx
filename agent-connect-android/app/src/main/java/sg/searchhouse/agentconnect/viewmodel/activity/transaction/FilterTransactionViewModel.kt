package sg.searchhouse.agentconnect.viewmodel.activity.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class FilterTransactionViewModel constructor(application: Application) :
    CoreViewModel(application) {
    var entryType: SearchEntryType? = null
    var ownershipType: ListingEnum.OwnershipType? = null
    var propertyPurpose: ListingEnum.PropertyPurpose? = null

    var query: String? = null
    var projectId: Int? = null
    var hdbTownIds: String? = null
    var districtIds: String? = null
    var amenityIds: String? = null

    var defaultPropertyMainType: ListingEnum.PropertyMainType? = null
    var defaultPropertySubTypes: List<ListingEnum.PropertySubType>? = null

    var previousPropertyMainType: ListingEnum.PropertyMainType? = null
    var previousPropertySubTypes: List<ListingEnum.PropertySubType>? = null

    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
    val propertySubTypes = MutableLiveData<List<ListingEnum.PropertySubType>>()

    val radius = MutableLiveData<TransactionEnum.Radius>()
    val areaType = MutableLiveData<TransactionEnum.AreaType>()
    val tenureType = MutableLiveData<TransactionEnum.TenureType>()

    var previousMinAge: Int? = null
    var previousMaxAge: Int? = null

    var previousMinSize: Int? = null
    var previousMaxSize: Int? = null

    var previousRadius: TransactionEnum.Radius? = null
    var previousAreaType: TransactionEnum.AreaType? = null
    var previousTenureType: TransactionEnum.TenureType? = null

    init {
        resetParams()
    }

    fun isRadiusSelected(radius: TransactionEnum.Radius): Boolean {
        return this.radius.value?.value == radius.value
    }

    fun isAreaTypeSelected(areaType: TransactionEnum.AreaType): Boolean {
        return this.areaType.value?.value == areaType.value
    }

    fun isTenureTypeSelected(tenureType: TransactionEnum.TenureType): Boolean {
        return this.tenureType.value?.value == tenureType.value
    }

    fun toggleRadius(radius: TransactionEnum.Radius) {
        this.radius.postValue(radius)
    }

    fun toggleAreaType(areaType: TransactionEnum.AreaType) {
        this.areaType.postValue(areaType)
    }

    fun toggleTenureType(tenureType: TransactionEnum.TenureType) {
        this.tenureType.postValue(tenureType)
    }

    fun togglePropertySubType(propertySubType: ListingEnum.PropertySubType) {
        when (propertyPurpose) {
            ListingEnum.PropertyPurpose.COMMERCIAL -> toggleCommercialPropertySubType(
                propertySubType
            )
            ListingEnum.PropertyPurpose.RESIDENTIAL -> toggleListItems(
                propertySubType,
                propertySubTypes
            )
        }
    }

    private fun <T> toggleListItems(item: T, itemsLiveData: MutableLiveData<List<T>>) {
        val existingItems = itemsLiveData.value ?: emptyList()
        if (existingItems.contains(item)) {
            itemsLiveData.postValue(existingItems - item)
        } else {
            itemsLiveData.postValue(existingItems + item)
        }
    }

    private fun toggleCommercialPropertySubType(propertySubType: ListingEnum.PropertySubType) {
        propertySubTypes.postValue(listOf(propertySubType))
    }

    fun hasPropertySubType(propertySubType: ListingEnum.PropertySubType): Boolean {
        return propertySubTypes.value?.any { it == propertySubType } == true
    }

    fun resetParams() {
        propertyMainType.value = defaultPropertyMainType
        propertySubTypes.value = defaultPropertySubTypes

        radius.value = null
        areaType.value = null
        tenureType.value = null
    }

    fun initParams() {
        propertyMainType.value = previousPropertyMainType
        propertySubTypes.value = previousPropertySubTypes

        radius.value = previousRadius
        areaType.value = previousAreaType
        tenureType.value = previousTenureType
    }
}