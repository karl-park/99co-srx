package sg.searchhouse.agentconnect.viewmodel.activity.listing

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
import retrofit2.Call
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.constant.AppConstant.MAX_LIMIT_EXPORT_LISTINGS
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.exception.ExceedMaxLimitException
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.cobrokesms.CobrokeSmsListingPO
import sg.searchhouse.agentconnect.model.api.listing.FindListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.listing.SearchListingResultPO
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsResponse
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ListingsViewModel(application: Application) :
    ApiBaseViewModel<FindListingsResponse>(application) {
    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var applicationContext: Context

    val query = MutableLiveData<String>()

    // Optional title if required to be different from query
    // Refer `queryLabel` for the actual title
    val optionalTitle = MutableLiveData<String>()

    val isTransacted = MutableLiveData<Boolean>()

    var propertyPurpose = MutableLiveData<PropertyPurpose>()
    var ownershipType = MutableLiveData<OwnershipType>()
    val orderCriteria: MutableLiveData<OrderCriteria> by lazy {
        MutableLiveData<OrderCriteria>().apply {
            value = OrderCriteria.DEFAULT
        }
    }
    var propertyType = MutableLiveData<PropertyMainType>()
    var propertySubType = MutableLiveData<PropertySubType>()
    var propertySubTypes = MutableLiveData<String>()
    var bedroomCount = MutableLiveData<BedroomCount>()
    private var bathroomCount = MutableLiveData<BathroomCount>()
    var propertyAge = MutableLiveData<PropertyAge>()
    var tenure = MutableLiveData<Tenure>()
    var amenitiesIds = MutableLiveData<String>()
    var amenityNames = MutableLiveData<String>()
    var districtIds = MutableLiveData<String>()
    var districtNames = MutableLiveData<String>()
    var hdbTownIds = MutableLiveData<String>()
    var hdbTownNames = MutableLiveData<String>()
    var floors = MutableLiveData<String>()
    var tenures = MutableLiveData<String>()
    var furnishes = MutableLiveData<String>()

    // From ListingFilterActivity
    var hasVirtualTours = MutableLiveData<Boolean>()
    var hasDroneViews = MutableLiveData<Boolean>()
    var ownerCertification = MutableLiveData<Boolean>()
    var exclusiveListing = MutableLiveData<Boolean>()
    var xListingPrice = MutableLiveData<Boolean>()
    var rentalType = MutableLiveData<RentalType>()
    val minPrice = MutableLiveData<Int>()
    val maxPrice = MutableLiveData<Int>()
    val minPSF = MutableLiveData<Int>()
    val maxPSF = MutableLiveData<Int>()
    val minBuiltSize = MutableLiveData<Int>()
    val maxBuiltSize = MutableLiveData<Int>()
    val minLandSize = MutableLiveData<Int>()
    val maxLandSize = MutableLiveData<Int>()
    val minBuiltYear = MutableLiveData<Int>()
    val maxBuiltYear = MutableLiveData<Int>()

    // From ListingFilterActivity, multiple room counts
    var bedroomCounts = MutableLiveData<String>()
    var bathroomCounts = MutableLiveData<String>()

    var minDateFirstPosted = MutableLiveData<MinDateFirstPosted>()

    var hasExternalFilter = MutableLiveData<Boolean>()
    var externalFilterCount = MutableLiveData<String>()

    val projectLaunchStatus = MutableLiveData<ProjectLaunchStatus>()
    val isIncludeNearBy: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = true }
    }

    val displayMode: MutableLiveData<DisplayMode> by lazy {
        MutableLiveData<DisplayMode>().apply {
            value =
                DisplayMode.LIST
        }
    }

    val listMode: MutableLiveData<ListMode> by lazy {
        MutableLiveData<ListMode>().apply {
            value =
                ListMode.DEFAULT
        }
    }

    val selectMode = MutableLiveData<SelectMode>()

    // e.g. 120 properties for sale
    val summaryLabel: MediatorLiveData<String> = MediatorLiveData()

    val propertyTypeFilterLabel: MediatorLiveData<String> = MediatorLiveData()

    // Query label shown at the top "Search" button
    val queryLabel: MediatorLiveData<String> = MediatorLiveData()

    // Display text on the link button of "Sort by"
    val sortLabel: LiveData<String> = Transformations.map(orderCriteria) { orderCriteria ->
        applicationContext.getString(orderCriteria.label)
    }

    val listItems = MediatorLiveData<List<Any>>()

    // Content: Pairs of listing ID and type
    val selectedListItems: MutableLiveData<ArrayList<Pair<String, String>>> by lazy {
        MutableLiveData<ArrayList<Pair<String, String>>>().apply {
            value = arrayListOf()
        }
    }

    val selectedListItemsSize: LiveData<Int> = Transformations.map(selectedListItems) {
        it.size
    }

    val exportListingsStatus: MutableLiveData<ApiStatus.StatusKey> = MutableLiveData()
    val exportListingsResponse: MutableLiveData<ExportListingsResponse> = MutableLiveData()

    val reportPdfLocalFileName = MutableLiveData<String>()
    val reportExcelLocalFileName = MutableLiveData<String>()

    // One seed per session of listing list
    private val seed = MutableLiveData<String>()

    // For "Select all" button
    val isAllSelected = MediatorLiveData<Boolean>()

    // Smart filter counts

    // Level 0
    val propertyMainTypeCounts = MutableLiveData<Map<PropertyMainType, Int>>()

    // Level 1
    val hdbPropertySubTypeCounts = MutableLiveData<Map<PropertySubType, Int>>()
    val condoBedRoomCountCounts = MutableLiveData<Map<BedroomCount, Int>>()
    val landedPropertySubTypeCounts =
        MutableLiveData<Map<PropertySubType, Int>>()

    // Level 2
    // List of property ages correspond to each HDB and Landed sub types
    val subTypePropertyAgeCounts =
        MutableLiveData<MutableMap<PropertySubType, Map<PropertyAge, Int>>>()
    val condoBedRoomTenureCounts =
        MutableLiveData<MutableMap<BedroomCount, Map<Tenure, Int>>>()

    // Level 3
    val landedPropertyAgeTenureCounts =
        MutableLiveData<MutableMap<PropertyAge, Map<Tenure, Int>>>()

    // Only applies to export listings
    private fun isExceedMaxLimit(): Boolean {
        return when (selectMode.value) {
            SelectMode.EXPORT_LISTINGS -> {
                val thisSelectedListItems = selectedListItems.value ?: arrayListOf()
                thisSelectedListItems.size >= MAX_LIMIT_EXPORT_LISTINGS
            }
            else -> false
        }
    }

    @Throws(ExceedMaxLimitException::class)
    fun toggleSelectedListItems(listingId: String, listingType: String) {
        val thisSelectedListItems = selectedListItems.value ?: arrayListOf()

        val existingItem = thisSelectedListItems.indexOfFirst {
            TextUtils.equals(it.first, listingId) && TextUtils.equals(it.second, listingType)
        }
        if (existingItem == -1) {
            if (isExceedMaxLimit()) throw ExceedMaxLimitException()
            thisSelectedListItems.add(Pair(listingId, listingType))
        } else {
            thisSelectedListItems.removeAt(existingItem)
        }
        selectedListItems.postValue(thisSelectedListItems)
    }

    // Total available items from the server for this request
    private val total = MutableLiveData<Int>()

    // For total summary use
    private val listingGroups: LiveData<SearchListingResultPO> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.listings
        }

    val isUpdateListingCounts = MutableLiveData<Boolean>()

    // TODO: Convert to enum when free
    val isApplyNearByToggle = MediatorLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)

        listItems.value = arrayListOf()
        isTransacted.value = false
        queryLabel.value = applicationContext.getString(R.string.label_listings_everywhere)
        orderCriteria.value = OrderCriteria.DEFAULT
        setupSummaryLabel()
        setupPropertyTypeFilterLabel()
        setupQueryLabel()
        setupIsAllSelected()
        setupIsApplyNearByToggle()
    }

    private fun setupIsApplyNearByToggle() {
        isApplyNearByToggle.addSource(query) {
            isApplyNearByToggle.postValue(
                !(TextUtils.isEmpty(it) || !TextUtils.isEmpty(districtIds.value) || !TextUtils.isEmpty(
                    hdbTownIds.value
                ))
            )
        }

        isApplyNearByToggle.addSource(districtIds) {
            isApplyNearByToggle.postValue(
                !(TextUtils.isEmpty(query.value) || !TextUtils.isEmpty(it) || !TextUtils.isEmpty(
                    hdbTownIds.value
                ))
            )
        }

        isApplyNearByToggle.addSource(hdbTownIds) {
            isApplyNearByToggle.postValue(
                !(TextUtils.isEmpty(query.value) || !TextUtils.isEmpty(
                    districtIds.value
                ) || !TextUtils.isEmpty(it))
            )
        }
    }

    private fun setupIsAllSelected() {
        isAllSelected.addSource(listItems) {
            isAllSelected.postValue(it?.size == selectedListItems.value?.size)
        }

        isAllSelected.addSource(selectedListItemsSize) {
            isAllSelected.postValue(it == getListingSize())
        }
    }

    private fun setupSummaryLabel() {
        summaryLabel.addSource(ownershipType) { ownershipType ->
            listingGroups.value?.let { populateSummaryLabel(it, ownershipType) }
        }

        summaryLabel.addSource(listingGroups) { listingGroups ->
            ownershipType.value?.let { populateSummaryLabel(listingGroups, it) }
        }
    }

    private fun setupPropertyTypeFilterLabel() {
        propertyTypeFilterLabel.addSource(propertyType) { propertyType ->
            populatePropertyTypeListingFilterLabel(
                propertyType,
                propertySubType.value,
                bedroomCount.value,
                propertyAge.value,
                tenure.value
            )
        }

        propertyTypeFilterLabel.addSource(propertySubType) { propertySubType ->
            populatePropertyTypeListingFilterLabel(
                propertyType.value,
                propertySubType,
                bedroomCount.value,
                propertyAge.value,
                tenure.value
            )
        }

        propertyTypeFilterLabel.addSource(bedroomCount) { bedroomCount ->
            populatePropertyTypeListingFilterLabel(
                propertyType.value,
                propertySubType.value,
                bedroomCount,
                propertyAge.value,
                tenure.value
            )
        }

        propertyTypeFilterLabel.addSource(propertyAge) { propertyAge ->
            populatePropertyTypeListingFilterLabel(
                propertyType.value,
                propertySubType.value,
                bedroomCount.value,
                propertyAge,
                tenure.value
            )
        }

        propertyTypeFilterLabel.addSource(tenure) { tenure ->
            populatePropertyTypeListingFilterLabel(
                propertyType.value,
                propertySubType.value,
                bedroomCount.value,
                propertyAge.value,
                tenure
            )
        }
    }

    private fun setupQueryLabel() {
        queryLabel.addSource(query) { query ->
            if (optionalTitle.value == null) {
                queryLabel.postValue(
                    when {
                        query != null && query.isNotEmpty() -> query
                        else -> applicationContext.getString(R.string.label_listings_everywhere)
                    }
                )
            }
        }

        queryLabel.addSource(optionalTitle) { optionalTitle ->
            queryLabel.postValue(
                when {
                    optionalTitle != null && optionalTitle.isNotEmpty() -> optionalTitle
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }

        queryLabel.addSource(amenityNames) { amenitiesNames ->
            queryLabel.postValue(
                when {
                    amenitiesNames != null && amenitiesNames.isNotEmpty() -> amenitiesNames
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }

        queryLabel.addSource(districtNames) { districtNames ->
            queryLabel.postValue(
                when {
                    districtNames != null && districtNames.isNotEmpty() -> districtNames
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }

        queryLabel.addSource(hdbTownNames) { hdbTownNames ->
            queryLabel.postValue(
                when {
                    hdbTownNames != null && hdbTownNames.isNotEmpty() -> hdbTownNames
                    else -> applicationContext.getString(R.string.label_listings_everywhere)
                }
            )
        }
    }

    private fun populateSummaryLabel(
        listingGroups: SearchListingResultPO,
        ownershipType: OwnershipType
    ) {
        val ownershipTypeLabel =
            applicationContext.getString(ownershipType.label).toLowerCase(Locale.getDefault())

        val label = if (isApplyNearByToggle.value == true && isIncludeNearBy.value != false) {
            val mclpAllMatchSearchTerm = listingGroups.mclpAllMatchSearchTerm?.total ?: 0
            val mclpAllNearby = listingGroups.mclpAllNearby?.total ?: 0
            applicationContext.resources.getQuantityString(
                R.plurals.label_listing_summary_match_and_nearby,
                mclpAllMatchSearchTerm,
                NumberUtil.formatThousand(mclpAllMatchSearchTerm),
                queryLabel.value,
                NumberUtil.formatThousand(mclpAllNearby)
            )
        } else {
            val listingPropertyListingsCount = listingGroups.listingPropertyListings?.total ?: 0
            val total = when {
                isApplyNearByToggle.value != true -> {
                    val srxStpListingsCount = listingGroups.srxStpListings?.total ?: 0
                    srxStpListingsCount + listingPropertyListingsCount
                }
                else -> {
                    val mclpAllMatchSearchTerm = listingGroups.mclpAllMatchSearchTerm?.total ?: 0
                    mclpAllMatchSearchTerm + listingPropertyListingsCount
                }
            }
            val totalString = NumberUtil.formatThousand(total)
            applicationContext.resources.getQuantityString(
                R.plurals.label_listing_summary,
                total,
                totalString,
                ownershipTypeLabel
            )
        }

        summaryLabel.postValue(label)
    }

    private fun isRequestParamsReady(): Boolean {
        val isBaseParamsReady =
            ownershipType.value != null && propertyPurpose.value != null && orderCriteria.value != null
        return if (hdbTownIds.value == null && districtIds.value == null && amenitiesIds.value == null) {
            isBaseParamsReady && query.value != null
        } else {
            isBaseParamsReady
        }
    }

    fun canLoadNext(): Boolean {
        val listItemsSize = getListingSize() ?: return false
        val total = total.value ?: return false
        return mainStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM && listItemsSize < total
    }

    fun maybeRefreshListing() {
        maybeLoadListing(0) {
            selectMode.postValue(null)
            listItems.value = arrayListOf()
            performRequest(it)
        }
    }

    fun maybeFindListingsCounts() {
        if (propertyPurpose.value == PropertyPurpose.RESIDENTIAL) {
            when (val propertyType = propertyType.value) {
                PropertyMainType.HDB -> {
                    if (hdbPropertySubTypeCounts.value == null) {
                        // Level 1
                        findHdbListingSubPropertyTypeCounts()
                    } else {
                        val propertySubType = propertySubType.value
                        if (propertySubType != null) {
                            // Level 2
                            if (propertyAge.value == null) {
                                findPropertyAgeCounts(propertyType, propertySubType)
                            }
                        } else {
                            // Level 1
                            findHdbListingSubPropertyTypeCounts()
                        }
                    }
                }
                PropertyMainType.CONDO -> {
                    if (condoBedRoomCountCounts.value == null) {
                        // Level 1
                        findCondoBedRoomCountCounts()
                    } else {
                        // Level 2
                        bedroomCount.value?.run {
                            if (tenure.value == null) {
                                findCondoBedRoomTenureCounts(this)
                            }
                        } ?: run {
                            // Level 1 (override e.g. for new projects)
                            findCondoBedRoomCountCounts()
                        }
                    }
                }
                PropertyMainType.LANDED -> {
                    if (landedPropertySubTypeCounts.value == null) {
                        // Level 1
                        findLandedSubPropertyTypeCounts()
                    } else {
                        propertySubType.value?.let { propertySubType ->
                            if (!isSubTypeAgeCountsPopulated(propertySubType)) {
                                // Level 2
                                findPropertyAgeCounts(propertyType, propertySubType)
                            } else {
                                propertyAge.value?.run {
                                    // Level 3
                                    if (tenure.value == null) {
                                        findLandedPropertyAgeTenureCounts(
                                            propertySubType,
                                            this
                                        )
                                    }
                                } ?: run {
                                    // Level 2
                                    findPropertyAgeCounts(propertyType, propertySubType)
                                }
                            }
                        } ?: run {
                            // Level 1
                            findLandedSubPropertyTypeCounts()
                        }
                    }
                }
                null -> findTopLevelListingCounts()
                else -> {
                }
            }
        }
    }

    private fun isSubTypeAgeCountsPopulated(propertySubType: PropertySubType): Boolean {
        val existingSubTypePropertyAgeCounts = subTypePropertyAgeCounts.value ?: mutableMapOf()
        return existingSubTypePropertyAgeCounts.contains(propertySubType)
    }

    private fun findPropertyAgeCounts(
        propertyMainType: PropertyMainType,
        propertySubType: PropertySubType
    ) {
        val propertyAges = PropertyAge.values()
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()

        val projectLaunchStatus = if (isApplyNearByToggle.value == true) {
            null
        } else {
            projectLaunchStatus.value
        }

        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, propertyAges.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = propertyMainType,
                propertySubTypes = propertySubType.type.toString(),
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                propertyAge = it,
                projectLaunchStatus = projectLaunchStatus,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = responses.mapIndexed { index, findListingsCountResponse ->
                Pair(propertyAges[index], findListingsCountResponse.total)
            }.toMap()

            val existingSubTypePropertyAgeCounts = subTypePropertyAgeCounts.value ?: mutableMapOf()
            existingSubTypePropertyAgeCounts[propertySubType] = counts
            subTypePropertyAgeCounts.postValue(existingSubTypePropertyAgeCounts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            subTypePropertyAgeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            subTypePropertyAgeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun findTopLevelListingCounts() {
        val mainTypes = PropertyMainType.values().filter {
            it != PropertyMainType.COMMERCIAL
        }
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()

        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, mainTypes.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = it,
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                projectLaunchStatus = projectLaunchStatus.value,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = HashMap<PropertyMainType, Int>()
            responses.mapIndexed { index, findListingsCountResponse ->
                counts.put(mainTypes[index], findListingsCountResponse.total)
            }
            propertyMainTypeCounts.postValue(counts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            propertyMainTypeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            propertyMainTypeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun findHdbListingSubPropertyTypeCounts() {
        val propertySubTypes = PropertyMainType.HDB.propertySubTypes
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()

        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, propertySubTypes.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = PropertyMainType.HDB,
                propertySubTypes = it.type.toString(),
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                projectLaunchStatus = projectLaunchStatus.value,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = HashMap<PropertySubType, Int>()
            responses.mapIndexed { index, findListingsCountResponse ->
                counts.put(propertySubTypes[index], findListingsCountResponse.total)
            }
            hdbPropertySubTypeCounts.postValue(counts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            hdbPropertySubTypeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            hdbPropertySubTypeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun findCondoBedRoomCountCounts() {
        val bedroomCounts =
            BedroomCount.values().filter { it != BedroomCount.ANY }
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()
        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, bedroomCounts.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = PropertyMainType.CONDO,
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                bedroomCounts = it.value.toString(),
                projectLaunchStatus = projectLaunchStatus.value,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = HashMap<BedroomCount, Int>()
            responses.mapIndexed { index, findListingsCountResponse ->
                counts.put(bedroomCounts[index], findListingsCountResponse.total)
            }
            condoBedRoomCountCounts.postValue(counts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            condoBedRoomCountCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            condoBedRoomCountCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun findCondoBedRoomTenureCounts(bedroomCount: BedroomCount) {
        val existingCondoBedRoomTenureCounts = condoBedRoomTenureCounts.value ?: mutableMapOf()
        val tenures = Tenure.values().filter {
            it != Tenure.NOT_SPECIFIED && it != Tenure.NINE_NINE_NINE_YEARS
        }
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()

        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, tenures.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = PropertyMainType.CONDO,
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                bedroomCounts = bedroomCount.value.toString(),
                tenures = it.value.toString(),
                projectLaunchStatus = projectLaunchStatus.value,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = responses.mapIndexed { index, findListingsCountResponse ->
                Pair(tenures[index], findListingsCountResponse.total)
            }.toMap()

            existingCondoBedRoomTenureCounts[bedroomCount] = counts
            condoBedRoomTenureCounts.postValue(existingCondoBedRoomTenureCounts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            condoBedRoomTenureCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            condoBedRoomTenureCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun getApplicableTenures(): List<Tenure> {
        return Tenure.values().filter {
            it != Tenure.NOT_SPECIFIED && it != Tenure.NINE_NINE_NINE_YEARS
        }
    }

    private fun findLandedPropertyAgeTenureCounts(
        propertySubType: PropertySubType,
        propertyAge: PropertyAge
    ) {
        val existingLandedPropertyAgeTenureCounts =
            landedPropertyAgeTenureCounts.value ?: mutableMapOf()
        val tenures = getApplicableTenures()
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()

        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, tenures.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = PropertyMainType.LANDED,
                propertySubTypes = propertySubType.type.toString(),
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                propertyAge = propertyAge,
                tenures = it.value.toString(),
                projectLaunchStatus = projectLaunchStatus.value,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = responses.mapIndexed { index, findListingsCountResponse ->
                Pair(tenures[index], findListingsCountResponse.total)
            }.toMap()

            existingLandedPropertyAgeTenureCounts[propertyAge] = counts
            landedPropertyAgeTenureCounts.postValue(existingLandedPropertyAgeTenureCounts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            landedPropertyAgeTenureCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            landedPropertyAgeTenureCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun findLandedSubPropertyTypeCounts() {
        val propertySubTypes = PropertyMainType.LANDED.propertySubTypes
        val ownershipType = ownershipType.value ?: return handleMissingOwnershipTypeError()

        isUpdateListingCounts.postValue(true)
        ApiUtil.performRequests(applicationContext, propertySubTypes.map {
            listingSearchRepository.findListingsCountHeader(
                searchText = query.value,
                type = ownershipType,
                propertyMainType = PropertyMainType.LANDED,
                propertySubTypes = it.type.toString(),
                amenitiesIds = amenitiesIds.value,
                districtIds = districtIds.value,
                hdbTownIds = hdbTownIds.value,
                projectLaunchStatus = projectLaunchStatus.value,
                isIncludeNearBy = isIncludeNearBy.value == true,
                isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true
            )
        }, onSuccess = { responses ->
            val counts = HashMap<PropertySubType, Int>()
            responses.mapIndexed { index, findListingsCountResponse ->
                counts.put(propertySubTypes[index], findListingsCountResponse.total)
            }
            landedPropertySubTypeCounts.postValue(counts)
            isUpdateListingCounts.postValue(false)
        }, onFail = {
            landedPropertySubTypeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        }, onError = {
            landedPropertySubTypeCounts.postValue(null)
            isUpdateListingCounts.postValue(false)
        })
    }

    private fun handleMissingOwnershipTypeError() {
        ErrorUtil.handleError("ownershipType live data should not be null at this point")
    }

    fun maybeLoadNext() {
        val currentListItemsSize = getListingSize() ?: return
        if (!canLoadNext()) return
        maybeLoadListing(currentListItemsSize) { performNextRequest(it) }
    }

    private fun getListingSize(): Int? {
        return listItems.value?.size
    }

    // NOTE: Guard against inconsistent property purpose and property type pairs
    private fun getSubmitPropertyMainType(propertyPurpose: PropertyPurpose): PropertyMainType? {
        return if (propertyPurpose == PropertyPurpose.COMMERCIAL) {
            PropertyMainType.COMMERCIAL
        } else {
            propertyType.value
        }
    }

    private fun getSubmitPropertySubTypes(): String? {
        return propertySubTypes.value ?: propertySubType.value?.type?.toString()
    }

    private fun maybeLoadListing(
        startResultIndex: Int,
        performRequest: (Call<FindListingsResponse>) -> Unit
    ) {
        if (isRequestParamsReady()) {

            // TODO precede property purpose on property type

            val queryString = query.value
            val thisOwnershipType = ownershipType.value!!
            val thisPropertyPurpose = propertyPurpose.value!!
            val thisOrderCriteria = orderCriteria.value!!
            val thisIsTransacted = isTransacted.value!!
            val propertyType = getSubmitPropertyMainType(thisPropertyPurpose)
            val bedroomCount = bedroomCount.value
            val bathroomCount = bathroomCount.value
            val bedroomCounts = bedroomCounts.value
            val bathroomCounts = bathroomCounts.value
            val propertyAge = propertyAge.value
            val amenitiesIds = amenitiesIds.value
            val districtIds = districtIds.value
            val hdbTownIds = hdbTownIds.value

            // Use value from full filter first, followed by local filter
            val tenures = tenures.value ?: tenure.value?.value?.toString()
            val propertySubTypes = getSubmitPropertySubTypes()

            val floors = floors.value
            val furnishes = furnishes.value

            // Either true or null
            val hasVirtualTours = when (hasVirtualTours.value) {
                true -> true
                else -> null
            }
            val hasDroneViews = when (hasDroneViews.value) {
                true -> true
                else -> null
            }
            val ownerCertification = when (ownerCertification.value) {
                true -> true
                else -> null
            }
            val exclusiveListing = when (exclusiveListing.value) {
                true -> true
                else -> null
            }
            val xListingPrice = when (xListingPrice.value) {
                true -> true
                else -> null
            }

            val minDateFirstPosted = minDateFirstPosted.value

            val minPrice = minPrice.value
            val maxPrice = maxPrice.value
            val minPSF = minPSF.value
            val maxPSF = maxPSF.value
            val minBuiltSize = minBuiltSize.value
            val maxBuiltSize = maxBuiltSize.value

            val minLandSize = minLandSize.value
            val maxLandSize = maxLandSize.value

            val minBuiltYear = minBuiltYear.value
            val maxBuiltYear = maxBuiltYear.value

            // Use external filter value first, otherwise local
            val bedroomCountsFinal = bedroomCounts ?: bedroomCount?.value?.toString()
            val bathroomCountsFinal = bathroomCounts ?: bathroomCount?.value?.toString()

            val seed = seed.value

            performRequest.invoke(
                listingSearchRepository.findListings(
                    searchText = queryString,
                    startResultIndex = startResultIndex,
                    type = thisOwnershipType,
                    propertyPurpose = thisPropertyPurpose,
                    orderCriteria = thisOrderCriteria,
                    isTransacted = thisIsTransacted,
                    propertyMainType = propertyType,
                    propertySubTypes = propertySubTypes,
                    bedroomCounts = bedroomCountsFinal,
                    bathroomCounts = bathroomCountsFinal,
                    propertyAge = propertyAge,
                    tenures = tenures,
                    floors = floors,
                    furnishes = furnishes,
                    amenitiesIds = amenitiesIds,
                    districtIds = districtIds,
                    hdbTownIds = hdbTownIds,
                    hasVirtualTours = hasVirtualTours,
                    hasDroneViews = hasDroneViews,
                    ownerCertification = ownerCertification,
                    exclusiveListing = exclusiveListing,
                    xListingPrice = xListingPrice,
                    minDateFirstPosted = minDateFirstPosted,
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
                    projectLaunchStatus = projectLaunchStatus.value,
                    isIncludeNearBy = isIncludeNearBy.value == true,
                    isProjectLaunchStatusApplicable = isApplyNearByToggle.value != true,
                    rentalType = getRentalType(),
                    seed = seed
                )
            )
        }
    }

    private fun getRentalType(): RentalType? {
        return if (ownershipType.value == OwnershipType.SALE) {
            null
        } else {
            rentalType.value
        }
    }

    private fun populatePropertyTypeListingFilterLabel(
        propertyType: PropertyMainType?,
        propertySubType: PropertySubType?,
        bedroomCount: BedroomCount?,
        propertyAge: PropertyAge?,
        tenure: Tenure?
    ) {
        val context = applicationContext
        if (propertyType == null) {
            return propertyTypeFilterLabel.postValue("")
        }

        val propertyTypeString = context.getString(propertyType.label)

        when {
            tenure != null && bedroomCount != null -> {
                val bedroomCountString = getBedroomCountString(bedroomCount)
                val tenureString = context.getString(tenure.label)
                propertyTypeFilterLabel.postValue("$propertyTypeString, $bedroomCountString, $tenureString")
            }
            bedroomCount != null -> {
                val bedroomCountString = getBedroomCountString(bedroomCount)
                propertyTypeFilterLabel.postValue("$propertyTypeString, $bedroomCountString")
            }
            tenure != null && propertyAge != null && propertySubType != null -> {
                val propertySubTypeString = context.getString(propertySubType.label)
                val propertyAgeString = context.getString(propertyAge.label)
                val tenureString = context.getString(tenure.label)
                propertyTypeFilterLabel.postValue("$propertyTypeString $propertySubTypeString, $propertyAgeString, $tenureString")
            }
            propertyAge != null && propertySubType != null -> {
                val propertySubTypeString = context.getString(propertySubType.label)
                val propertyAgeString = context.getString(propertyAge.label)
                propertyTypeFilterLabel.postValue("$propertyTypeString $propertySubTypeString, $propertyAgeString")
            }
            propertySubType != null -> {
                val propertySubTypeString = context.getString(propertySubType.label)
                propertyTypeFilterLabel.postValue("$propertyTypeString $propertySubTypeString")
            }
            else -> {
                propertyTypeFilterLabel.postValue(propertyTypeString)
            }
        }
    }

    private fun getBedroomCountString(bedroomCount: BedroomCount): String {
        return when (bedroomCount) {
            BedroomCount.SIX_AND_ABOVE -> applicationContext.getString(
                R.string.bed_room_count_more_than,
                bedroomCount.value
            )
            else -> applicationContext.getString(R.string.bed_room_count, bedroomCount.value)
        }
    }

    fun exportListings(
        exportOption: ExportOption,
        withAgentContact: Boolean,
        withPhoto: Boolean
    ) {
        when (exportOption) {
            ExportOption.PDF -> exportPdfListings(withAgentContact, withPhoto)
            ExportOption.EXCEL -> exportExcelListings()
        }
    }

    private fun exportExcelListings() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userName = SessionUtil.getCurrentUser()?.name?.replace(" ", "_") ?: ""
                val fileName = "${userName}_Listing_Report.csv"
                val content = generateCsvReport()
                try {
                    FileUtil.writeTextToFile(
                        applicationContext,
                        content,
                        fileName,
                        AppConstant.DIR_LISTING_REPORT
                    )
                } catch (e: IOException) {
                    return@withContext ErrorUtil.handleError(applicationContext.getString(R.string.toast_generate_report_failed))
                }
                reportExcelLocalFileName.postValue(fileName)
            }
        }
    }

    private fun generateCsvReport(): String {
        var content = ""

        content += "No,Address,Blk,Street,Type,Asking,PSF,Built,RMS,Agency,Agent,Contact,Posted Date\n"

        val selectedListings =
            selectedListItems.value?.take(MAX_LIMIT_EXPORT_LISTINGS) ?: arrayListOf()

        val listings = listItems.value?.filterIsInstance<ListingPO>() ?: listOf()

        selectedListings.mapIndexed { index, pair ->
            val displayIndex = (index + 1).toString()

            // id and type
            val listingPO =
                listings.find { thisListing ->
                    thisListing.isListingIdType(
                        pair.first,
                        pair.second
                    )
                }
                    ?: return@mapIndexed

            val address = listingPO.name
            val subType = listingPO.cdResearchSubType
            val block = when {
                !PropertyTypeUtil.isLanded(subType) -> listingPO.block
                else -> ""
            }
            val street = listingPO.address
            val propertyType = listingPO.propertyType

            val askingPrice =
                "\$${
                    listingPO.getAskingPriceInt()?.run {
                        NumberUtil.getThousandMillionShortForm(this)
                    } ?: run { listingPO.askingPrice }
                }"

            val psf = listingPO.getSpreadsheetPsf()
            val builtSize = listingPO.getSpreadsheetBuiltSize()

            val bedrooms = "${listingPO.getRoomsNumber()} bedrooms"

            val agentPO = listingPO.agentPO

            val agencyName = agentPO?.agencyName ?: ""
            val agentName = agentPO?.name ?: ""
            val contact = agentPO?.mobile ?: ""

            val postedDate = listingPO.actualDatePosted

            val list = listOf(
                displayIndex,
                address,
                block,
                street,
                propertyType,
                askingPrice,
                psf,
                builtSize,
                bedrooms,
                agencyName,
                agentName,
                contact,
                postedDate
            )
            val row = list.joinToString(",") { StringUtil.removeAll(it, ",") }
            content += "$row\n"
        }

        return content
    }

    private fun exportPdfListings(withAgentContact: Boolean, withPhoto: Boolean) {
        exportListingsStatus.postValue(ApiStatus.StatusKey.LOADING)

        val selectedListings =
            selectedListItems.value?.take(MAX_LIMIT_EXPORT_LISTINGS) ?: arrayListOf()

        val srxListings = selectedListings.filter {
            TextUtils.equals(it.second, ListingType.SRX_LISTING.value)
        }.mapNotNull {
            NumberUtil.toInt(it.first)
        }

        val publicListings = selectedListings.filter {
            TextUtils.equals(it.second, ListingType.PUBLIC_LISTING.value)
        }.mapNotNull {
            NumberUtil.toInt(it.first)
        }

        ApiUtil.performRequest(
            applicationContext,
            listingSearchRepository.exportListings(
                withAgentContact = withAgentContact,
                withPhoto = withPhoto,
                srxStpListings = srxListings,
                otherListings = publicListings
            ),
            onSuccess = {
                exportListingsResponse.postValue(it)
                exportListingsStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = {
                exportListingsStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                exportListingsStatus.postValue(ApiStatus.StatusKey.ERROR)
            })
    }

    fun downloadReport(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val userName = SessionUtil.getCurrentUser()?.name?.replace(" ", "_") ?: ""
                val fileName = "${userName}_Listing_Report.pdf"
                try {
                    IntentUtil.downloadFile(
                        applicationContext, okHttpClient, url, fileName,
                        AppConstant.DIR_LISTING_REPORT
                    )
                } catch (e: IOException) {
                    return@withContext ErrorUtil.handleError(applicationContext.getString(R.string.toast_download_report_failed_generic))
                }
                reportPdfLocalFileName.postValue(fileName)
            }
        }
    }

    override fun shouldResponseBeOccupied(response: FindListingsResponse): Boolean {
        // TODO: Populate list in another method
        // get live data from mainResponse not working?

        val newListing: ArrayList<Any> = arrayListOf()

        response.listings.let { listings ->
            listings.srxStpListings?.listingPOs?.let { newListing.addAll(it) }
            listings.listingPropertyListings?.listingPOs?.let { newListing.addAll(it) }

            listings.mclpAllMatchSearchTerm?.listingPOs?.let { newListing.addAll(it) }

            if (isIncludeNearBy.value != false) {
                if (listings.mclpAllNearby?.listingPOs?.isNotEmpty() == true) {
                    if (listItems.value?.any { it is NearByHeader } != true) {
                        newListing.add(NearByHeader())
                    }
                    newListing.addAll(listings.mclpAllNearby.listingPOs)
                }
            }

            total.value = getTotal(listings)
        }

        listItems.value = listItems.value?.plus(newListing)
        seed.value = response.seed

        return (getListingSize() ?: 0) > 0
    }

    private fun getTotal(listings: SearchListingResultPO): Int {
        val srxStpListings = listings.srxStpListings?.total ?: 0
        val listingPropertyListings = listings.listingPropertyListings?.total ?: 0
        val mclpAllMatchSearchTerm = listings.mclpAllMatchSearchTerm?.total ?: 0
        val mclpAllNearby = listings.mclpAllNearby?.total ?: 0

        // TODO: Maybe remove this check
        return if (isIncludeNearBy.value == false) {
            srxStpListings + listingPropertyListings + mclpAllMatchSearchTerm
        } else {
            srxStpListings + listingPropertyListings + mclpAllMatchSearchTerm + mclpAllNearby
        }
    }

    fun getCurrentListingPOs(): List<ListingPO> {
        return listItems.value?.filterIsInstance<ListingPO>()?.map { it } ?: emptyList()
    }

    fun getAgentPO(listingId: String, listingType: String): AgentPO? {
        return getCurrentListingPOs().find { it.listingType == listingType && it.id == listingId }?.agentPO
    }

    @Throws(ExceedMaxLimitException::class)
    fun selectAll() {
        val thisSelectedItems = ArrayList(getCurrentListingPOs().map {
            Pair(it.id, it.listingType)
        })
        if (thisSelectedItems.size > MAX_LIMIT_EXPORT_LISTINGS) {
            throw ExceedMaxLimitException()
        } else {
            selectedListItems.postValue(thisSelectedItems)
        }
    }

    fun unselectAll() {
        selectedListItems.postValue(arrayListOf())
    }

    private fun getSelectedListings(): List<ListingPO> {
        return listItems.value?.filterIsInstance<ListingPO>()?.filter { listingPO ->
            selectedListItems.value?.any { selected ->
                listingPO.isListingIdType(
                    selected.first,
                    selected.second
                )
            } == true
        } ?: emptyList()
    }

    fun getSelectedCobrokeListings(): List<CobrokeSmsListingPO> {
        return getSelectedListings().map { listingPO ->
            val isMclp = when (listingPO.listingType) {
                ListingType.SRX_LISTING.value -> CobrokeSmsListingPO.IsMclp.YES
                else -> CobrokeSmsListingPO.IsMclp.NO
            }.value
            val sellerUserId = listingPO.agentPO?.userId?.toString() ?: ""
            val listingId = listingPO.id
            CobrokeSmsListingPO(isMclp = isMclp, sellerUserId = sellerUserId, listingId = listingId)
        }
    }

    enum class DisplayMode {
        LIST, GRID, MAP
    }

    enum class ListMode {
        DEFAULT, SELECTION
    }

    enum class SelectMode {
        EXPORT_LISTINGS, COBROKE
    }

    class NearByHeader
}