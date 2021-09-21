package sg.searchhouse.agentconnect.data.domain

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.GoogleEndpoint
import sg.searchhouse.agentconnect.model.app.Domain
import sg.searchhouse.agentconnect.util.InterceptorUtil
import javax.inject.Inject

class GoogleApiDomain @Inject constructor(val applicationContext: Context) : Domain() {
    override fun getBaseUrl(): String {
        return GoogleEndpoint.GOOGLE_API_BASE_URL
    }

    override fun getOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = getOkHttpClientBuilder()
        return okHttpClientBuilder.addInterceptor(InterceptorUtil.getHttpLoggingInterceptor())
            .addInterceptor(getAddApiKeyInterceptor())
            .build()
    }

    private fun getAddApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val key = applicationContext.getString(R.string.api_key_youtube)
            val httpUrl = chain.request().url.newBuilder().addQueryParameter("key", key).build()
            val request = chain.request().newBuilder().url(httpUrl).build()
            chain.proceed(request)
        }
    }
}