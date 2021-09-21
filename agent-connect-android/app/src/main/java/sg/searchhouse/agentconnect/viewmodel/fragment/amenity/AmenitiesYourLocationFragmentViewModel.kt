package sg.searchhouse.agentconnect.viewmodel.fragment.amenity

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleDirectionsResponse
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class AmenitiesYourLocationFragmentViewModel constructor(application: Application) :
    CoreViewModel(application) {
    val travelMode = MutableLiveData<LocationEnum.TravelMode>()
    val listingLocation = MutableLiveData<AmenityLocation>()
    val destination = MutableLiveData<AmenityLocation>()

    val listingAddress: LiveData<String> = Transformations.map(listingLocation) { it.address }
    val destinationAddress: LiveData<String> = Transformations.map(destination) { it.address }

    val isRouteReady = MediatorLiveData<Boolean>()

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    private val transitResponse = MutableLiveData<GoogleDirectionsResponse>()
    private val walkingResponse = MutableLiveData<GoogleDirectionsResponse>()
    private val drivingResponse = MutableLiveData<GoogleDirectionsResponse>()

    // TODO: Might want to refactor into duration and polyline points separately, it wasn't work
    val transitDurationPolylinePoints: LiveData<Pair<String?, String?>> = Transformations.map(transitResponse) {
        val duration = it.routes.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
        val polylinePoints = it.routes.getOrNull(0)?.overviewPolyline?.points
        Pair(duration, polylinePoints)
    }

    val walkingDurationPolylinePoints: LiveData<Pair<String?, String?>> = Transformations.map(walkingResponse) {
        val duration = it.routes.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
        val polylinePoints = it.routes.getOrNull(0)?.overviewPolyline?.points
        Pair(duration, polylinePoints)
    }

    val drivingDurationPolylinePoints: LiveData<Pair<String?, String?>> = Transformations.map(drivingResponse) {
        val duration = it.routes.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
        val polylinePoints = it.routes.getOrNull(0)?.overviewPolyline?.points
        Pair(duration, polylinePoints)
    }

    init {
        viewModelComponent.inject(this)
        setupIsRouteReady()
    }

    private fun setupIsRouteReady() {
        isRouteReady.addSource(destination) {
            isRouteReady.postValue(it != null && listingLocation.value != null)
        }
        isRouteReady.addSource(listingLocation) {
            isRouteReady.postValue(it != null && destination.value != null)
        }
    }

    fun performRequests() {
        val origin = listingLocation.value?.getLatLng() ?: return
        val destination = destination.value?.getLatLng() ?: return

        performRequest(origin, destination, LocationEnum.TravelMode.WALKING, walkingResponse)
        performRequest(origin, destination, LocationEnum.TravelMode.DRIVING, drivingResponse)
        performRequest(origin, destination, LocationEnum.TravelMode.TRANSIT, transitResponse)
    }

    private fun performRequest(
        origin: LatLng,
        destination: LatLng,
        travelMode: LocationEnum.TravelMode,
        responseLiveData: MutableLiveData<GoogleDirectionsResponse>
    ) {
        ApiUtil.performRequest(
            applicationContext,
            locationSearchRepository.getDirections(
                origin,
                destination,
                travelMode
            ),
            onSuccess = {
                responseLiveData.postValue(it)
            },
            onFail = {
                // Nothing to do
            },
            onError = {
                // Nothing to do
            })
    }

    fun findCurrentLocation(location: Location) {
        ApiUtil.performRequest(applicationContext,
            locationSearchRepository.findCurrentLocation(location.latitude, location.longitude),
            onSuccess = { response ->
                destination.postValue(AmenityLocation(response.property))
            },
            onFail = {
                // Nothing to do
            },
            onError = {
                // Nothing to do
            })
    }
}