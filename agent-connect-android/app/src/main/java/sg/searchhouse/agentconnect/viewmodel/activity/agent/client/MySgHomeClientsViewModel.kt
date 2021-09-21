package sg.searchhouse.agentconnect.viewmodel.activity.agent.client

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AgentClientRepository
import sg.searchhouse.agentconnect.model.api.agent.SRXPropertyUserPO
import sg.searchhouse.agentconnect.model.api.agentclient.GetAgentClientsResponse
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class MySgHomeClientsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetAgentClientsResponse>(application) {

    val propertyPO = MutableLiveData<PropertyPO>()

    val contentTitle: LiveData<String> = Transformations.map(mainResponse) {
        val clientSize = it?.clients?.size ?: 0
        val clientSizeString = NumberUtil.formatThousand(clientSize)
        when (clientSize) {
            0 -> applicationContext.getString(R.string.title_my_sg_home_clients_zero)
            else -> applicationContext.getString(
                R.string.title_my_sg_home_clients,
                clientSizeString
            )
        }
    }

    val clients: LiveData<List<SRXPropertyUserPO>> = Transformations.map(mainResponse) {
        it?.clients
    }
    val selectedClients = MutableLiveData<ArrayList<SRXPropertyUserPO>>()
    val selectedClientsLabel: LiveData<String> = Transformations.map(selectedClients) {
        return@map if (it != null) {
            applicationContext.resources.getQuantityString(
                R.plurals.label_selected_client_count,
                it.size,
                NumberUtil.formatThousand(it.size)
            )
        } else {
            applicationContext.getString(R.string.label_no_client_selected)
        }
    }

    val searchType: LiveData<GetAgentClientsResponse.SearchType> =
        Transformations.map(mainResponse) {
            try {
                // TODO Update `searchType` enum
                it?.getSearchTypeObject()
            } catch (e: IllegalArgumentException) {
                null
            }
        }

    val sortOrder = MutableLiveData<SortOrder>().apply { value = SortOrder.DESC }

    val header = MutableLiveData<String>()

    // Set value via `setQuery` method instead
    val query = MutableLiveData<String>()

    @Inject
    lateinit var agentClientRepository: AgentClientRepository

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
    }

    fun toggleSortOrder() {
        sortOrder.postValue(
            when (sortOrder.value) {
                SortOrder.ASC -> SortOrder.DESC
                SortOrder.DESC -> SortOrder.ASC
                else -> throw IllegalStateException("`sortOrder` cannot be null!")
            }
        )
    }

    fun setQuery(mQuery: String, mHeader: String? = null) {
        query.postValue(mQuery)
        header.postValue(mHeader ?: mQuery)
    }

    fun performRequest() {
        performRequest(
            agentClientRepository.getAgentClients(
                sortOrder.value?.value!!, // !! Must present
                query.value ?: ""
            )
        )
    }

    override fun shouldResponseBeOccupied(response: GetAgentClientsResponse): Boolean {
        return response.clients.isNotEmpty()
    }

    enum class SortOrder(
        val value: Boolean,
        @StringRes val addressLabel: Int,
        @StringRes val userLabel: Int
    ) {
        DESC(false, R.string.sort_order_address_desc, R.string.sort_order_user_desc),
        ASC(true, R.string.sort_order_address_asc, R.string.sort_order_user_asc)
    }
}