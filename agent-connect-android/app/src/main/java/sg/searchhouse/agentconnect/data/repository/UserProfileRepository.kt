package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.dsl.toMultiBodyPart
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.userprofile.GetProfileResponse
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API              : https://streetsine.atlassian.net/wiki/spaces/SIN/pages/703299585/User+Info+V1+API
 * Data structure   : https://streetsine.atlassian.net/wiki/spaces/SIN/pages/703135753/User+V1+Data+Structures
 */
@Singleton
class UserProfileRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getProfile(): Call<GetProfileResponse> {
        return srxDataSource.getProfile()
    }

    fun updatePhoto(file: File): Call<DefaultResultResponse> {
        return srxDataSource.updatePhoto(file.toMultiBodyPart())
    }

    fun removePhoto(): Call<DefaultResultResponse> {
        return srxDataSource.removePhoto()
    }
}