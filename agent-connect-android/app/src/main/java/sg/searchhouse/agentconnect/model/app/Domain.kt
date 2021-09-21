package sg.searchhouse.agentconnect.model.app

import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.constant.AppConstant
import java.util.concurrent.TimeUnit

abstract class Domain {
    abstract fun getBaseUrl(): String
    abstract fun getOkHttpClient(): OkHttpClient

    fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        return okHttpClientBuilder
            .connectTimeout(AppConstant.DEFAULT_SERVER_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(AppConstant.DEFAULT_SERVER_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConstant.DEFAULT_SERVER_TIMEOUT, TimeUnit.SECONDS)
    }
}