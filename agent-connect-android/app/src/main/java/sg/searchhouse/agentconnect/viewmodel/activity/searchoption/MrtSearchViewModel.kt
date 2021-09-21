package sg.searchhouse.agentconnect.viewmodel.activity.searchoption

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.model.api.lookup.LookupMrtsResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class MrtSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<LookupMrtsResponse>(application) {
    @Inject
    lateinit var lookupRepository: LookupRepository

    @Inject
    lateinit var applicationContext: Context

    val trainLines: LiveData<List<LookupMrtsResponse.TrainLine>> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.railTransitInformation
        }

    init {
        viewModelComponent.inject(this)
        performRequest(lookupRepository.lookupMrts())
    }

    fun getStations(): List<LookupMrtsResponse.TrainLine.Station> {
        return trainLines.value?.map { it.stations }?.flatten() ?: emptyList()
    }

    override fun shouldResponseBeOccupied(response: LookupMrtsResponse): Boolean {
        return response.railTransitInformation.isNotEmpty()
    }
}