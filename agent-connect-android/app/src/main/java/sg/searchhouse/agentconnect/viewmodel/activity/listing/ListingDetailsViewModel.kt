package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.annotation.ColorRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.listing.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateUpdateListingResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetListingForListingManagementResponse
import sg.searchhouse.agentconnect.model.api.xvalue.HomeReportTransaction
import sg.searchhouse.agentconnect.model.api.xvalue.XTrendKeyValue
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.view.widget.listing.MyListingsFabAddOn
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class ListingDetailsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetListingResponse>(application) {
    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var applicationContext: Context

    var listingParam = MutableLiveData<Pair<String, String>>()

    var fullListingPO: LiveData<FullListingPO> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing
        }

    var listingPO: LiveData<ListingPO> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing?.listingDetailPO?.listingPO
        }

    var listingDetailPO: LiveData<ListingDetailPO> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing?.listingDetailPO
        }

    val isExpandKeyInfo: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    var hasSimilarListings: LiveData<Boolean> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing?.listingDetailPO?.listingPO?.similarListings?.isNotEmpty()
        }

    var hasFacilities: LiveData<Boolean> =
        Transformations.map(mainResponse) { mainResponse ->
            val listingDetailPO = mainResponse?.fullListing?.listingDetailPO ?: return@map false
            val facilities =
                listingDetailPO.area + listingDetailPO.features + listingDetailPO.fixtures
            facilities.isNotEmpty()
        }

    var shouldShowHomeValueEstimator: LiveData<Boolean> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing?.listingDetailPO?.listingPO?.xValueDisplayInd == ListingEnum.XValueDisplayInd.SHOW.value
        }

    val isExpandDescription: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }
    val isExpandLocation: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }
    val isExpandFacilities: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val isExpandHomeValueEstimator: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val isExpandTransactions: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val isExpandSimilarListings: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val isExpandContactAgent: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val isAgentCurrentUser: LiveData<Boolean> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing?.agentPO?.userId == SessionUtil.getCurrentUser()?.id
        }

    val toolbarStyle: MutableLiveData<ToolbarStyle> by lazy {
        MutableLiveData<ToolbarStyle>().apply {
            value = ToolbarStyle.TRANSLUCENT
        }
    }

    val graphStatusKey = MutableLiveData<ApiStatus.StatusKey>()
    val latestTransactionsStatusKey = MutableLiveData<ApiStatus.StatusKey>()

    val latestTransactions =
        MutableLiveData<List<HomeReportTransaction>>()

    val xTrendList = MutableLiveData<List<XTrendKeyValue>>()

    val lowestXValue: LiveData<XTrendKeyValue> =
        Transformations.map(xTrendList) { xTrendList ->
            xTrendList.minBy { it.value }
        }

    val highestXValue: LiveData<XTrendKeyValue> =
        Transformations.map(xTrendList) { xTrendList ->
            xTrendList.maxBy { it.value }
        }

    val latestXValue: LiveData<XTrendKeyValue> =
        Transformations.map(xTrendList) { xTrendList ->
            xTrendList.maxBy { it.getMillis() }
        }

    val isShowLatestTransactionsOccupied = MediatorLiveData<Boolean>()
    val isShowLatestTransactionsEmpty = MediatorLiveData<Boolean>()

    val isGraphAvailable: LiveData<Boolean> = Transformations.map(xTrendList) {
        it?.isNotEmpty() == true
    }

    val selectedTimeScale: MutableLiveData<XValueEnum.TimeScale> by lazy {
        MutableLiveData<XValueEnum.TimeScale>().apply {
            value = XValueEnum.TimeScale.TEN_YEARS
        }
    }

    val listingGroup = MutableLiveData<ListingManagementEnum.ListingGroup>()
    val isShowEditLayoutByGroup: LiveData<Boolean> = Transformations.map(listingGroup) {
        return@map when (it) {
            ListingManagementEnum.ListingGroup.ACTIVE,
            ListingManagementEnum.ListingGroup.EXPIRED,
            ListingManagementEnum.ListingGroup.TAKEN_OFF -> true
            else -> false
        }
    }
    val isTransactedListing: LiveData<Boolean> = Transformations.map(listingGroup) {
        return@map when (it) {
            ListingManagementEnum.ListingGroup.TRANSACTION -> true
            else -> false
        }
    }

    val getManagementListingResponse =
        MutableLiveData<ApiStatus<GetListingForListingManagementResponse>>()

    val copyListingResponse =
        MutableLiveData<ApiStatus<CreateUpdateListingResponse>>()

    init {
        viewModelComponent.inject(this)
        setupIsShowLatestTransactionsOccupied()
        setupIsShowLatestTransactionsEmpty()
    }

    private fun setupIsShowLatestTransactionsEmpty() {
        isShowLatestTransactionsEmpty.value = false
        isShowLatestTransactionsEmpty.addSource(latestTransactions) {
            isShowLatestTransactionsEmpty.postValue(latestTransactions.value?.isNotEmpty() != true)
        }
        isShowLatestTransactionsEmpty.addSource(latestTransactionsStatusKey) { status ->
            if (status !in listOf(
                    ApiStatus.StatusKey.SUCCESS,
                    ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
                )
            ) {
                isShowLatestTransactionsEmpty.postValue(false)
            }
        }
    }

    private fun setupIsShowLatestTransactionsOccupied() {
        isShowLatestTransactionsOccupied.value = false
        isShowLatestTransactionsOccupied.addSource(latestTransactions) {
            isShowLatestTransactionsOccupied.postValue(latestTransactions.value?.isNotEmpty() == true)
        }
        isShowLatestTransactionsOccupied.addSource(latestTransactionsStatusKey) { status ->
            if (status !in listOf(
                    ApiStatus.StatusKey.SUCCESS,
                    ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
                )
            ) {
                isShowLatestTransactionsOccupied.postValue(false)
            }
        }
    }

    fun performRequest(listingId: String, listingType: String) {
        performRequest(listingSearchRepository.getListing(listingId, listingType))
    }

    fun performGetXTrend(listingId: String, listingType: String) {
        graphStatusKey.postValue(ApiStatus.StatusKey.LOADING)
        listingSearchRepository.getListingXTrend(listingId, listingType)
            .performRequest(applicationContext,
                onSuccess = { response ->
                    xTrendList.postValue(getFormattedXTrendKeyValues(response.getXValueTrendList()))
                    graphStatusKey.postValue(ApiStatus.StatusKey.SUCCESS)
                }, onFail = {
                    graphStatusKey.postValue(ApiStatus.StatusKey.FAIL)
                }, onError = {
                    graphStatusKey.postValue(ApiStatus.StatusKey.ERROR)
                })
    }

    private fun getFormattedXTrendKeyValues(xValueTrend: List<XTrendKeyValue>): List<XTrendKeyValue> {
        val sortedList = xValueTrend.sortedBy { it.getMillis() }
        val filteredList = sortedList.filterIndexed { index, xTrendKeyValue ->
            if (index == sortedList.size - 2) {
                val theLastDataPoint = sortedList[sortedList.size - 1]
                !XTrendKeyValue.isSameYearQuarter(xTrendKeyValue, theLastDataPoint)
            } else {
                true
            }
        }
        val listSize = filteredList.size
        return filteredList.mapIndexed { index, xTrendKeyValue ->
            xTrendKeyValue.isLatest = index == listSize - 1
            xTrendKeyValue
        }
    }

    fun performGetListingLatestTransactions(listingPO: ListingPO) {
        latestTransactionsStatusKey.postValue(ApiStatus.StatusKey.LOADING)
        listingSearchRepository.getListingLatestTransactions(
            listingPO,
            ListingEnum.OwnershipType.SALE
        ).performRequest(applicationContext,
            onSuccess = {
                latestTransactions.postValue(it.transactions?.recentTransactionPO?.transactionList)
                latestTransactionsStatusKey.postValue(ApiStatus.StatusKey.SUCCESS)
            }, onFail = {
                latestTransactionsStatusKey.postValue(ApiStatus.StatusKey.FAIL)
            }, onError = {
                latestTransactionsStatusKey.postValue(ApiStatus.StatusKey.ERROR)
            })
    }

    fun selectTimeScale(timeScale: XValueEnum.TimeScale) {
        selectedTimeScale.postValue(timeScale)
    }

    fun getAddOnList(): List<MyListingsFabAddOn.AddOnFab> {
        var initialList = listOf<MyListingsFabAddOn.AddOnFab>()
        if (listingPO.value?.isCommercialListing() != true) {
            initialList = listOf(MyListingsFabAddOn.AddOnFab.CERTIFIED_LISTING)
        }
        return initialList + listOf(
            MyListingsFabAddOn.AddOnFab.FEATURED_LISTING,
            MyListingsFabAddOn.AddOnFab.COPY,
            MyListingsFabAddOn.AddOnFab.V360,
            MyListingsFabAddOn.AddOnFab.X_DRONE,
            MyListingsFabAddOn.AddOnFab.COMMUNITY_POST
        )
    }

    override fun shouldResponseBeOccupied(response: GetListingResponse): Boolean {
        return true
    }

    fun performGetManagementListing() {
        val listingId = listingPO.value?.id ?: return
        listingManagementRepository.getListingForListingManagement(listingId)
            .performRequest(applicationContext,
                onSuccess = {
                    performCopyListing(it.listingEditPO)
                },
                onFail = {
                    getManagementListingResponse.postValue(ApiStatus.failInstance(it))
                },
                onError = {
                    getManagementListingResponse.postValue(ApiStatus.errorInstance())
                }
            )
    }

    private fun performCopyListing(listingEditPO: ListingEditPO) {
        listingManagementRepository.copyListing(listingEditPO).performRequest(applicationContext,
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

    enum class ToolbarStyle(@ColorRes val menuIconColor: Int, @ColorRes val backgroundColor: Int) {
        DEFAULT(R.color.black_invertible, R.color.white_smoke_invertible), TRANSLUCENT(
            R.color.white,
            R.color.listing_action_bar_background
        )
    }
}