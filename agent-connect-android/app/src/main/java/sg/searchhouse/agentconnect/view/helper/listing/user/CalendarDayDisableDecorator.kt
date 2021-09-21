package sg.searchhouse.agentconnect.view.helper.listing.user

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

//Note: This is for disabling calendar dates which are not allowed to be selected
class CalendarDayDisableDecorator : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setDaysDisabled(true)
    }

}