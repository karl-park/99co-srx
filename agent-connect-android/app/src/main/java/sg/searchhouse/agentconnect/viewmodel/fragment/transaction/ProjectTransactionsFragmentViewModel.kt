package sg.searchhouse.agentconnect.viewmodel.fragment.transaction

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.model.api.transaction.ProjectTransactionRequest
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.event.transaction.UpdateProjectTransactionsEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class ProjectTransactionsFragmentViewModel constructor(application: Application) :
    ApiBaseViewModel<TableListResponse>(
        application
    ) {
    var projectId: Int = 0
    val propertySubType = MutableLiveData<ListingEnum.PropertySubType>()
    val projectTransactionType =
        MutableLiveData<UpdateProjectTransactionsEvent.ProjectTransactionType>()
    val projectTransactionRequest = MutableLiveData<ProjectTransactionRequest>()
    val page: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply { value = 1 }
    }

    val isHdb: LiveData<Boolean> = Transformations.map(propertySubType) {
        it?.run { PropertyTypeUtil.isHDB(type) } ?: false
    }

    val isCondo: LiveData<Boolean> = Transformations.map(propertySubType) {
        it?.run { PropertyTypeUtil.isCondo(type) } ?: false
    }

    val isLanded: LiveData<Boolean> = Transformations.map(propertySubType) {
        it?.run { PropertyTypeUtil.isLanded(type) } ?: false
    }

    val isCommercial: LiveData<Boolean> = Transformations.map(propertySubType) {
        it?.run { PropertyTypeUtil.isCommercial(type) } ?: false
    }

    @Inject
    lateinit var transactionRepository: TransactionRepository

    val listItems = MutableLiveData<List<TableListResponse.Transactions.Result>>()

    val sortType: MutableLiveData<TransactionEnum.SortType> by lazy {
        MutableLiveData<TransactionEnum.SortType>().apply {
            value = TransactionEnum.SortType.DEFAULT
        }
    }

    val scale = MutableLiveData<Float>().apply { value = 1.0f }

    val shouldEnableHorizontalScrollView: LiveData<Boolean> = Transformations.map(scale) {
        val scale = (it ?: 1.0f)
        scale <= 1.0f
    }

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        val projectTransactionRequest = projectTransactionRequest.value ?: return
        val sortType = sortType.value ?: return
        projectTransactionRequest.orderByParam = sortType.value
        val page = page.value ?: return
        projectTransactionRequest.page = page
        if (page == 1) {
            performRequest(transactionRepository.projectTableList(projectTransactionRequest))
        } else {
            performNextRequest(transactionRepository.projectTableList(projectTransactionRequest))
        }
    }

    fun canLoadNext(): Boolean {
        val total = mainResponse.value?.transactions?.total ?: 0
        val currentSize = listItems.value?.size ?: 0
        return mainStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM && currentSize < total
    }

    fun maybeRescale(listWidth: Int, screenWidth: Int) {
        if (listWidth <= 0 || screenWidth <= 0) return
        val newScale = min(2.0f, max(1.0f, screenWidth.toFloat() / listWidth.toFloat()))
        if (scale.value != newScale) {
            scale.postValue(newScale)
        }
    }

    override fun shouldResponseBeOccupied(response: TableListResponse): Boolean {
        val results = response.transactions.results
        val page = page.value ?: 1
        listItems.value = if (page > 1) {
            (listItems.value ?: emptyList()) + results
        } else {
            results
        }
        return listItems.value?.isNotEmpty() == true
    }
}