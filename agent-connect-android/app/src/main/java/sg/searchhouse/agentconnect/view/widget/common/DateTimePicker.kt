package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DateTimePickerBinding
import sg.searchhouse.agentconnect.dsl.widget.setValueIfNotEqual
import sg.searchhouse.agentconnect.enumeration.app.DateTimeEnum
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import java.util.*

class DateTimePicker constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private var calendar = Calendar.getInstance()

    // Minimum time set to current time by default
    // Need to be positive
    private var minTimeOffSetInMinutes: Int = 0

    fun setupMinOffset(minTimeOffSetInMinutes: Int) {
        this.minTimeOffSetInMinutes = minTimeOffSetInMinutes
        resetToMin()
    }

    val binding: DateTimePickerBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.date_time_picker, this, true)

    private val days =
        (0 until MAX_DAY_FORWARD).map { DateTimeUtil.getDateAfterNow(it, Calendar.DAY_OF_MONTH) }

    init {
        populateDay()
        populateHour()
        populateMinute()
        populateDayPeriod()
    }

    private fun populateDayPeriod() {
        binding.pickerDayPeriod.run {
            displayedValues = DateTimeEnum.DayPeriod.values().map { it.name }.toTypedArray()
            minValue = DateTimeEnum.DayPeriod.AM.position
            maxValue = DateTimeEnum.DayPeriod.PM.position
            value = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                DateTimeEnum.DayPeriod.AM.position
            } else {
                DateTimeEnum.DayPeriod.PM.position
            }
            setOnValueChangedListener { _, _, _ -> maybeResetToMin() }
        }
    }

    private fun populateMinute() {
        binding.pickerMinute.run {
            displayedValues =
                (0 until 60).map { NumberUtil.getTwoDigitNumberString(it) }.toTypedArray()
            minValue = 0
            maxValue = 59
            value = calendar.get(Calendar.MINUTE)
            setOnValueChangedListener { _, _, _ -> maybeResetToMin() }
        }
    }

    private fun populateHour() {
        binding.pickerHour.run {
            displayedValues =
                (0 until 12).map {
                    NumberUtil.getTwoDigitNumberString(
                        when (it) {
                            0 -> 12
                            else -> it
                        }
                    )
                }.toTypedArray()
            minValue = 0
            maxValue = 11
            value = calendar.get(Calendar.HOUR_OF_DAY) % 12
            setOnValueChangedListener { _, _, _ -> maybeResetToMin() }
        }
    }

    private fun populateDay() {
        binding.pickerDay.run {
            wrapSelectorWheel = false
            displayedValues = days.map {
                DateTimeUtil.getFormattedDateTime(
                    context,
                    it.time,
                    DateTimeUtil.FORMAT_DATE_11,
                    dateOnly = true,
                    simplifyThisWeek = false
                )
            }.toTypedArray()
            minValue = 0
            maxValue = MAX_DAY_FORWARD - 1
            value = 0
            setOnValueChangedListener { _, _, _ -> maybeResetToMin() }
        }
    }

    fun getSelectedTime(): Calendar {
        val calendar = days[binding.pickerDay.value]
        calendar.set(Calendar.AM_PM, binding.pickerDayPeriod.value)
        calendar.set(Calendar.MINUTE, binding.pickerMinute.value)
        calendar.set(Calendar.HOUR, binding.pickerHour.value)
        return calendar
    }

    private fun maybeResetToMin() {
        if (isSelectedBeforeMin()) {
            resetToMin()
        }
    }

    private fun resetToMin() {
        calendar = getMinTime()
        binding.run {
            val minDayPeriod = calendar.get(Calendar.AM_PM)
            val minMinute = calendar.get(Calendar.MINUTE)
            val minHour = calendar.get(Calendar.HOUR)
            val minDay =
                days.indexOfFirst { it.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) }

            pickerDayPeriod.setValueIfNotEqual(minDayPeriod)
            pickerMinute.setValueIfNotEqual(minMinute)
            pickerHour.setValueIfNotEqual(minHour)
            pickerDay.setValueIfNotEqual(minDay)
        }
    }

    private fun getMinTime(): Calendar {
        val minTime = Calendar.getInstance()
        minTime.add(Calendar.MINUTE, minTimeOffSetInMinutes)
        return minTime
    }

    // Check if the selected time is before specified minimum
    // Minute is the smallest unit
    private fun isSelectedBeforeMin(): Boolean {
        val selectedTime = getSelectedTime()
        val minTime = getMinTime()

        val minuteDifference = DateTimeUtil.getMinutesBetweenTimes(
            minTime.timeInMillis,
            selectedTime.timeInMillis + BUFFER_TIME_IN_MILLIS
        )
        return minuteDifference < 0
    }

    companion object {
        // TODO Confirm
        private const val MAX_DAY_FORWARD = 90
         const val BUFFER_TIME_IN_MILLIS = 59999 // Almost one minute in millis, for user to reasonably reset time
    }
}