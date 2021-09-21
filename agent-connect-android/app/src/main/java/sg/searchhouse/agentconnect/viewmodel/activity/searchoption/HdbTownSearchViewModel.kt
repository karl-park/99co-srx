package sg.searchhouse.agentconnect.viewmodel.activity.searchoption

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.model.api.lookup.LookupHdbTownsResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class HdbTownSearchViewModel constructor(application: Application) : ApiBaseViewModel<LookupHdbTownsResponse>(application) {

    @Inject
    lateinit var lookupRepository: LookupRepository
    @Inject
    lateinit var applicationContext: Context

    val selectedHdbTownIds: MutableLiveData<ArrayList<Int>> by lazy {
        MutableLiveData<ArrayList<Int>>().apply {
            value = ArrayList()
        }
    }

    // TODO Remove
    val hdbTownsResponse: LiveData<LookupHdbTownsResponse> = Transformations.map(mainResponse) { mainResponse ->
        mainResponse
    }

    init {
        viewModelComponent.inject(this)
    }

    fun loadHdbTowns() {
        performRequest(lookupRepository.lookupHdbTowns())
    }

    override fun shouldResponseBeOccupied(response: LookupHdbTownsResponse): Boolean {
        return response.hdbTowns.isNotEmpty()
    }
}