package sg.searchhouse.agentconnect.util

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapUtil constructor(private val googleMap: GoogleMap) {
    fun addDefaultMarker(latLng: LatLng) {
        googleMap.addMarker(
            MarkerOptions().icon(
                BitmapDescriptorFactory.defaultMarker()
            ).position(latLng)
        )
    }

    fun moveCameraToPosition(latLng: LatLng, zoom: Float = DEFAULT_ZOOM) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        googleMap.moveCamera(cameraUpdate)
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
    }
}