package sg.searchhouse.agentconnect.data.domain

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import sg.searchhouse.agentconnect.constant.ApiHeaderConstant
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.model.app.Domain
import sg.searchhouse.agentconnect.util.*
import javax.inject.Inject

class SrxDomain @Inject constructor(val applicationContext: Context) : Domain() {
    override fun getBaseUrl(): String {
        return ApiUtil.getBaseUrl(applicationContext, ConfigUtil.isRunningTest())
    }

    override fun getOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = getOkHttpClientBuilder()

        okHttpClientBuilder.addInterceptor(getHeaders(applicationContext))
        okHttpClientBuilder.addInterceptor(InterceptorUtil.getHttpLoggingInterceptor())
        okHttpClientBuilder.addInterceptor(getAddCookieInterceptor())
        okHttpClientBuilder.addInterceptor(getReceiveCookieInterceptor())
        okHttpClientBuilder.addNetworkInterceptor(InterceptorUtil.getHttpLoggingInterceptor())

        return okHttpClientBuilder.build()
    }

    private fun getHeaders(context: Context): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header(ApiHeaderConstant.PARAM_APP_NAME, AppConstant.APP_NAME)
                .header(
                    ApiHeaderConstant.PARAM_APP_VERSION,
                    PackageUtil.getCompliantAppVersionName()
                )
                .header(ApiHeaderConstant.PARAM_VERSION, PackageUtil.getCompliantAppVersionName())
                .header(ApiHeaderConstant.PARAM_DEVICE_MODEL, PackageUtil.getDeviceModel())
                .header(ApiHeaderConstant.PARAM_DEVICE_TYPE, PackageUtil.getDeviceType(context))
                .header(ApiHeaderConstant.PARAM_OS_VERSION, PackageUtil.getOsVersion())
                .header(ApiHeaderConstant.PARAM_UID, PackageUtil.getDeviceId(context))
                .build()
            chain.proceed(request)
        }
    }

    private fun getAddCookieInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            val cookies: HashSet<String> = SessionUtil.getCookies()
            for (cookie in cookies) {
                // Chun Hoe: Cut off elements (appended by okHttp?) after the first `;`
                // to be consistent with that of iOS, tentatively fix unhandled 400 HTML error bug
                val extractedCookie = cookie.split(";").firstOrNull()
                extractedCookie?.run {
                    Log.i("AddCookieToHeader", this)
                    request.addHeader("Cookie", this)
                }
            }
            Log.e("AddCookieToHeader", "--------------------------------------")
            chain.proceed(request.build())
        }
    }

    private fun getReceiveCookieInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            //saved cookie after successful
            if (response.isSuccessful) {
                if (response.headers("Set-Cookie").isNotEmpty()) {
                    //don't save cookie in local if there is only JSESSIONID cookie
                    val setCookies: List<String> =
                        response.headers("Set-Cookie").toString().split(",")
                    if (setCookies.size > 1) {
                        val cookies: HashSet<String> = HashSet(SessionUtil.getCookies())

                        for (header in response.headers("Set-Cookie")) {
                            if (SessionUtil.isCookieValueIncluded(header)) {
                                Log.e("SaveCookieInStorage", header)
                                cookies.add(header)
                            }
                        }
                        SessionUtil.setCookies(cookies)
                    } //end of setCookies condition
                }
            }
            response
        }
    }
}