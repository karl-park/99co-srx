package sg.searchhouse.agentconnect.viewmodel.activity.searchoption

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.LookupRepository
import sg.searchhouse.agentconnect.model.api.lookup.LookupSchoolsResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SchoolSearchViewModel constructor(application: Application) : ApiBaseViewModel<LookupSchoolsResponse>(application) {
    @Inject
    lateinit var lookupRepository: LookupRepository

    @Inject
    lateinit var applicationContext: Context

    val schools: LiveData<LookupSchoolsResponse.Schools> = Transformations.map(mainResponse) { mainResponse ->
        mainResponse?.schools
    }

    val isShowSchool: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>().apply { value = false } }

    init {
        viewModelComponent.inject(this)
        performRequest(lookupRepository.lookupSchools())
    }

    override fun shouldResponseBeOccupied(response: LookupSchoolsResponse): Boolean {
        return true
    }
}