package sg.searchhouse.agentconnect.viewmodel.fragment.transaction

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchCriteriaV2VO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GroupTransactionsFragmentViewModel constructor(application: Application) :
    ApiBaseViewModel<TableListResponse>(
        application
    ) {
    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>()
    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()

    @Inject
    lateinit var transactionRepository: TransactionRepository

    val listItems = MutableLiveData<List<TableListResponse.Transactions.Result>>()

    val transactionSearchCriteriaV2VO = MutableLiveData<TransactionSearchCriteriaV2VO>()

    val sortType: MutableLiveData<TransactionEnum.SortType> by lazy {
        MutableLiveData<TransactionEnum.SortType>().apply {
            value = TransactionEnum.SortType.DEFAULT
        }
    }

    val isEnablePagination = MutableLiveData<Boolean>()

    val scale = MutableLiveData<Float>().apply { value = 1.0f }

    val shouldEnableHorizontalScrollView: LiveData<Boolean> = Transformations.map(scale) {
        val scale = (it ?: 1.0f)
        scale <= 1.0f
    }

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest(transactionSearchCriteriaV2VO: TransactionSearchCriteriaV2VO) {
        if (transactionSearchCriteriaV2VO.page == 1) {
            performRequest(transactionRepository.tableList(transactionSearchCriteriaV2VO))
        } else {
            performNextRequest(transactionRepository.tableList(transactionSearchCriteriaV2VO))
        }
    }

    fun canLoadNext(): Boolean {
        val total = mainResponse.value?.transactions?.total ?: 0
        val currentSize = listItems.value?.size ?: 0
        return mainStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM && currentSize < total
    }

    // TODO Maybe refactor, with `ProjectTransactionsFragment` and `ProjectOfficialTransactionsFragment(?)`
    fun maybeRescale(listWidth: Int, screenWidth: Int) {
        if (listWidth <= 0 || screenWidth <= 0) return
        val newScale = min(2.0f, max(1.0f, screenWidth.toFloat() / listWidth.toFloat()))
        if (scale.value != newScale) {
            scale.postValue(newScale)
        }
    }

    override fun shouldResponseBeOccupied(response: TableListResponse): Boolean {
        val results = response.transactions.results
        val page = transactionSearchCriteriaV2VO.value?.page ?: 1
        listItems.value = if (page > 1) {
            val originalListItems = listItems.value ?: emptyList()
            originalListItems + results
        } else {
            results
        }
        return listItems.value?.isNotEmpty() == true
    }
}