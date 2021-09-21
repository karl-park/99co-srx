package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.PropertyIndexRepository
import sg.searchhouse.agentconnect.enumeration.api.PropertyIndexEnum
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchGraphResponse
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchIndicesResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class MarketWatchGraphViewModel constructor(application: Application) : ApiBaseViewModel<LoadMarketWatchGraphResponse>(application) {
    val srxIndexPO = MutableLiveData<LoadMarketWatchIndicesResponse.SrxIndexPO>()

    @Inject
    lateinit var propertyIndexRepository: PropertyIndexRepository
    @Inject
    lateinit var applicationContext: Context

    val title = MutableLiveData<String>()

    val selectedIndicator = MutableLiveData<PropertyIndexEnum.Indicator>()

    val selectedTimeScale: MutableLiveData<PropertyIndexEnum.TimeScale> by lazy {
        MutableLiveData<PropertyIndexEnum.TimeScale>().apply {
            value = PropertyIndexEnum.TimeScale.ONE_YEAR
        }
    }

    init {
        viewModelComponent.inject(this)
        selectedIndicator.value = PropertyIndexEnum.Indicator.PRICE
    }

    fun performRequest(propertyType: PropertyIndexEnum.PropertyType, transactionType: PropertyIndexEnum.TransactionType) {
        performRequest(propertyIndexRepository.loadMarketWatchGraph(propertyType, transactionType))
    }

    fun toggleIndicator(indicator: PropertyIndexEnum.Indicator) {
        if (indicator != selectedIndicator.value) {
            selectedIndicator.postValue(indicator)
        }
    }

    fun selectTimeScale(timeScale: PropertyIndexEnum.TimeScale) {
        if (timeScale != selectedTimeScale.value) {
            selectedTimeScale.postValue(timeScale)
        }
    }

    override fun shouldResponseBeOccupied(response: LoadMarketWatchGraphResponse): Boolean {
        return response.priceGraphData.isNotEmpty()
    }
}