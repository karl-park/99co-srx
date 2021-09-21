package sg.searchhouse.agentconnect.util

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.google.android.gms.maps.model.LatLng
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.MathConstant.EARTH_RADIUS
import sg.searchhouse.agentconnect.constant.MathConstant.RADIAN_PER_DEGREE
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class LocationUtil @Inject constructor(private val context: Context) {
    // Return true if success, vice versa
    // TODO: Throw security exception here
    fun getCurrentLocation(
        getLocationName: (Location) -> Unit,
        hasQuickUpdate: Boolean? = false
    ): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return when {
            isGpsEnabled -> {
                maybeSubmitLastKnownLocation(
                    locationManager,
                    LocationManager.GPS_PROVIDER,
                    hasQuickUpdate == true,
                    getLocationName
                )
                requestSingleLocationUpdate(LocationManager.GPS_PROVIDER, getLocationName)
                true
            }
            isNetworkEnabled -> {
                maybeSubmitLastKnownLocation(
                    locationManager,
                    LocationManager.NETWORK_PROVIDER,
                    hasQuickUpdate == true,
                    getLocationName
                )
                requestSingleLocationUpdate(LocationManager.NETWORK_PROVIDER, getLocationName)
                true
            }
            else -> {
                ViewUtil.showMessage(R.string.msg_turn_on_location_provider)
                false
            }
        }
    }

    private fun maybeSubmitLastKnownLocation(
        locationManager: LocationManager,
        providerName: String,
        hasQuickUpdate: Boolean,
        getLocationName: (Location) -> Unit
    ) {
        val lastKnownLocation = maybeGetLastKnownLocation(locationManager, providerName)
        if (hasQuickUpdate) {
            lastKnownLocation?.run { getLocationName.invoke(this) }
        }
    }

    private fun maybeGetLastKnownLocation(
        locationManager: LocationManager,
        providerName: String
    ): Location? {
        return try {
            locationManager.getLastKnownLocation(providerName)
        } catch (e: SecurityException) {
            ErrorUtil.handleError(context, R.string.exception_security, e)
            null
        }
    }

    private fun requestSingleLocationUpdate(
        providerName: String,
        getLocationName: (Location) -> Unit
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestSingleUpdate(
                providerName,
                object : LocationListener {
                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

                    override fun onProviderEnabled(p0: String) {}

                    override fun onProviderDisabled(providerName: String) {
                        ViewUtil.showMessage(R.string.msg_turn_on_location_provider)
                    }

                    override fun onLocationChanged(location: Location) {
                        location.let { getLocationName.invoke(it) } ?: run {
                            ViewUtil.showMessage(R.string.msg_turn_on_location_provider)
                        }
                    }
                },
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            ErrorUtil.handleError(context, R.string.exception_security, e)
        }
    }

    fun getDistance(location1: LatLng, location2: LatLng): Double {
        val lat1 = location1.latitude
        val lng1 = location1.longitude

        val lat2 = location2.latitude
        val lng2 = location2.longitude

        return when {
            lat1 == lat2 && lng1 == lng2 -> 0.0
            else -> {
                acos(
                    sin(lat1 * RADIAN_PER_DEGREE) *
                            sin(lat2 * RADIAN_PER_DEGREE) +
                            cos(lat1 * RADIAN_PER_DEGREE) *
                            cos(lat2 * RADIAN_PER_DEGREE) *
                            cos(lng1 * RADIAN_PER_DEGREE - lng2 * RADIAN_PER_DEGREE)
                ) * EARTH_RADIUS
            }
        }
    }
}