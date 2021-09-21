package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CeaExclusiveRepository
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateUpdateListingResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetListingForListingManagementResponse
import sg.searchhouse.agentconnect.model.api.listing.user.ListingGroupSummaryPO
import sg.searchhouse.agentconnect.model.api.listing.user.ListingsGroupSummaryResponse
import sg.searchhouse.agentconnect.event.listing.user.MyListingSelectAllEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPostingCreditsResponse
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.adapter.listing.user.MyListingsPagerAdapter
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

//TODO: to refactor not to call APIs many times
class MyListingsViewModel constructor(application: Application) :
    ApiBaseViewModel<ListingsGroupSummaryResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var ceaRepository: CeaExclusiveRepository

    val selectAllAction = MutableLiveData<Pair<MyListingSelectAllEvent.Option, Boolean>>()
    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>()
    val orderCriteria = MutableLiveData<ListingManagementEnum.OrderCriteria>()
    val isSelectionApplicable = MutableLiveData<Boolean>()

    val orderCriteriaLabel: LiveData<String> = Transformations.map(orderCriteria) {
        applicationContext.getString(it.label).replace(" asc", "").replace(" desc", "")
    }

    val isOrderCriteriaNeutral: LiveData<Boolean> = Transformations.map(orderCriteria) {
        ListingManagementEnum.isOrderCriteriaNeutral(it)
    }

    val orderCriteriaIcon: LiveData<Int> = Transformations.map(orderCriteria) {
        return@map when {
            it.value.contains("Asc") -> {
                R.drawable.ic_chevron_up
            }
            it.value.contains("Desc") -> {
                R.drawable.ic_chevron_down
            }
            else -> {
                // Icon hidden
                R.drawable.ic_chevron_up
            }
        }
    }

    val summary: LiveData<List<ListingGroupSummaryPO>> =
        Transformations.map(mainResponse) { it?.summary }

    val apiCallStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val showAddOn = MutableLiveData<Boolean>()
    val selectedTab = MutableLiveData<MyListingsPagerAdapter.MyListingsTab>()
    val ceaOwnershipType = MutableLiveData<ListingEnum.OwnershipType>()
    val draftMode = MutableLiveData<ListingManagementEnum.ListingDraftMode>().apply {
        value = ListingManagementEnum.ListingDraftMode.LISTING
    }

    val searchText = MutableLiveData<String>()
    val searchEditTextHint: LiveData<String> = Transformations.map(selectedTab) { tab ->
        return@map if (tab != null) {
            applicationContext.getString(
                R.string.hint_my_listing_search_listings,
                StringUtil.toLowerCase(applicationContext.getString(tab.labelEmpty))
            )
        } else {
            ""
        }
    }
    val showSearchEditText = MutableLiveData<Boolean>().apply { value = false }
    val showSearchBarLayout = MutableLiveData<Boolean>().apply { value = true }

    //SMART FILTER Options
    val isUpdatingFilterListingsCount = MutableLiveData<Boolean>()
    val isShowSmartFilter = MutableLiveData<Boolean>()
    val propertyTypeFilterLabel = MediatorLiveData<String>()
    val listingGroups: LiveData<List<ListingManagementEnum.ListingGroup>> =
        Transformations.map(selectedTab) { it?.listingGroups }

    val tempPropertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
    val propertySubType = MutableLiveData<ListingEnum.PropertySubType>()
    val propertyAge = MutableLiveData<ListingEnum.PropertyAge>()
    val propertyBedRoom = MutableLiveData<ListingEnum.BedroomCount>()
    val propertyTenure = MutableLiveData<ListingEnum.Tenure>()

    //To reset the values
    val clearFilterOptions = MutableLiveData<Any>()

    //Filter Options Count
    val propertyMainTypeCounts = MutableLiveData<Map<ListingEnum.PropertyMainType, Int>>()
    val propertySubTypeCounts = MutableLiveData<Map<ListingEnum.PropertySubType, Int>>()
    val builtYearCounts = MutableLiveData<Map<ListingEnum.PropertyAge, Int>>()
    val condoBedroomCounts = MutableLiveData<Map<ListingEnum.BedroomCount, Int>>()
    val condoBedroomTenureCounts = MutableLiveData<Map<ListingEnum.Tenure, Int>>()
    val landedTenureCounts = MutableLiveData<Map<ListingEnum.Tenure, Int>>()

    //Copy listings
    val getListingResponse = MutableLiveData<ApiStatus<GetListingForListingManagementResponse>>()
    val copyListingResponse = MutableLiveData<ApiStatus<CreateUpdateListingResponse>>()

    init {
        viewModelComponent.inject(this)
        ownershipType.value = ListingEnum.OwnershipType.SALE
        orderCriteria.value = ListingManagementEnum.OrderCriteria.DEFAULT
        showAddOn.value = false
        //cea draft forms
        ceaOwnershipType.value = ListingEnum.OwnershipType.SALE
        performRequest()
        setupPropertyTypeFilterLabel()
    }

    fun performRequest() {
        performRequest(listingManagementRepository.getListingsGroupSummary())
    }

    fun repostListings(selectedListings: List<String>) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.repostListings(getSelectedListingIds(selectedListings)),
            onSuccess = {
                apiCallStatus.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun takeDownListings(selectedListings: List<String>) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.takeOffListings(getSelectedListingIds(selectedListings)),
            onSuccess = {
                apiCallStatus.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallStatus.postValue(ApiStatus.errorInstance())
            })
    }

    fun deleteListings(selectedListings: List<String>) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.deleteListings(getSelectedListingIds(selectedListings)),
            onSuccess = {
                apiCallStatus.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallStatus.postValue(ApiStatus.errorInstance())
            })
    }

    fun removeDraftCeaForms(draftFormIds: ArrayList<String>) {
        ApiUtil.performRequest(
            applicationContext,
            ceaRepository.removeUnsubmittedCeaForms(draftFormIds.toList()),
            onSuccess = {
                apiCallStatus.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun performGetPostingsCredits(listingIds: List<String>, onSuccess: (postingCost: Int) -> Unit, onFail: () -> Unit) {
        listingManagementRepository.getPostingsCredits(listingIds)
            .performRequest(applicationContext, onSuccess = {
                CoroutineScope(Dispatchers.Main).launch { onSuccess.invoke(it.postingCost) }
            }, onFail = {
                CoroutineScope(Dispatchers.Main).launch { onFail.invoke() }
            }, onError = {
                CoroutineScope(Dispatchers.Main).launch { onFail.invoke() }
            })
    }

    //smart filter
    fun handlePropertyPrimarySubType(primarySubType: Int) {
        tempPropertyMainType.value =
            PropertyTypeUtil.getPropertyMainTypeByPrimarySubType(primarySubType = primarySubType)
    }

    //MAIN TYPE
    private fun findPropertyMainTypeCounts() {
        val listingGroups =
            listingGroups.value ?: return ErrorUtil.handleError("Missing listing group")
        val listingGroupIds = listingGroups.map { it.value }
        val mainTypes = listOf(
            ListingEnum.PropertyMainType.HDB,
            ListingEnum.PropertyMainType.CONDO,
            ListingEnum.PropertyMainType.LANDED,
            ListingEnum.PropertyMainType.COMMERCIAL
        )
        val requests = mainTypes.map {
            listingManagementRepository.findMyListingsCount(
                listingGroups = listingGroups,
                orderCriteria = orderCriteria.value ?: ListingManagementEnum.OrderCriteria.DEFAULT,
                ownershipType = ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                propertySubTypes = it.propertySubTypes,
                address = searchText.value
            )
        }

        isShowSmartFilter.postValue(true)
        isUpdatingFilterListingsCount.postValue(true)

        ApiUtil.performRequests(
            applicationContext,
            requests,
            onSuccess = { response ->
                val counts = hashMapOf<ListingEnum.PropertyMainType, Int>()
                response.mapIndexed { index, findMyListingsResponse ->
                    val listingGroupResponse =
                        findMyListingsResponse.listingManagementGroups.filter {
                            listingGroupIds.contains(
                                it.id
                            )
                        }
                    val totalListingCount = listingGroupResponse.sumBy { it.total }
                    return@mapIndexed counts.put(mainTypes[index], totalListingCount)
                }
                val listingsWithCount = counts.filter { it.value > 0 }
                when {
                    listingsWithCount.isEmpty() -> {
                        //Note: all listing count is zero
                        isShowSmartFilter.postValue(false)
                        isUpdatingFilterListingsCount.postValue(false)
                    }
                    tempPropertyMainType.value != null -> {
                        propertyMainType.postValue(tempPropertyMainType.value)
                        propertyMainTypeCounts.postValue(counts)
                    }
                    else -> {
                        propertyMainTypeCounts.postValue(counts)
                        isUpdatingFilterListingsCount.postValue(false)
                    }
                }
            },
            onFail = {
                propertyMainTypeCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onError = {
                propertyMainTypeCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            }
        )
    }

    fun handleSecondLevelFilterOptions() {
        when (propertyMainType.value) {
            ListingEnum.PropertyMainType.HDB -> {
                findPropertySubTypesCounts(propertyMainType = ListingEnum.PropertyMainType.HDB)
            }
            ListingEnum.PropertyMainType.CONDO -> {
                findCondoBedRoomCounts()
            }
            ListingEnum.PropertyMainType.LANDED -> {
                findPropertySubTypesCounts(propertyMainType = ListingEnum.PropertyMainType.LANDED)
            }
            ListingEnum.PropertyMainType.COMMERCIAL -> {
                findPropertySubTypesCounts(propertyMainType = ListingEnum.PropertyMainType.COMMERCIAL)
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun findPropertySubTypesCounts(propertyMainType: ListingEnum.PropertyMainType) {
        val listingGroups =
            listingGroups.value ?: return ErrorUtil.handleError("Missing listing group")
        val listingGroupIds = listingGroups.map { it.value }
        val propertySubTypes = propertyMainType.propertySubTypes
        val requests = propertySubTypes.map {
            listingManagementRepository.findMyListingsCount(
                listingGroups = listingGroups,
                orderCriteria = orderCriteria.value ?: ListingManagementEnum.OrderCriteria.DEFAULT,
                ownershipType = ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                propertySubTypes = listOf(it),
                address = searchText.value
            )
        }
        isUpdatingFilterListingsCount.postValue(true)
        ApiUtil.performRequests(
            applicationContext,
            requests,
            onSuccess = { response ->
                val counts = HashMap<ListingEnum.PropertySubType, Int>()
                response.mapIndexed { index, findMyListingsResponse ->
                    val listingGroupResponse =
                        findMyListingsResponse.listingManagementGroups.filter {
                            listingGroupIds.contains(it.id)
                        }
                    val totalListingCount = listingGroupResponse.sumBy { it.total }
                    return@mapIndexed counts.put(propertySubTypes[index], totalListingCount)
                }
                propertySubTypeCounts.postValue(counts)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onFail = {
                propertySubTypeCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onError = {
                propertySubTypeCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            }
        )

    }

    private fun findCondoBedRoomCounts() {
        val listingGroups =
            listingGroups.value ?: return ErrorUtil.handleError("Missing listing group")
        val listingGroupIds = listingGroups.map { it.value }
        val condoSubTypes = ListingEnum.PropertyMainType.CONDO.propertySubTypes
        val bedroomCounts =
            ListingEnum.BedroomCount.values().filter { it != ListingEnum.BedroomCount.ANY }
        val requests = bedroomCounts.map {
            listingManagementRepository.findMyListingsCount(
                listingGroups = listingGroups,
                orderCriteria = orderCriteria.value ?: ListingManagementEnum.OrderCriteria.DEFAULT,
                ownershipType = ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                propertySubTypes = condoSubTypes,
                bedrooms = it.value,
                address = searchText.value
            )
        }
        isUpdatingFilterListingsCount.postValue(true)
        ApiUtil.performRequests(
            applicationContext,
            requests,
            onSuccess = { response ->
                val counts = HashMap<ListingEnum.BedroomCount, Int>()
                response.mapIndexed { index, findMyListingsResponse ->
                    val listingGroupResponse =
                        findMyListingsResponse.listingManagementGroups.filter {
                            listingGroupIds.contains(it.id)
                        }
                    val totalListingCount = listingGroupResponse.sumBy { it.total }
                    return@mapIndexed counts.put(bedroomCounts[index], totalListingCount)
                }
                condoBedroomCounts.postValue(counts)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onFail = {
                condoBedroomCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onError = {
                condoBedroomCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            }
        )
    }

    fun handleThirdLevelFilterOptions() {
        when (propertyMainType.value) {
            ListingEnum.PropertyMainType.HDB -> {
                val propertySubType = propertySubType.value ?: return
                findSubTypeBuiltYearCounts(propertySubType)
            }
            ListingEnum.PropertyMainType.CONDO -> {
                val bedRoomCount = propertyBedRoom.value ?: return
                findCondoBedRoomTenureCounts(bedRoomCount)
            }
            ListingEnum.PropertyMainType.LANDED -> {
                val propertySubType = propertySubType.value ?: return
                findSubTypeBuiltYearCounts(propertySubType)
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun findSubTypeBuiltYearCounts(propertySubType: ListingEnum.PropertySubType) {
        val listingGroups =
            listingGroups.value ?: return ErrorUtil.handleError("Missing listing group")
        val listingGroupIds = listingGroups.map { it.value }
        val builtYears = ListingEnum.PropertyAge.values()
        val requests = builtYears.map {
            listingManagementRepository.findMyListingsCount(
                listingGroups = listingGroups,
                orderCriteria = orderCriteria.value ?: ListingManagementEnum.OrderCriteria.DEFAULT,
                ownershipType = ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                propertySubTypes = listOf(propertySubType),
                propertyAge = it,
                address = searchText.value
            )
        }

        isUpdatingFilterListingsCount.postValue(true)
        ApiUtil.performRequests(
            applicationContext,
            requests,
            onSuccess = { response ->
                val counts = HashMap<ListingEnum.PropertyAge, Int>()
                response.mapIndexed { index, findMyListingsResponse ->
                    val listingGroupResponse =
                        findMyListingsResponse.listingManagementGroups.filter {
                            listingGroupIds.contains(it.id)
                        }
                    val totalListingCount = listingGroupResponse.sumBy { it.total }
                    return@mapIndexed counts.put(builtYears[index], totalListingCount)
                }
                builtYearCounts.postValue(counts)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onFail = {
                builtYearCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onError = {
                builtYearCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            }
        )

    }

    private fun findCondoBedRoomTenureCounts(bedroomCount: ListingEnum.BedroomCount) {
        val listingGroups =
            listingGroups.value ?: return ErrorUtil.handleError("Missing listing group")
        val listingGroupIds = listingGroups.map { it.value }
        val tenures = ListingEnum.Tenure.values().filter {
            it != ListingEnum.Tenure.NOT_SPECIFIED && it != ListingEnum.Tenure.NINE_NINE_NINE_YEARS
        }
        val requests = tenures.map {
            listingManagementRepository.findMyListingsCount(
                listingGroups = listingGroups,
                orderCriteria = orderCriteria.value ?: ListingManagementEnum.OrderCriteria.DEFAULT,
                ownershipType = ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                propertySubTypes = ListingEnum.PropertyMainType.CONDO.propertySubTypes,
                propertyAge = propertyAge.value,
                bedrooms = bedroomCount.value,
                tenure = it.value.toString(),
                address = searchText.value
            )
        }
        isUpdatingFilterListingsCount.postValue(true)
        ApiUtil.performRequests(
            applicationContext,
            requests,
            onSuccess = { response ->
                val counts = HashMap<ListingEnum.Tenure, Int>()
                response.mapIndexed { index, findMyListingsResponse ->
                    val listingGroupResponse =
                        findMyListingsResponse.listingManagementGroups.filter {
                            listingGroupIds.contains(it.id)
                        }
                    val totalListingCount = listingGroupResponse.sumBy { it.total }
                    return@mapIndexed counts.put(tenures[index], totalListingCount)
                }
                condoBedroomTenureCounts.postValue(counts)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onFail = {
                condoBedroomTenureCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onError = {
                condoBedroomTenureCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            }
        )
    }

    fun findLandedTenureCounts() {
        val listingGroups =
            listingGroups.value ?: return ErrorUtil.handleError("Missing listing group")
        val listingGroupIds = listingGroups.map { it.value }
        val tenures = ListingEnum.Tenure.values().filter {
            it != ListingEnum.Tenure.NOT_SPECIFIED && it != ListingEnum.Tenure.NINE_NINE_NINE_YEARS
        }
        val requests = tenures.map {
            listingManagementRepository.findMyListingsCount(
                listingGroups = listingGroups,
                orderCriteria = orderCriteria.value ?: ListingManagementEnum.OrderCriteria.DEFAULT,
                ownershipType = ownershipType.value ?: ListingEnum.OwnershipType.SALE,
                propertySubTypes = ListingEnum.PropertyMainType.CONDO.propertySubTypes,
                propertyAge = propertyAge.value,
                bedrooms = propertyBedRoom.value?.value,
                tenure = it.value.toString(),
                address = searchText.value
            )
        }
        isUpdatingFilterListingsCount.postValue(true)
        ApiUtil.performRequests(
            applicationContext,
            requests,
            onSuccess = { response ->
                val counts = HashMap<ListingEnum.Tenure, Int>()
                response.mapIndexed { index, findMyListingsResponse ->
                    //TODO: to refine to get listing grouped response
                    val listingGroupResponse =
                        findMyListingsResponse.listingManagementGroups.filter {
                            listingGroupIds.contains(it.id)
                        }
                    val totalListingCount = listingGroupResponse.sumBy { it.total }
                    return@mapIndexed counts.put(tenures[index], totalListingCount)
                }
                landedTenureCounts.postValue(counts)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onFail = {
                landedTenureCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            },
            onError = {
                landedTenureCounts.postValue(null)
                isUpdatingFilterListingsCount.postValue(false)
            }
        )
    }

    private fun setupPropertyTypeFilterLabel() {
        propertyTypeFilterLabel.addSource(propertyMainType) { propertyMainType ->
            populatePropertyTypeListingFilterLabel(
                propertyMainType,
                propertySubType.value,
                propertyAge.value,
                propertyBedRoom.value,
                propertyTenure.value
            )
        }
        propertyTypeFilterLabel.addSource(propertySubType) { propertySubType ->
            populatePropertyTypeListingFilterLabel(
                propertyMainType.value,
                propertySubType,
                propertyAge.value,
                propertyBedRoom.value,
                propertyTenure.value
            )
        }
        propertyTypeFilterLabel.addSource(propertyAge) { propertyAge ->
            populatePropertyTypeListingFilterLabel(
                propertyMainType.value,
                propertySubType.value,
                propertyAge,
                propertyBedRoom.value,
                propertyTenure.value
            )
        }
        propertyTypeFilterLabel.addSource(propertyBedRoom) { bedRoomCount ->
            populatePropertyTypeListingFilterLabel(
                propertyMainType.value,
                propertySubType.value,
                propertyAge.value,
                bedRoomCount,
                propertyTenure.value
            )
        }
        propertyTypeFilterLabel.addSource(propertyTenure) { tenure ->
            populatePropertyTypeListingFilterLabel(
                propertyMainType.value,
                propertySubType.value,
                propertyAge.value,
                propertyBedRoom.value,
                tenure
            )
        }

    }

    private fun populatePropertyTypeListingFilterLabel(
        propertyMainType: ListingEnum.PropertyMainType?,
        propertySubType: ListingEnum.PropertySubType?,
        propertyAge: ListingEnum.PropertyAge?,
        bedroomCount: ListingEnum.BedroomCount?,
        tenure: ListingEnum.Tenure?
    ) {
        if (propertyMainType == null) {
            return propertyTypeFilterLabel.postValue("")
        }
        val propertyMainTypeString = applicationContext.getString(propertyMainType.label)

        when {
            tenure != null && propertyAge != null && propertySubType != null -> {
                propertyTypeFilterLabel.postValue(
                    "${propertyMainTypeString}, ${
                        applicationContext.getString(
                            propertySubType.label
                        )
                    }, ${applicationContext.getString(propertyAge.label)}, ${
                        applicationContext.getString(
                            tenure.label
                        )
                    }"
                )
            }
            tenure != null && bedroomCount != null -> {
                propertyTypeFilterLabel.postValue(
                    "$propertyMainTypeString, ${
                        getBedroomCountString(
                            bedroomCount
                        )
                    }, ${applicationContext.getString(tenure.label)}"
                )
            }
            bedroomCount != null -> {
                propertyTypeFilterLabel.postValue(
                    "$propertyMainTypeString, ${getBedroomCountString(bedroomCount)}"
                )
            }
            propertySubType != null && propertyAge != null -> {
                propertyTypeFilterLabel.postValue(
                    "$propertyMainTypeString, ${
                        applicationContext.getString(
                            propertySubType.label
                        )
                    }, ${applicationContext.getString(propertyAge.label)}"
                )
            }
            propertySubType != null -> {
                propertyTypeFilterLabel.postValue(
                    "$propertyMainTypeString, ${
                        applicationContext.getString(
                            propertySubType.label
                        )
                    } "
                )
            }
            else -> {
                propertyTypeFilterLabel.postValue(propertyMainTypeString)
            }
        }
    }

    fun setupSmartFilterOptions() {
        //Note: please don't remove it for now
        if (selectedTab.value != MyListingsPagerAdapter.MyListingsTab.DRAFT ||
            draftMode.value != ListingManagementEnum.ListingDraftMode.CEA_FORMS
        ) {
            resetSmartFilterOptionsValue()
            findPropertyMainTypeCounts()
        }
    }

    private fun resetSmartFilterOptionsValue() {
        propertySubTypeCounts.value = null
        builtYearCounts.value = null
        condoBedroomCounts.value = null
        condoBedroomTenureCounts.value = null
        landedTenureCounts.value = null
        propertyMainType.value = null
        propertySubType.value = null
        propertyAge.value = null
        propertyBedRoom.value = null
        propertyTenure.value = null
    }

    private fun getBedroomCountString(bedroomCount: ListingEnum.BedroomCount): String {
        return when (bedroomCount) {
            ListingEnum.BedroomCount.SIX_AND_ABOVE -> applicationContext.getString(
                R.string.bed_room_count_more_than,
                bedroomCount.value
            )
            else -> applicationContext.getString(R.string.bed_room_count, bedroomCount.value)
        }
    }

    fun getSelectedListingIds(selectedListings: List<String>): List<String> {
        val ids: ArrayList<String> = arrayListOf()
        selectedListings.forEach {
            val id = it.split(",").first()
            if (NumberUtil.isNaturalNumber(id)) {
                ids.add(id)
            }
        }

        return ids.toList()
    }

    fun getListingManagementListing(listingId: String) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.getListingForListingManagement(listingId),
            onSuccess = {
                copyListing(it.listingEditPO)
            },
            onFail = {
                getListingResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                getListingResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun copyListing(listingEditPO: ListingEditPO) {
        ApiUtil.performRequest(
            applicationContext,
            listingManagementRepository.copyListing(listingEditPO),
            onSuccess = {
                copyListingResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                copyListingResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                copyListingResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    override fun shouldResponseBeOccupied(response: ListingsGroupSummaryResponse): Boolean {
        return response.summary.isNotEmpty()
    }
}