package sg.searchhouse.agentconnect.viewmodel.fragment.listing.user

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.prolificinteractive.materialcalendarview.CalendarDay
import sg.searchhouse.agentconnect.model.app.AppointmentDateTime
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.view.widget.listing.user.AppointmentTimeSlotPill
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import java.util.*
import kotlin.collections.HashSet

class BookAppointmentViewModel constructor(application: Application) : CoreViewModel(application) {

    var timeSlotViews = emptyList<AppointmentTimeSlotPill>()
    var minAppointmentDateTime: AppointmentDateTime? = null

    val listingIdType = MutableLiveData<String>()
    val availableBookingSlots = MutableLiveData<List<Long>>()

    val availableTimeSlotsByDate = MutableLiveData<Map<String, List<AppointmentDateTime>>>()
    val isTimeAvailable = MutableLiveData<Boolean>()
    val selectedTimeSlot = MutableLiveData<AppointmentDateTime>()
    val minMaxCalendar = MutableLiveData<List<CalendarDay>>()
    val enabledDates = MutableLiveData<HashSet<CalendarDay>>()

    init {
        viewModelComponent.inject(this)
    }

    fun handleTimeSlots() {
        //TODO: to refine or remove date from DateTime Convert String to Date
        val tempEnabledDates = arrayListOf<CalendarDay>()
        val unixTimeSlots = availableBookingSlots.value ?: return
        val availableAppointmentDates = unixTimeSlots.map { unixTime ->
            val timeStamp = unixTime * 1000L
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeStamp
            tempEnabledDates.add(
                CalendarDay.from(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            return@map AppointmentDateTime(
                unixTime,
                DateTimeUtil.convertTimeStampToString(timeStamp, DateTimeUtil.FORMAT_DATE_4),
                DateTimeUtil.convertTimeStampToString(timeStamp, DateTimeUtil.FORMAT_TIME)
            )
        }
        minAppointmentDateTime = availableAppointmentDates.first()
        minMaxCalendar.value = listOf(tempEnabledDates.first(), tempEnabledDates.last())
        availableTimeSlotsByDate.value =
            availableAppointmentDates.asSequence().groupBy { it.dateString }
        enabledDates.value = tempEnabledDates.distinct().toHashSet()
    }

    fun isTimeSlotSelected(appointment: AppointmentDateTime): Boolean {
        val tempSelectedDateTime = selectedTimeSlot.value ?: return false
        return tempSelectedDateTime.unixTime == appointment.unixTime
    }
}