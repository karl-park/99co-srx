package sg.searchhouse.agentconnect.viewmodel.fragment.transaction

import android.app.Application
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.model.api.transaction.PaginatedProjectListResponse
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchCriteriaV2VO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class TransactionProjectsViewModel constructor(application: Application) :
    ApiBaseViewModel<PaginatedProjectListResponse>(
        application
    ) {
    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var applicationContext: Context

    val listItems = MediatorLiveData<List<Any>>()

    val transactionSearchCriteriaV2VO = MutableLiveData<TransactionSearchCriteriaV2VO>()

    val page = MutableLiveData<Int>()

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        getBody()?.run {
            when (page) {
                1 -> {
                    performRequest(transactionRepository.paginatedProjectList(this))
                }
                else -> {
                    performNextRequest(transactionRepository.paginatedProjectList(this))
                }
            }
        }
    }

    private fun getBody(): TransactionSearchCriteriaV2VO? {
        val body = transactionSearchCriteriaV2VO.value ?: return null
        body.page = page.value ?: return null
        body.limit = PAGE_SIZE
        return body
    }

    fun maybeAddPage() {
        if (mainStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM) {
            if (!isTotalReached()) {
                val currentPage = page.value ?: return
                page.postValue(currentPage + 1)
            }
        }
    }

    private fun isTotalReached(): Boolean {
        val total = mainResponse.value?.projects?.total ?: return true
        val currentSize =
            listItems.value?.map { it is PaginatedProjectListResponse.Projects.Project }?.size
                ?: 0
        return currentSize >= total
    }

    override fun shouldResponseBeOccupied(response: PaginatedProjectListResponse): Boolean {
        val currentPage = page.value ?: 1
        val newListItems = if (currentPage > 1) {
            val existing = listItems.value?.filter { it !is Loading } ?: emptyList()
            existing + response.projects.results
        } else {
            response.projects.results
        }
        listItems.postValue(newListItems)
        return newListItems.isNotEmpty()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}