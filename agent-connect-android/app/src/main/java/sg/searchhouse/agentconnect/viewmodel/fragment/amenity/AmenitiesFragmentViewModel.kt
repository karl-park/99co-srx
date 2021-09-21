package sg.searchhouse.agentconnect.viewmodel.fragment.amenity

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleDirectionsResponse
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ListBaseViewModel
import javax.inject.Inject

class AmenitiesFragmentViewModel constructor(application: Application) :
    ListBaseViewModel<AmenitiesPO>(application) {

    val travelMode = MutableLiveData<LocationEnum.TravelMode>()

    var listingLocation: AmenityLocation? = null
    val selectedAmenity = MutableLiveData<AmenitiesPO>()

    private val transitResponse = MutableLiveData<GoogleDirectionsResponse>()
    private val walkingResponse = MutableLiveData<GoogleDirectionsResponse>()
    private val drivingResponse = MutableLiveData<GoogleDirectionsResponse>()

    val transitDurationPolylinePoints: LiveData<Pair<String?, String?>> = Transformations.map(transitResponse) {
        val duration = it?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
        val polylinePoints = it?.routes?.getOrNull(0)?.overviewPolyline?.points
        Pair(duration, polylinePoints)
    }

    val walkingDurationPolylinePoints: LiveData<Pair<String?, String?>> = Transformations.map(walkingResponse) {
        val duration = it?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
        val polylinePoints = it?.routes?.getOrNull(0)?.overviewPolyline?.points
        Pair(duration, polylinePoints)
    }

    val drivingDurationPolylinePoints: LiveData<Pair<String?, String?>> = Transformations.map(drivingResponse) {
        val duration = it?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
        val polylinePoints = it?.routes?.getOrNull(0)?.overviewPolyline?.points
        Pair(duration, polylinePoints)
    }

    private val transitStatus = MutableLiveData<ApiStatus.StatusKey>()
    private val walkingStatus = MutableLiveData<ApiStatus.StatusKey>()
    private val drivingStatus = MutableLiveData<ApiStatus.StatusKey>()

    fun getTransitPolylinePoints(): String? {
        return transitResponse.value?.routes?.getOrNull(0)?.overviewPolyline?.points
    }

    fun getDrivingPolylinePoints(): String? {
        return drivingResponse.value?.routes?.getOrNull(0)?.overviewPolyline?.points
    }

    fun getWalkingPolylinePoints(): String? {
        return walkingResponse.value?.routes?.getOrNull(0)?.overviewPolyline?.points
    }

    val isRouteReady = MediatorLiveData<Boolean>()
    val isAllTravelModesReady = MediatorLiveData<Boolean>()

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    init {
        viewModelComponent.inject(this)
        setupIsRouteReady()
        setupIsAllTravelModesReady()
    }

    private fun setupIsAllTravelModesReady() {
        isAllTravelModesReady.addSource(transitStatus) {
            isAllTravelModesReady.postValue(
                it != ApiStatus.StatusKey.LOADING
                        && walkingStatus.value != ApiStatus.StatusKey.LOADING
                        && drivingStatus.value != ApiStatus.StatusKey.LOADING
            )
        }
        isAllTravelModesReady.addSource(drivingStatus) {
            isAllTravelModesReady.postValue(
                it != ApiStatus.StatusKey.LOADING
                        && walkingStatus.value != ApiStatus.StatusKey.LOADING
                        && transitStatus.value != ApiStatus.StatusKey.LOADING
            )
        }
        isAllTravelModesReady.addSource(walkingStatus) {
            isAllTravelModesReady.postValue(
                it != ApiStatus.StatusKey.LOADING
                        && transitStatus.value != ApiStatus.StatusKey.LOADING
                        && drivingStatus.value != ApiStatus.StatusKey.LOADING
            )
        }
    }

    private fun setupIsRouteReady() {
        isRouteReady.addSource(selectedAmenity) {
            isRouteReady.postValue(it != null && listingLocation != null)
        }
    }

    fun getTravelModeDuration(travelMode: LocationEnum.TravelMode): String? {
        val response = when (travelMode) {
            LocationEnum.TravelMode.TRANSIT -> transitResponse
            LocationEnum.TravelMode.WALKING -> walkingResponse
            LocationEnum.TravelMode.DRIVING -> drivingResponse
        }
        return response.value?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
    }

    fun performRequests() {
        val origin = listingLocation?.getLatLng() ?: return
        val destination = selectedAmenity.value?.getLatLng() ?: return

        resetAndPerformRequest(
            origin,
            destination,
            LocationEnum.TravelMode.WALKING,
            walkingResponse,
            walkingStatus
        )
        resetAndPerformRequest(
            origin,
            destination,
            LocationEnum.TravelMode.DRIVING,
            drivingResponse,
            drivingStatus
        )
        resetAndPerformRequest(
            origin,
            destination,
            LocationEnum.TravelMode.TRANSIT,
            transitResponse,
            transitStatus
        )
    }

    private fun resetAndPerformRequest(
        origin: LatLng,
        destination: LatLng,
        travelMode: LocationEnum.TravelMode,
        responseLiveData: MutableLiveData<GoogleDirectionsResponse>,
        statusLiveData: MutableLiveData<ApiStatus.StatusKey>
    ) {
        statusLiveData.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(
            applicationContext,
            locationSearchRepository.getDirections(
                origin,
                destination,
                travelMode
            ),
            onSuccess = {
                statusLiveData.postValue(ApiStatus.StatusKey.SUCCESS)
                responseLiveData.postValue(it)
            },
            onFail = {
                statusLiveData.postValue(ApiStatus.StatusKey.FAIL)
                responseLiveData.postValue(null)
            },
            onError = {
                statusLiveData.postValue(ApiStatus.StatusKey.ERROR)
                responseLiveData.postValue(null)
            })
    }

    fun toggleSelectAmenity(amenitiesPO: AmenitiesPO) {
        if (selectedAmenity.value?.id == amenitiesPO.id) {
            selectedAmenity.postValue(null)
        } else {
            selectedAmenity.postValue(amenitiesPO)
        }
    }
}