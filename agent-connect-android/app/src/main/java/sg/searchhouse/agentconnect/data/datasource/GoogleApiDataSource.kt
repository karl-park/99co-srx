package sg.searchhouse.agentconnect.data.datasource

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import sg.searchhouse.agentconnect.constant.GoogleEndpoint
import sg.searchhouse.agentconnect.model.api.googleapi.GetYoutubeVideoInfoResponse

interface GoogleApiDataSource {
    @GET(GoogleEndpoint.YOUTUBE_VIDEOS)
    fun getYoutubeVideos(
        @Query("id") id: String,
        @Query("part") part: String
    ): Call<GetYoutubeVideoInfoResponse>
}