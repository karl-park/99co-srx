package sg.searchhouse.agentconnect.viewmodel.activity.agent.client

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.model.api.location.FindPropertiesResponse
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.util.LocationUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SearchClientViewModel constructor(application: Application) :
    ApiBaseViewModel<FindPropertiesResponse>(application) {

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var locationUtil: LocationUtil

    @Inject
    lateinit var applicationContext: Context

    val isSearchingLocation = MutableLiveData<Boolean>()

    val currentLocation: MutableLiveData<PropertyPO> = MutableLiveData()

    val searchText = MutableLiveData<String>()

    val properties: LiveData<List<PropertyPO>> = Transformations.map(mainResponse) {
        it?.properties ?: emptyList()
    }

    init {
        viewModelComponent.inject(this)
    }

    fun getCurrentLocation(getLocationName: (Location) -> Unit) {
        if (locationUtil.getCurrentLocation(getLocationName)) {
            isSearchingLocation.postValue(true)
        }
    }

    fun performRequest(searchText: String) {
        // Validate
        if (searchText.isEmpty()) {
            return mainResponse.postValue(null)
        }

        performRequest(
            locationSearchRepository.findProperties(
                searchText,
                AppConstant.BATCH_SIZE_DOUBLE // TODO Ask search size
            )
        )
    }

    fun findCurrentLocation(location: Location) {
        locationSearchRepository.findCurrentLocation(location.latitude, location.longitude)
            .performRequest(applicationContext,
                onSuccess = { response ->
                    currentLocation.postValue(response.property)
                    isSearchingLocation.postValue(false)
                },
                onFail = {
                    currentLocation.postValue(null)
                    isSearchingLocation.postValue(false)
                    ViewUtil.showMessage(R.string.toast_search_current_location_fail)
                },
                onError = {
                    currentLocation.postValue(null)
                    isSearchingLocation.postValue(false)
                    ViewUtil.showMessage(R.string.toast_search_current_location_error)
                })
    }

    override fun shouldResponseBeOccupied(response: FindPropertiesResponse): Boolean {
        return response.properties.isNotEmpty()
    }
}