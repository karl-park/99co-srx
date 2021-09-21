package sg.searchhouse.agentconnect.viewmodel.activity.agent.cv

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.cobrokesms.CobrokeSmsListingPO
import sg.searchhouse.agentconnect.model.api.listing.FindListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingsViewModel
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class CvListingsViewModel constructor(application: Application) :
    ApiBaseViewModel<FindListingsResponse>(application) {

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    val title: MutableLiveData<String> = MutableLiveData()
    val index: MutableLiveData<Int> = MutableLiveData()
    val ownershipType: MutableLiveData<ListingEnum.OwnershipType> = MutableLiveData()
    val total: MutableLiveData<Int> = MutableLiveData()
    val listingItems: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    val selectedListItems: MutableLiveData<ArrayList<Pair<String, String>>> by lazy {
        MutableLiveData<ArrayList<Pair<String, String>>>().apply {
            value = arrayListOf()
        }
    }
    val selectedListItemsSize: LiveData<Int> = Transformations.map(selectedListItems) {
        it.size
    }

    val selectedMode: MutableLiveData<ListingsViewModel.SelectMode> = MutableLiveData()
    val isOwnCv: MutableLiveData<Boolean> = MutableLiveData()
    val totalProperties: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
        listingItems.value = arrayListOf()
    }

    fun findListings(index: Int, ownershipType: ListingEnum.OwnershipType, agentId: Int) {
        performRequest(
            listingSearchRepository.findListingsForCv(
                startIndex = index,
                ownershipType = ownershipType,
                filterUserId = agentId
            )
        )
    }

    fun loadMoreListings(index: Int, ownershipType: ListingEnum.OwnershipType, agentId: Int) {
        performNextRequest(
            listingSearchRepository.findListingsForCv(
                startIndex = index,
                ownershipType = ownershipType,
                filterUserId = agentId
            )
        )
    }

    fun toggleSelectedListItems(listingId: String, listingType: String) {
        val thisSelectedListItems = selectedListItems.value ?: arrayListOf()
        val existingItem = thisSelectedListItems.indexOfFirst {
            TextUtils.equals(it.first, listingId) && TextUtils.equals(it.second, listingType)
        }
        if (existingItem == -1) {
            thisSelectedListItems.add(Pair(listingId, listingType))
        } else {
            thisSelectedListItems.removeAt(existingItem)
        }
        selectedListItems.postValue(thisSelectedListItems)
    }

    //TODO: to refine after test version.
    private fun getSelectedListings(): List<ListingPO> {
        return listingItems.value?.filterIsInstance<ListingPO>()?.filter { listingPO ->
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
                ListingEnum.ListingType.SRX_LISTING.value -> CobrokeSmsListingPO.IsMclp.YES
                else -> CobrokeSmsListingPO.IsMclp.NO
            }.value
            val sellerUserId = listingPO.agentPO?.userId?.toString() ?: ""
            val listingId = listingPO.id
            CobrokeSmsListingPO(isMclp = isMclp, sellerUserId = sellerUserId, listingId = listingId)
        }
    }

    fun getCurrentListingPOs(): List<ListingPO> {
        return listingItems.value?.filterIsInstance<ListingPO>()?.map { it } ?: emptyList()
    }

    fun canLoadMore(): Boolean {
        val listItemSize = listingItems.value?.size ?: return false
        val total = total.value ?: return false
        return listItemSize < total
    }

    override fun shouldResponseBeOccupied(response: FindListingsResponse): Boolean {
        return listingItems.value?.isNotEmpty() ?: false
    }
}