package sg.searchhouse.agentconnect.event.listing.user

import sg.searchhouse.agentconnect.model.app.AppointmentDateTime

class BookAppointmentEvent(
    val listingIdType: String,
    val selectedTimeSlot: AppointmentDateTime
)