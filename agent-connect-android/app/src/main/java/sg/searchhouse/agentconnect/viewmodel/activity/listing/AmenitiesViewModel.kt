package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesCategoryPO
import sg.searchhouse.agentconnect.model.api.listing.GetListingResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class AmenitiesViewModel constructor(application: Application) :
    ApiBaseViewModel<GetListingResponse>(application) {
    override fun shouldResponseBeOccupied(response: GetListingResponse): Boolean {
        return response.fullListing.listingDetailPO.amenitiesCategories.isNotEmpty()
    }

    val amenityCategories: LiveData<List<AmenitiesCategoryPO>> = Transformations.map(mainResponse) {
        it?.fullListing?.listingDetailPO?.amenitiesCategories ?: emptyList()
    }

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    val selectedAmenityOption = MutableLiveData<LocationEnum.AmenityOption>()
    val travelMode = MutableLiveData<LocationEnum.TravelMode>()

    val polylinePoints = MutableLiveData<String>()

    var listingPO: LiveData<ListingPO> =
        Transformations.map(mainResponse) { mainResponse ->
            mainResponse?.fullListing?.listingDetailPO?.listingPO
        }

    val destination = MutableLiveData<AmenityLocation>()

    val shouldShowMap = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
        travelMode.value = LocationEnum.TravelMode.TRANSIT
    }

    fun performRequest(listingId: String, listingType: String) {
        performRequest(listingSearchRepository.getListing(listingId, listingType))
    }
}