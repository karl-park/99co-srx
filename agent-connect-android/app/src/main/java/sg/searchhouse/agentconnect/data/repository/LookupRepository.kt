package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.lookup.*
import javax.inject.Inject
import javax.inject.Singleton

// Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/698318849/Lookup+API+V1
@Singleton
class LookupRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun lookupHdbTowns(): Call<LookupHdbTownsResponse> {
        return srxDataSource.lookupHdbTowns()
    }

    fun lookupSingaporeDistricts(): Call<LookupSingaporeDistrictsResponse> {
        return srxDataSource.lookupSingaporeDistricts()
    }

    fun lookupMrts(): Call<LookupMrtsResponse> {
        return srxDataSource.lookupMrts()
    }

    fun lookupSchools(): Call<LookupSchoolsResponse> {
        return srxDataSource.lookupSchools()
    }

    fun lookupBedrooms(): Call<LookupBedroomsResponse> {
        return srxDataSource.lookupBedrooms()
    }

    fun lookupListingFeaturesFixturesAreas(category: Int): Call<LookupListingFeaturesFixturesAreasResponse> {
        //cdResearchType & category is same . from listing edit po property type is category
        return srxDataSource.lookupListingFeaturesFixturesAreas(category)
    }

    fun lookupModels(): Call<LookupModelsResponse> {
        return srxDataSource.lookupModels()
    }
}