package sg.searchhouse.agentconnect.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import sg.searchhouse.agentconnect.BuildConfig
import sg.searchhouse.agentconnect.constant.AppConstant
import java.net.Inet4Address
import java.net.NetworkInterface

object PackageUtil {
    //application id
    fun getApplicationId(): String {
        return BuildConfig.APPLICATION_ID
    }

    //version name
    fun getAppVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    // Return version name
    // Strip `-staging` from version name to comply backend check
    fun getCompliantAppVersionName(): String {
        return getAppVersionName().replace(AppConstant.STAGING_FLAG, "")
    }

    fun isStagingVersion(): Boolean {
        return getAppVersionName().contains(AppConstant.STAGING_FLAG)
    }

    //device model
    fun getDeviceModel(): String {
        val deviceModel: String
        var model = Build.MODEL
        val manufacturer = Build.MANUFACTURER

        if (StringUtil.isEmpty(model)) {
            model = Build.PRODUCT
        }

        deviceModel = if (model.startsWith(manufacturer)) {
            model
        } else {
            manufacturer.plus(" ").plus(model)
        }

        return deviceModel
    }

    //device type
    fun getDeviceType(context: Context): String {
        val deviceType = "Android"
        if (isTablet(context)) {
            return deviceType.plus(" ").plus("Tablet")
        }
        return deviceType.plus(" ").plus("Mobile")
    }

    //table or not
    private fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    //get device id
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getClientIPAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let {
                return it.hostAddress
            }
        }
        return ""
    }
}