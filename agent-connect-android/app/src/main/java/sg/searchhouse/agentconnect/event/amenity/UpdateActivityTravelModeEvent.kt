package sg.searchhouse.agentconnect.event.amenity

import sg.searchhouse.agentconnect.enumeration.api.LocationEnum

class UpdateActivityTravelModeEvent(val travelMode: LocationEnum.TravelMode, val polylinePoints: String?)