package sg.searchhouse.agentconnect.viewmodel.fragment.transaction

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.model.api.transaction.LatestRentalTransactionsResponse
import sg.searchhouse.agentconnect.model.api.transaction.TransactionListItem
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class ProjectRentalOfficialTransactionsViewModel constructor(application: Application) :
    ApiBaseViewModel<LatestRentalTransactionsResponse>(
        application
    ) {
    var projectId: Int = 0

    @Inject
    lateinit var transactionRepository: TransactionRepository

    val listItems = MutableLiveData<List<TransactionListItem>>()
    val isHdb = MutableLiveData<Boolean>()

    val scale = MutableLiveData<Float>().apply { value = 1.0f }

    val shouldEnableHorizontalScrollView: LiveData<Boolean> = Transformations.map(scale) {
        val scale = (it ?: 1.0f)
        scale <= 1.0f
    }

    init {
        viewModelComponent.inject(this)
    }

    fun maybeRescale(listWidth: Int, screenWidth: Int) {
        if (listWidth <= 0 || screenWidth <= 0) return
        val newScale = min(2.0f, max(1.0f, screenWidth.toFloat() / listWidth.toFloat()))
        if (scale.value != newScale) {
            scale.postValue(newScale)
        }
    }

    fun performRequest() {
        performRequest(transactionRepository.getLatestRentalTransactions(projectId))
    }

    override fun shouldResponseBeOccupied(response: LatestRentalTransactionsResponse): Boolean {
        val transactionList =
            response.rentalTransactionData.rentalOfficialTransactionPO.transactionList
                ?: emptyList()
        listItems.postValue(transactionList)
        return transactionList.isNotEmpty()
    }
}