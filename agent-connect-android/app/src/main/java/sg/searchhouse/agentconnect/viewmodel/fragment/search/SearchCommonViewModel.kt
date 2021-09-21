package sg.searchhouse.agentconnect.viewmodel.fragment.search

import android.app.Application
import android.content.Context
import android.location.Location
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.SearchEnum
import sg.searchhouse.agentconnect.model.api.location.FindLocationSuggestionsResponse
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.view.activity.listing.ListingsActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectDirectoryActivity
import sg.searchhouse.agentconnect.view.activity.report.newlaunches.NewLaunchesReportsActivity
import sg.searchhouse.agentconnect.view.activity.transaction.GroupTransactionsActivity
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

// View model for SearchListingsFragment
class SearchCommonViewModel constructor(application: Application) :
    ApiBaseViewModel<FindLocationSuggestionsResponse>(application) {
    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var applicationContext: Context

    val getCurrentLocationStatus: MutableLiveData<ApiStatus.StatusKey> = MutableLiveData()
    val currentLocation: MutableLiveData<PropertyPO> = MutableLiveData()

    val searchResultType: MutableLiveData<SearchResultType> = MutableLiveData()

    val searchText: MutableLiveData<String> = MutableLiveData()

    val displayMode: MutableLiveData<DisplayMode> =
        MutableLiveData<DisplayMode>().apply { value = DisplayMode.DEFAULT }

    // Applied for residential transactions
    val propertyType = MutableLiveData<ListingEnum.PropertyMainType>()

    val suggestions: LiveData<List<Pair<String, List<LocationEntryPO>>>> =
        Transformations.map(mainResponse) { response ->
            response?.suggestions?.groupBy { suggestion ->
                suggestion.type
            }?.map { entryMap ->
                Pair(entryMap.key, entryMap.value.sortedBy { entry -> entry.displayText })
            } ?: emptyList()
        }

    val isSearchTextOccupied: LiveData<Boolean> = Transformations.map(searchText) {
        !TextUtils.isEmpty(it)
    }

    val propertyPurpose = MutableLiveData<ListingEnum.PropertyPurpose>()

    init {
        viewModelComponent.inject(this)
        // Set to empty layout by default
        mainStatus.value = ApiStatus.StatusKey.SUCCESS
    }

    fun maybePerformRequest() {
        val searchText = searchText.value?.trim() ?: return
        performRequest(
            locationSearchRepository.findLocationSuggestions(
                searchText,
                SearchEnum.FindLocationSuggestionSource.LISTING_SEARCH,
                false
            )
        )
    }

    override fun shouldResponseBeOccupied(response: FindLocationSuggestionsResponse): Boolean {
        return response.suggestions.isNotEmpty()
    }

    fun findCurrentLocation(location: Location) {
        ApiUtil.performRequest(applicationContext,
            locationSearchRepository.findCurrentLocation(location.latitude, location.longitude),
            onSuccess = { response ->
                currentLocation.postValue(response.property)
                getCurrentLocationStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = {
                getCurrentLocationStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                getCurrentLocationStatus.postValue(ApiStatus.StatusKey.ERROR)
            })
    }

    enum class DisplayMode {
        DEFAULT, SEARCH
    }

    enum class SearchResultType(val sharedPreferenceKey: String, val activityClass: Class<*>) {
        LISTINGS(
            SharedPreferenceKey.PREF_SEARCH_HISTORY_LISTINGS,
            ListingsActivity::class.java
        ),
        TRANSACTIONS(
            SharedPreferenceKey.PREF_SEARCH_HISTORY_TRANSACTIONS,
            GroupTransactionsActivity::class.java
        ),
        PROJECTS(
            SharedPreferenceKey.PREF_SEARCH_HISTORY_PROJECTS,
            ProjectDirectoryActivity::class.java
        ),
        NEW_LAUNCHES_REPORTS(
            SharedPreferenceKey.PREF_SEARCH_HISTORY_NEW_LAUNCHES_REPORTS,
            NewLaunchesReportsActivity::class.java
        )
    }
}