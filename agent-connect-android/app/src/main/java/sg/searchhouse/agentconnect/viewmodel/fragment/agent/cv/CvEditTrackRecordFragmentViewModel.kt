package sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.GetAgentTransactions
import sg.searchhouse.agentconnect.model.api.agent.GetAgentTransactions.AgentTransaction.TransactedListingPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class CvEditTrackRecordFragmentViewModel constructor(application: Application) :
    ApiBaseViewModel<GetAgentTransactions>(application) {

    @Inject
    lateinit var agentRepository: AgentRepository
    @Inject
    lateinit var applicationContext: Context

    val transactionType: MutableLiveData<AgentProfileAndCvEnum.TransactionType> = MutableLiveData()
    val saleTransactions: LiveData<List<TransactedListingPO>> =
        Transformations.map(mainResponse) { response ->
            response?.let {
                if (it.saleTransactions.listingPOs.isNotEmpty()) {
                    it.saleTransactions.listingPOs
                } else {
                    arrayListOf()
                }
            }
        }
    val rentTransactions: LiveData<List<TransactedListingPO>> =
        Transformations.map(mainResponse) { response ->
            response?.let {
                if (it.rentalTransactions.listingPOs.isNotEmpty()) {
                    it.rentalTransactions.listingPOs
                } else {
                    arrayListOf()
                }
            }
        }
    val apiCallResult: MutableLiveData<ApiStatus<DefaultResultResponse>> = MutableLiveData()
    val agentCv: MutableLiveData<AgentCvPO> = MutableLiveData()
    val selectedOrderCriteria: MutableLiveData<AgentProfileAndCvEnum.OrderCriteria> =
        MutableLiveData()
    val orderCriteriaLabel: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)

        transactionType.value = AgentProfileAndCvEnum.TransactionType.SOLD
        selectedOrderCriteria.value = AgentProfileAndCvEnum.OrderCriteria.CATEGORY
        orderCriteriaLabel.value = applicationContext.getString(R.string.order_criteria_category)
        getAgentTransactions()
    }

    fun getAgentTransactions(sortOrder: String = "") {
        performRequest(agentRepository.getAgentTransactions(sortOrder))
    }

    fun concealTransaction(transactionId: Int) {
        ApiUtil.performRequest(
            applicationContext,
            agentRepository.concealTransaction(transactionId),
            onSuccess = {
                apiCallResult.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallResult.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallResult.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun revealTransaction(transactionId: Int) {
        ApiUtil.performRequest(
            applicationContext,
            agentRepository.revealTransaction(transactionId),
            onSuccess = {
                apiCallResult.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                apiCallResult.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                apiCallResult.postValue(ApiStatus.errorInstance())
            }
        )
    }

    override fun shouldResponseBeOccupied(response: GetAgentTransactions): Boolean {
        return true
    }
}