package sg.searchhouse.agentconnect.event.project

import com.google.android.gms.maps.model.LatLng

class NotifyProjectInfoToActivityEvent(val projectName: String, val position: LatLng, val postalCode: String)