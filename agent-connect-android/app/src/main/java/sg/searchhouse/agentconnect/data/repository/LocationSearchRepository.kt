package sg.searchhouse.agentconnect.data.repository

import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.GoogleMapDataSource
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.enumeration.api.SearchEnum
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleDirectionsResponse
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleNearByResponse
import sg.searchhouse.agentconnect.model.api.location.*
import javax.inject.Inject
import javax.inject.Singleton

// API doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/694976513/Location+Search+V1+API
@Singleton
class LocationSearchRepository @Inject constructor(
    private val srxDataSource: SrxDataSource,
    private val googleMapDataSource: GoogleMapDataSource
) {
    fun findLocationSuggestions(
        searchText: String,
        source: SearchEnum.FindLocationSuggestionSource,
        isNewProject: Boolean
    ): Call<FindLocationSuggestionsResponse> {
        return srxDataSource.findLocationSuggestions(searchText, source.value, isNewProject)
    }

    fun findCurrentLocation(
        latitude: Double,
        longitude: Double
    ): Call<FindCurrentLocationResponse> {
        return srxDataSource.findCurrentLocation(latitude, longitude)
    }

    fun findProperties(searchText: String, maxResults: Int): Call<FindPropertiesResponse> {
        return srxDataSource.findProperties(searchText, maxResults)
    }

    fun getPropertySize(
        postalCode: Int,
        blk: String,
        cdResearchSubtype: Int,
        floorNo: String,
        unitNo: String
    ): Call<GetPropertyDataResponse> {
        return srxDataSource.getPropertyData(postalCode, blk, cdResearchSubtype, floorNo, unitNo)
    }

    fun getPropertyType(postalCode: Int, blk: String): Call<GetPropertyTypeResponse> {
        return srxDataSource.getPropertyType(postalCode, blk)
    }

    fun getPropertyData(
        postalCode: Int,
        blk: String,
        cdResearchSubtype: Int
    ): Call<GetPropertyDataResponse> {
        return srxDataSource.getPropertyData(
            postalCode,
            blk,
            cdResearchSubtype,
            floorNo = null,
            unitNo = null
        )
    }

    fun getDirections(
        origin: LatLng,
        destination: LatLng,
        travelMode: LocationEnum.TravelMode
    ): Call<GoogleDirectionsResponse> {
        val originString = "${origin.latitude},${origin.longitude}"
        val destinationString = "${destination.latitude},${destination.longitude}"
        return googleMapDataSource.getDirections(originString, destinationString, travelMode.value)
    }

    fun getNearBySearch(
        location: LatLng,
        placeType: LocationEnum.PlaceType
    ): Call<GoogleNearByResponse> {
        val locationString = "${location.latitude},${location.longitude}"
        return googleMapDataSource.getNearBySearch(
            locationString,
            placeType.value,
            AppConstant.NEARBY_SEARCH_RADIUS
        )
    }

    fun getAddressPropertyType(
        postalCode: Int,
        skipCommercial: Boolean,
        blk: String?
    ): Call<GetAddressPropertyTypeResponse> {
        return srxDataSource.getAddressPropertyType(postalCode, skipCommercial, blk)
    }
}