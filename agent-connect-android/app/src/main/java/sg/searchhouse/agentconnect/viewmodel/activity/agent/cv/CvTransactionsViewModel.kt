package sg.searchhouse.agentconnect.viewmodel.activity.agent.cv

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.TransactionOwnershipType
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.TransactionPropertyType
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertyMainType
import sg.searchhouse.agentconnect.model.api.agent.FindOtherAgentTransactionsResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class CvTransactionsViewModel constructor(application: Application) :
    ApiBaseViewModel<FindOtherAgentTransactionsResponse>(application) {

    @Inject
    lateinit var agentRepository: AgentRepository

    val title: MutableLiveData<String> = MutableLiveData()
    val totalProperties: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }

    fun findOtherAgentTransactions(
        agentId: Int,
        type: TransactionOwnershipType,
        propertyType: TransactionPropertyType
    ) {
        var cdResearchSubTypeString = ""
        if (propertyType == TransactionPropertyType.HDB) {
            cdResearchSubTypeString =
                PropertyMainType.HDB.propertySubTypes.map { it.type }.joinToString(",")

        } else if (propertyType == TransactionPropertyType.PRIVATE) {
            val condo = PropertyMainType.CONDO.propertySubTypes.map { it.type }.joinToString(",")
            val landed =
                PropertyMainType.LANDED.propertySubTypes.map { it.type }.joinToString(",")
            cdResearchSubTypeString = condo.plus(",").plus(landed)
        }

        performRequest(
            agentRepository.findOtherAgentTransactions(
                agentId,
                type.value,
                cdResearchSubTypeString
            )
        )
    }

    override fun shouldResponseBeOccupied(response: FindOtherAgentTransactionsResponse): Boolean {
        return true
    }
}