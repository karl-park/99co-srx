package sg.searchhouse.agentconnect.util

import okhttp3.logging.HttpLoggingInterceptor

object InterceptorUtil {
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        //To show request and response information of BODY level
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }
}