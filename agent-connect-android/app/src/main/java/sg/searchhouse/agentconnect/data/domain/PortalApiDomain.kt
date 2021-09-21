package sg.searchhouse.agentconnect.data.domain

import android.content.Context
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.model.app.Domain
import sg.searchhouse.agentconnect.util.InterceptorUtil
import javax.inject.Inject

class PortalApiDomain @Inject constructor(val applicationContext: Context) : Domain() {

    companion object {
        const val PORTAL_BASE_URL = "https://api.propertyguru.com.sg"
    }

    //all api url are dynamic
    override fun getBaseUrl(): String {
        return PORTAL_BASE_URL
    }

    override fun getOkHttpClient(): OkHttpClient {
        return getOkHttpClientBuilder()
            .addInterceptor(InterceptorUtil.getHttpLoggingInterceptor())
            .addNetworkInterceptor(InterceptorUtil.getHttpLoggingInterceptor())
            .build()
    }
}