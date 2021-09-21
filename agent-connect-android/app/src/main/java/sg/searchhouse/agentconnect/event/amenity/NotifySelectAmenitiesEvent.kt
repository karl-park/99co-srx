package sg.searchhouse.agentconnect.event.amenity

import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO

class NotifySelectAmenitiesEvent(val amenityOption: LocationEnum.AmenityOption, val amenities: List<AmenitiesPO>)