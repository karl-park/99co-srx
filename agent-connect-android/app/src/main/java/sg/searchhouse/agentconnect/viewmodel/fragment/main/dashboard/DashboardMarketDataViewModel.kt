package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.PropertyIndexRepository
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchIndicesResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardMarketDataViewModel constructor(application: Application) :
    ApiBaseViewModel<LoadMarketWatchIndicesResponse>(application) {

    @Inject
    lateinit var propertyIndexRepository: PropertyIndexRepository

    @Inject
    lateinit var applicationContext: Context

    val isExpandCondoResale = MutableLiveData<Boolean>()
    val isExpandCondoRent = MutableLiveData<Boolean>()
    val isExpandHdbResale = MutableLiveData<Boolean>()
    val isExpandHdbRent = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest() {
        performRequest(propertyIndexRepository.loadMarketWatchIndices())
    }

    override fun shouldResponseBeOccupied(response: LoadMarketWatchIndicesResponse): Boolean {
        return true
    }
}