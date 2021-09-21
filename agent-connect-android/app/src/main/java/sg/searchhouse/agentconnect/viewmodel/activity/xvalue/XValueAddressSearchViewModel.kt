package sg.searchhouse.agentconnect.viewmodel.activity.xvalue

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.XValueRepository
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class XValueAddressSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<SearchWithWalkupResponse>(application) {

    @Inject
    lateinit var repository: XValueRepository

    @Inject
    lateinit var applicationContext: Context

    val searchText: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }

    fun findProperties() {
        val searchText = searchText.value
        if (searchText?.isNotEmpty() == true) {
            performRequest(repository.searchWithWalkup(searchText))
        } else {
            mainResponse.postValue(null)
        }
    }

    fun afterTextChangedSearchText(text: String) {
        searchText.postValue(text)
    }

    override fun shouldResponseBeOccupied(response: SearchWithWalkupResponse): Boolean {
        return response.data.isNotEmpty()
    }
}