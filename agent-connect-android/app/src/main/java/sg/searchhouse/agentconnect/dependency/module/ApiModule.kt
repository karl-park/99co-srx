package sg.searchhouse.agentconnect.dependency.module

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import sg.searchhouse.agentconnect.data.domain.SrxDomain
import sg.searchhouse.agentconnect.data.database.SrxRoomDatabase
import sg.searchhouse.agentconnect.data.datasource.*
import sg.searchhouse.agentconnect.data.domain.GoogleApiDomain
import sg.searchhouse.agentconnect.data.domain.GoogleMapDomain
import sg.searchhouse.agentconnect.data.domain.PortalApiDomain
import sg.searchhouse.agentconnect.util.ApiUtil

@Module(includes = [AppModule::class])
class ApiModule {
    @Provides
    internal fun provideOkHttpClient(srxDomain: SrxDomain): OkHttpClient {
        return srxDomain.getOkHttpClient()
    }

    @Provides
    internal fun provideSrxRetrofit(srxDomain: SrxDomain): Retrofit {
        return ApiUtil.createRetrofit(srxDomain)
    }

    @Provides
    internal fun provideSrxDataSource(srxRetrofit: Retrofit): SrxDataSource {
        return srxRetrofit.create(SrxDataSource::class.java)
    }

    @Provides
    internal fun provideGoogleMapDataSource(googleMapDomain: GoogleMapDomain): GoogleMapDataSource {
        return ApiUtil.createRetrofit(googleMapDomain).create(GoogleMapDataSource::class.java)
    }

    @Provides
    internal fun provideGoogleApiDataSource(googleApiDomain: GoogleApiDomain): GoogleApiDataSource {
        return ApiUtil.createRetrofit(googleApiDomain).create(GoogleApiDataSource::class.java)
    }

    @Provides
    internal fun providePortalDataSource(portalApiDomain: PortalApiDomain): PortalDataSource {
        return ApiUtil.createRetrofit(portalApiDomain).create(PortalDataSource::class.java)
    }

    @Provides
    internal fun provideLocalDataSource(applicationContext: Context): LocalDataSource {
        return SrxRoomDatabase.getDatabase(applicationContext).localDataSource()
    }


}