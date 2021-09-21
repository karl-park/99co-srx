package sg.searchhouse.agentconnect.viewmodel.activity.common

import android.app.Application
import android.content.Context
import android.location.Location
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.SearchEnum
import sg.searchhouse.agentconnect.model.api.location.FindLocationSuggestionsResponse
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.util.LocationUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class LocationSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<FindLocationSuggestionsResponse>(application) {

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var locationUtil: LocationUtil

    @Inject
    lateinit var applicationContext: Context

    val searchText = MutableLiveData<String>()
    val isSearchingLocation = MutableLiveData<Boolean>()
    val currentLocation = MutableLiveData<PropertyPO>()
    val isIncludeNewProject = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
    }

    val suggestions: LiveData<List<Pair<String, List<LocationEntryPO>>>> =
        Transformations.map(mainResponse) { response ->
            response?.suggestions?.groupBy { suggestion ->
                suggestion.type
            }?.map { entryMap ->
                Pair(entryMap.key, entryMap.value.sortedBy { entry -> entry.displayText })
            } ?: emptyList()
        }

    private fun findLocationSuggestions() {
        val searchText = searchText.value ?: return
        performRequest(
            locationSearchRepository.findLocationSuggestions(
                searchText,
                SearchEnum.FindLocationSuggestionSource.BUILDING_SEARCH,
                isIncludeNewProject.value ?: false
            )
        )
    }

    fun afterTextChangedSearchText(editable: Editable?) {
        val text = editable?.toString() ?: ""
        //start doing search more than 4 characters
        searchText.postValue(text)
        findLocationSuggestions()
    }

    fun getCurrentLocation(getLocationName: (Location) -> Unit) {
        if (locationUtil.getCurrentLocation(getLocationName)) {
            isSearchingLocation.postValue(true)
        }
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

    override fun shouldResponseBeOccupied(response: FindLocationSuggestionsResponse): Boolean {
        return response.suggestions.isNotEmpty()
    }
}