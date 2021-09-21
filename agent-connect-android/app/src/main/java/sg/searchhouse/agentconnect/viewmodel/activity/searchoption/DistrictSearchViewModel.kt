package sg.searchhouse.agentconnect.viewmodel.activity.searchoption

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.model.api.lookup.LookupSingaporeDistrictsResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DistrictSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<LookupSingaporeDistrictsResponse>(application) {

    @Inject
    lateinit var lookupRepository: LookupRepository
    @Inject
    lateinit var applicationContext: Context

    val selectedDistrictIds: MutableLiveData<ArrayList<Int>> by lazy {
        MutableLiveData<ArrayList<Int>>().apply {
            value = ArrayList()
        }
    }

    init {
        viewModelComponent.inject(this)
    }

    fun loadDistricts() {
        performRequest(lookupRepository.lookupSingaporeDistricts())
    }

    override fun shouldResponseBeOccupied(response: LookupSingaporeDistrictsResponse): Boolean {
        return response.singaporeDistricts.isNotEmpty()
    }
}