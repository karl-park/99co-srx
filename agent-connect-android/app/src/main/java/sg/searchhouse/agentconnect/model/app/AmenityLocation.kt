package sg.searchhouse.agentconnect.model.app

import com.google.android.gms.maps.model.LatLng
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleNearByResponse
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import java.io.Serializable

class AmenityLocation: Serializable {
    val address: String
    val latitude: Double
    val longitude: Double

    constructor(propertyPO: PropertyPO) {
        address = propertyPO.address
        latitude = propertyPO.latitude
        longitude = propertyPO.longitude
    }

    constructor(locationEntryPO: LocationEntryPO) {
        address = locationEntryPO.displayText
        latitude = locationEntryPO.latitude
        longitude = locationEntryPO.longitude
    }

    @Throws(NumberFormatException::class)
    constructor(listingPO: ListingPO) {
        address = listingPO.address
        latitude = listingPO.latitude.toDouble()
        longitude = listingPO.longitude.toDouble()
    }

    @Throws(NumberFormatException::class)
    constructor(amenitiesPO: AmenitiesPO) {
        address = amenitiesPO.name
        latitude = amenitiesPO.latitude.toDouble()
        longitude = amenitiesPO.longitude.toDouble()
    }

    constructor(googleNearByResponseResult: GoogleNearByResponse.Result) {
        address = googleNearByResponseResult.name
        latitude = googleNearByResponseResult.geometry.location.lat
        longitude = googleNearByResponseResult.geometry.location.lng
    }

    fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}