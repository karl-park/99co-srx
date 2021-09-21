package sg.searchhouse.agentconnect.view.fragment.listing.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentBookAppointmentBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.app.AppointmentDateTime
import sg.searchhouse.agentconnect.event.listing.user.BookAppointmentEvent
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.view.widget.listing.user.AppointmentTimeSlotPill
import sg.searchhouse.agentconnect.view.helper.listing.user.CalendarDayDisableDecorator
import sg.searchhouse.agentconnect.view.helper.listing.user.CalendarDayEnableDecorator
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.BookAppointmentViewModel
import java.util.*

//note : use DateTimeUtil.DATE_FORMAT_4
class BookAppointmentDialogFragment :
    ViewModelFullWidthDialogFragment<BookAppointmentViewModel, DialogFragmentBookAppointmentBinding>() {

    companion object {
        private const val TAG = "BOOK_APPOINTMENT_DIALOG_FRAGMENT"
        private const val ARGUMENT_LISTING_ID_TYPES = "ARGUMENT_LISTING_ID_TYPES"
        private const val ARGUMENT_TIME_SLOTS = "ARGUMENT_TIME_SLOTS"

        fun newInstance(
            listingIdTypes: String,
            timeSlots: List<Long>
        ): BookAppointmentDialogFragment {
            val dialog = BookAppointmentDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARGUMENT_LISTING_ID_TYPES, listingIdTypes)
            bundle.putLongArray(ARGUMENT_TIME_SLOTS, timeSlots.toLongArray())
            dialog.arguments = bundle
            return dialog
        }

        fun launch(
            fragmentManager: FragmentManager,
            listingIdTypes: String,
            timeSlots: List<Long>
        ) {
            newInstance(listingIdTypes, timeSlots).show(fragmentManager, TAG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArgumentParams()
        observeLiveData()
        setupInitialDate()
        setOnClickListeners()
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        bundle.getString(ARGUMENT_LISTING_ID_TYPES)?.run {
            viewModel.listingIdType.value = this
        } ?: throw Throwable("Missing Listing Id Types")

        bundle.getLongArray(ARGUMENT_TIME_SLOTS)?.run {
            viewModel.availableBookingSlots.value = this.toList()
        } ?: throw Throwable("Missing list of time slots")
    }

    private fun setupInitialDate() {
        binding.calendar.addDecorator(CalendarDayDisableDecorator())
        //TODO: commented for now not sure. will need in future or not.
        //binding.calendar.state().edit().setMinimumDate(CalendarDay.today()).commit()
        //binding.calendar.setDateSelected(CalendarDay.today(), true)
    }

    private fun observeLiveData() {
        viewModel.availableBookingSlots.observeNotNull(this) {
            if (it.isNotEmpty()) {
                viewModel.handleTimeSlots()
            }
        }

        viewModel.availableTimeSlotsByDate.observeNotNull(this) {
            if (viewModel.minAppointmentDateTime != null) {
                val items = it[viewModel.minAppointmentDateTime?.dateString] ?: emptyList()
                populateTimeSlotViews(items)
            }
        }

        viewModel.enabledDates.observeNotNull(this) {
            binding.calendar.addDecorator(
                CalendarDayEnableDecorator(
                    it
                )
            )
        }

        viewModel.selectedTimeSlot.observeNotNull(this) {
            viewModel.timeSlotViews.map { pill ->
                val item = pill.binding.dateTime ?: return@map
                pill.binding.isSelected = viewModel.isTimeSlotSelected(item)
                pill.binding.invalidateAll()
            }
        }

        viewModel.minMaxCalendar.observeNotNull(this) {
            if (it.isNotEmpty()) {
                binding.calendar.setDateSelected(it.first(), true)
                binding.calendar.setCurrentDate(it.first(), true)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.ibClose.setOnClickListener { dismiss() }

        binding.calendar.setOnDateChangedListener { _, date, _ ->
            handleSelectedDate(
                date.year,
                date.month - 1,
                date.day
            )
        }

        binding.tvDone.setOnClickListener {
            val selectedTimeSlot = viewModel.selectedTimeSlot.value ?: return@setOnClickListener
            val listingIdType = viewModel.listingIdType.value ?: return@setOnClickListener
            RxBus.publish(
                BookAppointmentEvent(
                    listingIdType = listingIdType,
                    selectedTimeSlot = selectedTimeSlot
                )
            )
            dismiss()
        }
    }

    private fun handleSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val displayDate =
            DateTimeUtil.convertCalendarToString(calendar, DateTimeUtil.FORMAT_DATE_4)

        viewModel.availableTimeSlotsByDate.value?.run {
            val items = this[displayDate] ?: emptyList()
            populateTimeSlotViews(items)
        }
    }

    private fun populateTimeSlotViews(items: List<AppointmentDateTime>) {
        if (items.isNotEmpty()) {
            viewModel.isTimeAvailable.value = true
            viewModel.selectedTimeSlot.value = items.first()
            binding.layoutAvailableTime.removeAllViews()
            viewModel.timeSlotViews = items.map { item -> addTimeSlot(item) }
        } else {
            viewModel.isTimeAvailable.value = false
            viewModel.selectedTimeSlot.value = null
        }
    }

    private fun addTimeSlot(item: AppointmentDateTime): AppointmentTimeSlotPill {
        val mContext = context ?: throw Throwable("Missing context")
        val pill = AppointmentTimeSlotPill(mContext)
        pill.binding.dateTime = item
        pill.binding.isSelected = viewModel.isTimeSlotSelected(item)
        pill.binding.btnTimeSlot.setOnClickListener {
            viewModel.selectedTimeSlot.value = item
        }
        binding.layoutAvailableTime.addView(pill)
        return pill
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_book_appointment
    }

    override fun getViewModelClass(): Class<BookAppointmentViewModel> {
        return BookAppointmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

}