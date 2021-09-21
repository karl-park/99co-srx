package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DatePickerBinding
import sg.searchhouse.agentconnect.util.DateTimeUtil
import java.util.*

class DatePicker constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private var calendar = Calendar.getInstance()
    private val yearRange = getYearRange()

    val binding: DatePickerBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.date_picker, this, true)

    init {
        initialize()
    }

    private fun initialize() {
        populateYears()
        populateMonths()
        initPopulateDays()
    }

    fun setDefaultDate(calendar: Calendar) {
        this.calendar = calendar
        initialize()
    }

    private fun populateYears() {
        binding.pickerYear.wrapSelectorWheel = false
        binding.pickerYear.displayedValues =
            yearRange.map { it.toString() }.toTypedArray()
        binding.pickerYear.minValue = 0
        binding.pickerYear.maxValue = yearRange.count() - 1
        binding.pickerYear.value = yearRange.indexOfFirst { it == calendar.get(Calendar.YEAR) }
        binding.pickerYear.setOnValueChangedListener { _, _, newVal ->
            resetYear(newVal)
            repopulateDays()
        }
    }

    private fun resetYear(newYearIndex: Int) {
        calendar.set(Calendar.YEAR, yearRange[newYearIndex])
        calendar.set(Calendar.MONTH, binding.pickerMonth.value)
    }

    // Months start from 0
    private fun populateMonths() {
        binding.pickerMonth.wrapSelectorWheel = false
        binding.pickerMonth.displayedValues =
            (0 until 12).map { DateTimeUtil.getMonthShortName(it) }.toTypedArray()
        binding.pickerMonth.minValue = 0
        binding.pickerMonth.maxValue = 11
        binding.pickerMonth.value = calendar.get(Calendar.MONTH)
        binding.pickerMonth.setOnValueChangedListener { _, _, newVal ->
            calendar.set(Calendar.MONTH, newVal)
            repopulateDays()
        }
    }

    // Initialize days
    private fun initPopulateDays() {
        populateDaysCore()
        binding.pickerDayOfMonth.wrapSelectorWheel = false
        binding.pickerDayOfMonth.setOnValueChangedListener { _, _, newVal ->
            calendar.set(Calendar.DAY_OF_MONTH, newVal)
        }
        binding.pickerDayOfMonth.value = calendar.get(Calendar.DAY_OF_MONTH)
    }

    // Repopulate days upon update of year and month value
    private fun repopulateDays() {
        populateDaysCore()
        binding.pickerDayOfMonth.value = 1
        calendar.set(Calendar.DAY_OF_MONTH, 1)
    }

    // Days value start from 1
    private fun populateDaysCore() {
        binding.pickerDayOfMonth.run {
            val daysOfMonth = DateTimeUtil.getDaysOfMonth(calendar)

            // Prevent `ArrayOutOfBoundExceptions`
            displayedValues = null

            // Order sensitive: `displayValues` must set after `minValue`, `maxValue`
            minValue = 1
            maxValue = daysOfMonth
            displayedValues = (1..daysOfMonth).map { it.toString() }.toTypedArray()
        }
    }

    private fun getYearRange(): List<Int> {
        val minYear = calendar.get(Calendar.YEAR) - YEAR_RANGE
        val maxYear = calendar.get(Calendar.YEAR) + YEAR_RANGE
        return (minYear until maxYear).toList()
    }

    fun getSelectedDate(): Calendar {
        return calendar
    }

    companion object {
        private const val YEAR_RANGE = 100
    }
}