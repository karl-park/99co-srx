package sg.searchhouse.agentconnect.view.helper.listing.user

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import sg.searchhouse.agentconnect.R

//Note: Applying decorator for each date include in dates hashset by adding span and enable to select date.
class CalendarDayEnableDecorator(private val dates: HashSet<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setDaysDisabled(false)
        view?.addSpan(DotSpan(5F, R.color.red))
    }
}