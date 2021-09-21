package sg.searchhouse.agentconnect.viewmodel.fragment.listing.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.CeaExclusiveRepository
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.listing.UnsubmittedCeaFormPO
import sg.searchhouse.agentconnect.model.api.listing.user.FindMyListingsResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class MyListingsFragmentViewModel constructor(application: Application) :
    ApiBaseViewModel<FindMyListingsResponse>(application) {

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var ceaRepository: CeaExclusiveRepository

    //as default value and params for API calls
    var defaultPropertyMainType: ListingEnum.PropertyMainType? = null
    var startIndex: Int = 0
    var ownershipType: ListingEnum.OwnershipType = ListingEnum.OwnershipType.SALE
    var orderCriteria: ListingManagementEnum.OrderCriteria =
        ListingManagementEnum.OrderCriteria.DEFAULT
    var propertySubTypes: List<ListingEnum.PropertySubType>? = null
    var propertyAge: ListingEnum.PropertyAge? = null
    var bedroomCount: ListingEnum.BedroomCount? = null
    var tenure: ListingEnum.Tenure? = null
    var searchText: String? = null

    //Listings
    val listingPOs = MutableLiveData<List<Any>>()
    val total = MutableLiveData<Int>()

    //CEA Drafts Mode
    var selectedDraftMode = MutableLiveData<ListingManagementEnum.ListingDraftMode>()
    val showDraftCeaForms = MutableLiveData<Boolean>()
    val draftCeaForms = MutableLiveData<List<UnsubmittedCeaFormPO>>()
    var ceaOwnershipType: ListingEnum.OwnershipType = ListingEnum.OwnershipType.SALE
    var isDraftCeaFormsOccupied = MutableLiveData<Boolean>()
    var isDraftCeaFormsEmpty = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
    }

    fun loadMyListings(listingGroups: List<ListingManagementEnum.ListingGroup>) {
        startIndex = 0
        mainStatus.postValue(ApiStatus.StatusKey.LOADING)
        performRequest(listingGroups = listingGroups)
    }

    fun loadMoreMyListings(listingGroups: List<ListingManagementEnum.ListingGroup>) {
        startIndex = getListingSize() ?: 0
        mainStatus.postValue(ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM)
        performRequest(listingGroups = listingGroups)
    }

    // The extra checks on the call and result are to ensure sale and rent tab list items don't mix up
    fun performRequest(listingGroups: List<ListingManagementEnum.ListingGroup>) {
        when (ownershipType) {
            ListingEnum.OwnershipType.SALE -> {
                ApiUtil.performRequest(
                    applicationContext,
                    listingManagementRepository.findMyListings(
                        startResultIndex = startIndex,
                        listingGroups = listingGroups,
                        orderCriteria = orderCriteria,
                        ownershipType = ownershipType,
                        propertySubTypes = propertySubTypes,
                        propertyAge = propertyAge,
                        bedrooms = bedroomCount?.value,
                        tenure = tenure?.value?.toString(),
                        address = searchText
                    ), onSuccess = {
                        if (ownershipType == ListingEnum.OwnershipType.SALE) {
                            mainResponse.postValue(it)
                            mainStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                        }
                    }, onFail = {
                        if (ownershipType == ListingEnum.OwnershipType.SALE) {
                            mainStatus.postValue(ApiStatus.StatusKey.FAIL)
                        }
                    }, onError = {
                        if (ownershipType == ListingEnum.OwnershipType.SALE) {
                            mainStatus.postValue(ApiStatus.StatusKey.ERROR)
                        }
                    })
            }
            ListingEnum.OwnershipType.RENT -> {
                ApiUtil.performRequest(
                    applicationContext,
                    listingManagementRepository.findMyListings(
                        startResultIndex = startIndex,
                        listingGroups = listingGroups,
                        orderCriteria = orderCriteria,
                        ownershipType = ownershipType,
                        propertySubTypes = propertySubTypes,
                        propertyAge = propertyAge,
                        bedrooms = bedroomCount?.value,
                        tenure = tenure?.value?.toString(),
                        address = searchText
                    ), onSuccess = {
                        if (ownershipType == ListingEnum.OwnershipType.RENT) {
                            mainResponse.postValue(it)
                            mainStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                        }
                    }, onFail = {
                        if (ownershipType == ListingEnum.OwnershipType.RENT) {
                            mainStatus.postValue(ApiStatus.StatusKey.FAIL)
                        }
                    }, onError = {
                        if (ownershipType == ListingEnum.OwnershipType.RENT) {
                            mainStatus.postValue(ApiStatus.StatusKey.ERROR)
                        }
                    })
            }
            else -> {
            }
        }
    }

    private fun getListingSize(): Int? {
        return listingPOs.value?.size
    }

    fun getListingPOs(response: FindMyListingsResponse): List<ListingPO> {
        return response.listingManagementGroups.map { groupPO -> groupPO.listings }.flatten()
    }

    fun getListingGroup(group: Int): ListingManagementEnum.ListingGroup? {
        return ListingManagementEnum.ListingGroup.values().find { it.value == group }
    }

    fun getListingPOsTotal(response: FindMyListingsResponse): Int {
        return response.listingManagementGroups.sumBy { it.total }
    }

    fun findUnsubmittedCeaForms() {
        mainStatus.postValue(ApiStatus.StatusKey.LOADING)
        isDraftCeaFormsOccupied.postValue(false)
        isDraftCeaFormsEmpty.postValue(false)
        ApiUtil.performRequest(
            applicationContext,
            ceaRepository.findUnsubmittedCeaForms(ceaOwnershipType.value),
            onSuccess = {
                draftCeaForms.postValue(it.forms)
                mainStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                isDraftCeaFormsOccupied.postValue(it.forms.isNotEmpty())
                isDraftCeaFormsEmpty.postValue(it.forms.isEmpty())
            },
            onFail = {
                mainStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                mainStatus.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    fun canLoadMore(): Boolean {
        val total = total.value ?: return false
        val size = getListingSize() ?: return false
        return size < total
    }

    override fun shouldResponseBeOccupied(response: FindMyListingsResponse): Boolean {
        return if (startIndex == 0) {
            getListingPOs(response).isNotEmpty()
        } else {
            (getListingSize() ?: 0) > 0
        }
    }
}
