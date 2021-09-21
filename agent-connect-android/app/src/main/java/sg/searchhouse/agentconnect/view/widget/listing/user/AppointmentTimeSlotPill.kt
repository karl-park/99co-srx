package sg.searchhouse.agentconnect.view.widget.listing.user

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillAppointmentTimeSlotBinding

class AppointmentTimeSlotPill(context: Context) : FrameLayout(context, null) {
    val binding: PillAppointmentTimeSlotBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_appointment_time_slot,
        this, true
    )
}