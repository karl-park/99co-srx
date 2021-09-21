package sg.searchhouse.agentconnect.data.datasource

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import sg.searchhouse.agentconnect.constant.GoogleEndpoint
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleDirectionsResponse
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleNearByResponse

interface GoogleMapDataSource {
    @GET(GoogleEndpoint.DIRECTIONS)
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String? = null
    ): Call<GoogleDirectionsResponse>

    @GET(GoogleEndpoint.NEAR_BY_SEARCH)
    fun getNearBySearch(
        @Query("location") origin: String,
        @Query("type") type: String,
        @Query("radius") radius: Int
    ): Call<GoogleNearByResponse>
}