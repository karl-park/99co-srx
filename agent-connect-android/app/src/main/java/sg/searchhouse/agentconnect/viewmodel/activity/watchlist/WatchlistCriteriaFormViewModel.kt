package sg.searchhouse.agentconnect.viewmodel.activity.watchlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.WatchlistRepository
import sg.searchhouse.agentconnect.dsl.getIntListCount
import sg.searchhouse.agentconnect.dsl.getOrNull
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.watchlist.SavePropertyCriteriaResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class WatchlistCriteriaFormViewModel constructor(application: Application) :
    ApiBaseViewModel<SavePropertyCriteriaResponse>(application) {
    val propertyPurpose = MutableLiveData<ListingEnum.PropertyPurpose>().apply {
        value = ListingEnum.PropertyPurpose.RESIDENTIAL
    }

    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>().apply {
        value = ListingEnum.OwnershipType.SALE
    }

    val propertyType = MutableLiveData<ListingEnum.PropertyMainType>().apply {
        value = ListingEnum.PropertyMainType.RESIDENTIAL
    }

    val propertySubTypes = MutableLiveData<List<ListingEnum.PropertySubType>>()

    val hasListings = MutableLiveData<Boolean>()
    val hasTransactions = MutableLiveData<Boolean>()

    val searchRadius = MutableLiveData<Int>()
    val isShowRadiusSeekBar = MediatorLiveData<Boolean>()

    val bedroomCounts =
        MutableLiveData<List<WatchlistEnum.BedroomCount>>().apply {
            value = listOf(WatchlistEnum.BedroomCount.ANY)
        }

    val bathroomCounts =
        MutableLiveData<List<WatchlistEnum.BathroomCount>>().apply {
            value = listOf(WatchlistEnum.BathroomCount.ANY)
        }

    val location = MutableLiveData<String>()

    val minPrice = MutableLiveData<Int>()
    val maxPrice = MutableLiveData<Int>()

    val minSize = MutableLiveData<Int>()
    val maxSize = MutableLiveData<Int>()

    val minPsf = MutableLiveData<Int>()
    val maxPsf = MutableLiveData<Int>()

    val isShowPriceError = MediatorLiveData<Boolean>()
    val isShowSizeError = MediatorLiveData<Boolean>()
    val isShowPsfError = MediatorLiveData<Boolean>()

    val areaType = MutableLiveData<WatchlistEnum.AreaType>()
    val tenureType = MutableLiveData<TransactionEnum.TenureType>()
    val rentalType = MutableLiveData<WatchlistEnum.RentalType>()

    val projectType = MutableLiveData<WatchlistEnum.ProjectType>()

    val mrts = MutableLiveData<String>()
    val districts = MutableLiveData<String>()
    val hdbTowns = MutableLiveData<String>()
    val schools = MutableLiveData<String>()

    val mrtCount: LiveData<Int> = Transformations.map(mrts) {
        it?.getIntListCount() ?: 0
    }
    val districtCount: LiveData<Int> = Transformations.map(districts) {
        it?.getIntListCount() ?: 0
    }
    val hdbTownCount: LiveData<Int> = Transformations.map(hdbTowns) {
        it?.getIntListCount() ?: 0
    }
    val schoolCount: LiveData<Int> = Transformations.map(schools) {
        it?.getIntListCount() ?: 0
    }

    val name = MutableLiveData<String>()
    val isShowNameError = MutableLiveData<Boolean>()

    val isShowWatchlistTypeError = MutableLiveData<Boolean>()

    val isShowMore = MutableLiveData<Boolean>()

    val editCriteria = MutableLiveData<WatchlistPropertyCriteriaPO>()

    val deleteStatus = MutableLiveData<ApiStatus.StatusKey>()

    val isEnableSubmitButton: LiveData<Boolean> = Transformations.map(mainStatus) {
        it != ApiStatus.StatusKey.LOADING && it != ApiStatus.StatusKey.SUCCESS
    }

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        mainStatus.value = null
        editCriteria.value = null // Reset edit, in case reused for add purpose
        setupErrors()
        setupIsShowRadiusSeekBar()
    }

    private fun setupIsShowRadiusSeekBar() {
        isShowRadiusSeekBar.addSource(location) {
            isShowRadiusSeekBar.postValue(isRadiusApplicable())
        }
        isShowRadiusSeekBar.addSource(mrts) {
            isShowRadiusSeekBar.postValue(isRadiusApplicable())
        }
        isShowRadiusSeekBar.addSource(districts) {
            isShowRadiusSeekBar.postValue(isRadiusApplicable())
        }
        isShowRadiusSeekBar.addSource(hdbTowns) {
            isShowRadiusSeekBar.postValue(isRadiusApplicable())
        }
        isShowRadiusSeekBar.addSource(schools) {
            isShowRadiusSeekBar.postValue(isRadiusApplicable())
        }
    }

    private fun isRadiusApplicable(): Boolean {
        return location.value?.isNotEmpty() == true || mrts.value?.isNotEmpty() == true || schools.value?.isNotEmpty() == true
    }

    private fun setupErrors() {
        setupPriceError()
        setupSizeError()
        setupPsfError()
    }

    private fun setupPriceError() {
        isShowPriceError.addSource(minPrice) {
            isShowPriceError.postValue(!isPriceRangeValid())
        }
        isShowPriceError.addSource(maxPrice) {
            isShowPriceError.postValue(!isPriceRangeValid())
        }
    }

    private fun isPriceRangeValid(): Boolean {
        val minPrice = minPrice.value
        val maxPrice = maxPrice.value
        if (minPrice == null || maxPrice == null) return true
        return maxPrice > minPrice
    }

    private fun setupSizeError() {
        isShowSizeError.addSource(minSize) {
            isShowSizeError.postValue(!isSizeRangeValid())
        }
        isShowSizeError.addSource(maxSize) {
            isShowSizeError.postValue(!isSizeRangeValid())
        }
    }

    private fun isSizeRangeValid(): Boolean {
        val minSize = minSize.value
        val maxSize = maxSize.value
        if (minSize == null || maxSize == null) return true
        return maxSize > minSize
    }

    private fun setupPsfError() {
        isShowPsfError.addSource(minPsf) {
            isShowPsfError.postValue(!isPsfRangeValid())
        }
        isShowPsfError.addSource(maxPsf) {
            isShowPsfError.postValue(!isPsfRangeValid())
        }
    }

    private fun isPsfRangeValid(): Boolean {
        val minPsf = minPsf.value
        val maxPsf = maxPsf.value
        if (minPsf == null || maxPsf == null) return true
        return maxPsf > minPsf
    }

    fun performDelete() {
        val id = editCriteria.value?.id
            ?: throw IllegalStateException("`editCriteria` must be present here!")
        deleteStatus.postValue(ApiStatus.StatusKey.LOADING)
        watchlistRepository.deletePropertyCriteria(id).performRequest(applicationContext,
            onSuccess = {
                deleteStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            }, onFail = {
                deleteStatus.postValue(ApiStatus.StatusKey.FAIL)
            }, onError = {
                deleteStatus.postValue(ApiStatus.StatusKey.ERROR)
            })
    }

    private fun getWatchlistType(): WatchlistEnum.WatchlistType? {
        return when {
            hasListings.value == true && hasTransactions.value != true -> WatchlistEnum.WatchlistType.LISTINGS
            hasTransactions.value == true && hasListings.value != true -> WatchlistEnum.WatchlistType.TRANSACTIONS
            else -> null
        }
    }

    private fun getBedrooms(): String? {
        return when (propertyPurpose.value!!) {
            ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                bedroomCounts.value?.mapNotNull { it.value }?.joinToString(",")
            }
            ListingEnum.PropertyPurpose.COMMERCIAL -> {
                null
            }
        }
    }

    private fun getBathrooms(): String? {
        return bathroomCounts.value?.mapNotNull { it.value }?.joinToString(",")
    }

    private fun getSubTypes(): String? {
        val propertyPurpose = propertyPurpose.value!! // !! Guaranteed not null
        val subTypes = propertySubTypes.value?.map { it.type } ?: emptyList()
        var filteredSubTypes = filterSubTypesByPurpose(subTypes)
        if (filteredSubTypes.isEmpty()) {
            filteredSubTypes =
                PropertyTypeUtil.getPurposeMainType(propertyPurpose).propertySubTypes.map { it.type }
        }
        return filteredSubTypes.joinToString(",")
    }

    private fun filterSubTypesByPurpose(subTypes: List<Int>): List<Int> {
        val propertyPurpose = propertyPurpose.value!! // !! Guaranteed not null
        return subTypes.filter {
            when (propertyPurpose) {
                ListingEnum.PropertyPurpose.RESIDENTIAL -> {
                    PropertyTypeUtil.isResidential(it)
                }
                ListingEnum.PropertyPurpose.COMMERCIAL -> {
                    PropertyTypeUtil.isCommercial(it)
                }
            }
        }
    }

    private fun getAmenities(): String? {
        if (mrts.value?.isNotEmpty() == true) return mrts.value
        if (schools.value?.isNotEmpty() == true) return schools.value
        return null
    }

    private fun getRentalType(): WatchlistEnum.RentalType? {
        return when (ownershipType.value) {
            ListingEnum.OwnershipType.RENT -> {
                rentalType.value
            }
            else -> null
        }
    }

    private fun getSearchRadiusSubmission(): Int? {
        return if (isRadiusApplicable()) {
            searchRadius.value
        } else {
            null
        }
    }

    fun performAdd() {
        if (!isValidated()) return

        val criteria = WatchlistPropertyCriteriaPO(
            id = editCriteria.value?.id,
            name = name.value!!,
            location = location.value?.getOrNull(),
            type = getWatchlistType()?.value,
            saleType = ownershipType.value!!.value,
            radius = getSearchRadiusSubmission(),
            priceMin = minPrice.value,
            priceMax = maxPrice.value,
            typeOfArea = areaType.value?.value,
            tenure = tenureType.value?.value,
            newLaunch = projectType.value?.value,
            sizeMin = minSize.value,
            sizeMax = maxSize.value,
            psfMin = minPsf.value?.toDouble(),
            psfMax = maxPsf.value?.toDouble(),
            roomRental = getRentalType()?.value,
            bedrooms = getBedrooms(),
            bathrooms = getBathrooms(),
            cdResearchSubtypes = getSubTypes(),
            locationDistricts = districts.value?.getOrNull(),
            locationHdbTowns = hdbTowns.value?.getOrNull(),
            locationAmenities = getAmenities()
        )

        performRequest(watchlistRepository.savePropertyCriteria(criteria))
    }

    private fun isValidated(): Boolean {
        if (name.value?.isNotEmpty() != true) {
            isShowNameError.postValue(true)
            return false
        }
        if (hasListings.value != true && hasTransactions.value != true) {
            isShowWatchlistTypeError.postValue(true)
            return false
        }
        if (!hasLocation()) {
            ViewUtil.showMessage(R.string.toast_error_watchlist_missing_location)
            return false
        }
        return true
    }

    private fun hasLocation(): Boolean {
        val hasLocation = location.value?.isNotEmpty() == true
        val hasMrt = mrts.value?.isNotEmpty() == true
        val hasDistrict = districts.value?.isNotEmpty() == true
        val hasHdbTowns = hdbTowns.value?.isNotEmpty() == true
        val hasSchools = schools.value?.isNotEmpty() == true

        return hasLocation || hasMrt || hasDistrict || hasHdbTowns || hasSchools
    }

    fun hasPropertySubType(propertySubType: ListingEnum.PropertySubType): Boolean {
        return propertySubTypes.value?.any { it == propertySubType } == true
    }

    fun togglePropertySubType(propertySubType: ListingEnum.PropertySubType) {
        toggleListItems(
            propertySubType,
            propertySubTypes
        )
    }

    fun toggleCommercialPropertySubType(propertySubType: ListingEnum.PropertySubType) {
        when (propertySubType) {
            ListingEnum.PropertySubType.ALL_COMMERCIAL -> {
                propertySubTypes.postValue(listOf(ListingEnum.PropertySubType.ALL_COMMERCIAL))
            }
            else -> {
                val existingItems = propertySubTypes.value?.filter {
                    it != ListingEnum.PropertySubType.ALL_COMMERCIAL
                } ?: emptyList()

                if (existingItems.contains(propertySubType)) {
                    val subtracted = existingItems - propertySubType
                    propertySubTypes.postValue(
                        if (subtracted.isNotEmpty()) {
                            subtracted
                        } else {
                            listOf(ListingEnum.PropertySubType.ALL_COMMERCIAL)
                        }
                    )
                } else {
                    propertySubTypes.postValue(existingItems + propertySubType)
                }
            }
        }
    }

    // TODO Repeated use, refactor
    private fun <T> toggleListItems(item: T, itemsLiveData: MutableLiveData<List<T>>) {
        val existingItems = itemsLiveData.value ?: emptyList()
        if (existingItems.contains(item)) {
            itemsLiveData.postValue(existingItems - item)
        } else {
            itemsLiveData.postValue(existingItems + item)
        }
    }

    fun isAreaTypeSelected(areaType: WatchlistEnum.AreaType): Boolean {
        return this.areaType.value?.value == areaType.value
    }

    fun isTenureTypeSelected(tenureType: TransactionEnum.TenureType): Boolean {
        return this.tenureType.value?.value == tenureType.value
    }

    fun isProjectTypeSelected(projectType: WatchlistEnum.ProjectType): Boolean {
        return this.projectType.value?.value == projectType.value
    }

    fun isRentalTypeSelected(rentalType: WatchlistEnum.RentalType): Boolean {
        return this.rentalType.value?.value == rentalType.value
    }

    override fun shouldResponseBeOccupied(response: SavePropertyCriteriaResponse): Boolean {
        return true
    }
}
