package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.BlockStatus
import sg.searchhouse.agentconnect.model.api.listing.FindListingsResponse
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class FilterListingViewModel constructor(application: Application) :
    ApiBaseViewModel<FindListingsResponse>(application) {
    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var applicationContext: Context

    val propertyPurpose = MutableLiveData<ListingEnum.PropertyPurpose>()

    var defaultPropertyMainType: ListingEnum.PropertyMainType? = null
    var defaultPropertySubTypes: List<ListingEnum.PropertySubType>? = null

    var defaultMinConstructionYear: Int? = null
    var defaultMaxConstructionYear: Int? = null

    var defaultTenures: List<ListingEnum.Tenure>? = null
    var defaultBedroomCounts: List<ListingEnum.BedroomCount>? = null

    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
    val propertySubTypes = MutableLiveData<List<ListingEnum.PropertySubType>>()

    val bedroomCounts = MutableLiveData<List<ListingEnum.BedroomCount>>()
    val bathroomCounts = MutableLiveData<List<ListingEnum.BathroomCount>>()

    val floors = MutableLiveData<List<ListingEnum.Floor>>()
    val tenures = MutableLiveData<List<ListingEnum.Tenure>>()
    val furnishes = MutableLiveData<List<ListingEnum.Furnish>>()

    val rentalType =
        MutableLiveData<ListingEnum.RentalType>().apply { value = ListingEnum.RentalType.ANY }

    val hasVirtualTours = MutableLiveData<Boolean>()
    val hasDroneViews = MutableLiveData<Boolean>()
    val ownerCertification = MutableLiveData<Boolean>()
    val exclusiveListing = MutableLiveData<Boolean>()
    val xListingPrice = MutableLiveData<Boolean>()

    val minDateFirstPosted = MutableLiveData<ListingEnum.MinDateFirstPosted>()

    // InputOnly means these params are imported from ListingActivity, no further changes will be made here
    val searchTextInputOnly = MutableLiveData<String>()
    val ownershipTypeInputOnly = MutableLiveData<ListingEnum.OwnershipType>()
    val isTransactedInputOnly = MutableLiveData<Boolean>()
    val propertyAgeInputOnly = MutableLiveData<ListingEnum.PropertyAge>()
    val amenitiesIdsInputOnly = MutableLiveData<String>()
    val districtIdsInputOnly = MutableLiveData<String>()
    val hdbTownIdsInputOnly = MutableLiveData<String>()
    val projectLaunchStatusInputOnly = MutableLiveData<ListingEnum.ProjectLaunchStatus>()
    val isIncludeNearbyInputOnly = MutableLiveData<Boolean>()
    val isNearbyApplicable = MutableLiveData<Boolean>()

    private val isRequestInProgressList = MutableLiveData<List<Boolean>>()

    val isDisplayCount: LiveData<Boolean> =
        Transformations.map(isRequestInProgressList) { isRequestInProgressList ->
            // Hide text count label if isRequestInProgressList contains any "true", i.e. there is/are request(s) in progress
            isRequestInProgressList?.any { it } != true
        }

    // Request total count when this value is BLOCK
    // Rationale of this flag: The change property main type change will trigger change of sub type as well,
    // thus trigger more than one request for total
    // Hence, this ensure total request only called once per change of property main type
    val updatePropertyMainTypeTotalBlockStatus = MutableLiveData<BlockStatus>()

    // Same as above, for reset
    val resetBlockStatus = MutableLiveData<BlockStatus>()

    // Total available items from the server for this request
    val totalLabel: LiveData<String> = Transformations.map(mainResponse) { mainResponse ->
        mainResponse?.let {
            val srxStpListingsCount = mainResponse.listings.srxStpListings?.total ?: 0
            val listingPropertyListingsCount =
                mainResponse.listings.listingPropertyListings?.total ?: 0
            val mclpAllMatchSearchTermCount =
                mainResponse.listings.mclpAllMatchSearchTerm?.total ?: 0
            val mclpAllNearbyCount = mainResponse.listings.mclpAllNearby?.total ?: 0

            val total = if (isIncludeNearbyInputOnly.value == false) {
                srxStpListingsCount + listingPropertyListingsCount + mclpAllMatchSearchTermCount
            } else {
                srxStpListingsCount + listingPropertyListingsCount + mclpAllMatchSearchTermCount + mclpAllNearbyCount
            }

            val totalFormatted = NumberUtil.formatThousand(total)
            when (propertyPurpose.value) {
                ListingEnum.PropertyPurpose.RESIDENTIAL -> applicationContext.resources.getQuantityString(
                    R.plurals.label_filter_listings_count_residential,
                    total,
                    totalFormatted
                )
                ListingEnum.PropertyPurpose.COMMERCIAL -> applicationContext.resources.getQuantityString(
                    R.plurals.label_filter_listings_count_commercial,
                    total,
                    totalFormatted
                )
                else -> ""
            }
        } ?: ""
    }

    // Values from previous session of FilterListings
    // Look for variable begin with "default" if overlapped
    var previousBathroomCounts: List<ListingEnum.BathroomCount>? = null
    var previousFloors: List<ListingEnum.Floor>? = null
    var previousRentalType: ListingEnum.RentalType? = null
    var previousFurnishes: List<ListingEnum.Furnish>? = null
    var previousMinPriceRange: Int? = null
    var previousMaxPriceRange: Int? = null
    var previousMinPsf: Int? = null
    var previousMaxPsf: Int? = null
    var previousMinBuiltSize: Int? = null
    var previousMaxBuiltSize: Int? = null
    var previousMinLandSize: Int? = null
    var previousMaxLandSize: Int? = null
    var previousMinDateFirstPosted: ListingEnum.MinDateFirstPosted? = null

    var previousHasVirtualTours: Boolean? = null
    var previousHasDroneViews: Boolean? = null
    var previousOwnerCertification: Boolean? = null
    var previousExclusiveListing: Boolean? = null
    var previousXListingPrice: Boolean? = null

    init {
        viewModelComponent.inject(this)
        resetParams()
    }

    fun isHdbPropertyMainType(): Boolean = propertyMainType.value == ListingEnum.PropertyMainType.HDB

    fun hasPropertySubType(propertySubType: ListingEnum.PropertySubType): Boolean {
        return propertySubTypes.value?.any { it == propertySubType } == true
    }

    fun hasFloor(floor: ListingEnum.Floor): Boolean {
        return floors.value?.any { it == floor } == true
    }

    fun hasTenure(tenure: ListingEnum.Tenure): Boolean {
        return tenures.value?.any { it == tenure } == true
    }

    fun hasFurnish(furnish: ListingEnum.Furnish): Boolean {
        return furnishes.value?.any { it == furnish } == true
    }

    fun hasListingDate(listingDate: ListingEnum.MinDateFirstPosted): Boolean {
        return minDateFirstPosted.value == listingDate
    }

    fun togglePropertySubType(propertySubType: ListingEnum.PropertySubType) {
        when (propertyPurpose.value) {
            ListingEnum.PropertyPurpose.COMMERCIAL -> toggleCommercialPropertySubType(
                propertySubType
            )
            ListingEnum.PropertyPurpose.RESIDENTIAL -> toggleListItems(
                propertySubType,
                propertySubTypes
            )
        }
    }

    private fun toggleCommercialPropertySubType(propertySubType: ListingEnum.PropertySubType) {
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

    fun toggleFloor(floor: ListingEnum.Floor) {
        toggleListItems(floor, floors)
    }

    fun toggleTenure(tenure: ListingEnum.Tenure) {
        toggleListItems(tenure, tenures)
    }

    fun toggleFurnish(furnish: ListingEnum.Furnish) {
        toggleListItems(furnish, furnishes)
    }

    // TODO Encapsulate into `LiveDataDsl`
    private fun <T> toggleListItems(item: T, itemsLiveData: MutableLiveData<List<T>>) {
        val existingItems = itemsLiveData.value ?: emptyList()
        if (existingItems.contains(item)) {
            itemsLiveData.postValue(existingItems - item)
        } else {
            itemsLiveData.postValue(existingItems + item)
        }
    }

    fun resetParams() {
        propertyMainType.value = defaultPropertyMainType
        propertySubTypes.value = defaultPropertySubTypes

        bathroomCounts.value = listOf(ListingEnum.BathroomCount.ANY)
        bedroomCounts.value = listOf(ListingEnum.BedroomCount.ANY)

        floors.value = emptyList()
        tenures.value = emptyList()
        furnishes.value = emptyList()

        hasVirtualTours.value = false
        hasDroneViews.value = false
        ownerCertification.value = false
        exclusiveListing.value = false
        xListingPrice.value = false

        minDateFirstPosted.value = ListingEnum.MinDateFirstPosted.ANY
    }

    // Difference from resetParams: Include params from previous session
    fun initParams() {
        propertyMainType.value = defaultPropertyMainType
        propertySubTypes.value = defaultPropertySubTypes

        bathroomCounts.value = when {
            previousBathroomCounts?.isNotEmpty() != true -> listOf(ListingEnum.BathroomCount.ANY)
            else -> previousBathroomCounts
        }

        bedroomCounts.value = when {
            defaultBedroomCounts?.isNotEmpty() != true -> listOf(ListingEnum.BedroomCount.ANY)
            else -> defaultBedroomCounts
        }
        rentalType.value = previousRentalType
        floors.value = previousFloors ?: emptyList()
        tenures.value = defaultTenures ?: emptyList()
        furnishes.value = previousFurnishes ?: emptyList()

        hasVirtualTours.value = previousHasVirtualTours ?: false
        hasDroneViews.value = previousHasDroneViews ?: false
        ownerCertification.value = previousOwnerCertification ?: false
        exclusiveListing.value = previousExclusiveListing ?: false
        xListingPrice.value = previousXListingPrice ?: false

        minDateFirstPosted.value = previousMinDateFirstPosted ?: ListingEnum.MinDateFirstPosted.ANY
    }

    override fun shouldResponseBeOccupied(response: FindListingsResponse): Boolean {
        return true
    }

    // TODO: Do data binding on number ranges
    fun requestResultCount(
        minPrice: Int?,
        maxPrice: Int?,
        minPSF: Int?,
        maxPSF: Int?,
        minBuiltSize: Int?,
        maxBuiltSize: Int?,
        minLandSize: Int?,
        maxLandSize: Int?,
        minBuiltYear: Int?,
        maxBuiltYear: Int?
    ) {
        val propertyPurpose = propertyPurpose.value ?: return
        val ownershipType = ownershipTypeInputOnly.value ?: return

        val propertySubTypes = propertySubTypes.value?.map { it.type }?.joinToString(",")

        val tenures = tenures.value?.map { it.value }?.joinToString(",")
        val floors = floors.value?.joinToString(",") { it.value }
        val furnishes = furnishes.value?.joinToString(",") { it.value }

        val bedroomCountsString = bedroomCounts.value?.map { it.value }?.joinToString(",")
        val bathroomCountsString = bathroomCounts.value?.map { it.value }?.joinToString(",")

        val isIncludeNearby = isIncludeNearbyInputOnly.value ?: return

        addIsRequestInProgressList()
        performRequest(
            listingSearchRepository.findListingsCount(
                searchText = searchTextInputOnly.value,
                type = ownershipType,
                propertyPurpose = propertyPurpose,
                isTransacted = isTransactedInputOnly.value == true,
                propertyMainType = propertyMainType.value,
                propertySubTypes = propertySubTypes,
                bedroomCounts = bedroomCountsString,
                bathroomCounts = bathroomCountsString,
                propertyAge = propertyAgeInputOnly.value,
                tenures = tenures,
                floors = floors,
                furnishes = furnishes,
                amenitiesIds = amenitiesIdsInputOnly.value,
                districtIds = districtIdsInputOnly.value,
                hdbTownIds = hdbTownIdsInputOnly.value,
                hasVirtualTours = getTrueOrNull(hasVirtualTours.value),
                hasDroneViews = getTrueOrNull(hasDroneViews.value),
                ownerCertification = getTrueOrNull(ownerCertification.value),
                exclusiveListing = getTrueOrNull(exclusiveListing.value),
                xListingPrice = getTrueOrNull(xListingPrice.value),
                minDateFirstPosted = minDateFirstPosted.value,
                minPrice = minPrice,
                maxPrice = maxPrice,
                minPSF = minPSF,
                maxPSF = maxPSF,
                minBuiltSize = minBuiltSize,
                maxBuiltSize = maxBuiltSize,
                minLandSize = minLandSize,
                maxLandSize = maxLandSize,
                minBuiltYear = minBuiltYear,
                maxBuiltYear = maxBuiltYear,
                projectLaunchStatus = projectLaunchStatusInputOnly.value,
                isProjectLaunchStatusApplicable = isNearbyApplicable.value != true,
                rentalType = getRentalTypeValue(),
                isIncludeNearBy = isIncludeNearby
            )
        )
    }

    // To include into request
    fun getRentalTypeValue(): ListingEnum.RentalType? {
        return if (ownershipTypeInputOnly.value == ListingEnum.OwnershipType.SALE) {
            null
        } else {
            rentalType.value
        }
    }

    private fun addIsRequestInProgressList() {
        val thisIsRequestInProgressList =
            (isRequestInProgressList.value ?: emptyList()) + listOf(true)
        isRequestInProgressList.postValue(thisIsRequestInProgressList)
    }

    fun updateIsRequestInProgressList() {
        val thisIsRequestInProgressList = (isRequestInProgressList.value ?: emptyList())

        // Get the first in progress instance
        val indexOfFirstInProgress = thisIsRequestInProgressList.indexOfFirst { it }

        // Update its in progress flag to false
        val updated = thisIsRequestInProgressList.mapIndexed { index, isInProgress ->
            if (index == indexOfFirstInProgress) {
                false
            } else {
                isInProgress
            }
        }

        isRequestInProgressList.postValue(
            when {
                updated.any { it } -> {
                    // If any still in progress
                    updated
                }
                else -> {
                    // If none, clear
                    emptyList()
                }
            }
        )
    }

    private fun getTrueOrNull(input: Boolean?): Boolean? {
        return when (input) {
            true -> input
            else -> null
        }
    }
}