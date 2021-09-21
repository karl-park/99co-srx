package sg.searchhouse.agentconnect.model.app

import android.content.Context
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil

class WatchlistHeader(private val criteriaCount: Int, val propertyCount: Int) {
    fun getLabel(context: Context): String {
        // TODO Include property count when available
        return context.resources.getQuantityString(
            R.plurals.header_watchlist_listing,
            criteriaCount,
            NumberUtil.formatThousand(criteriaCount)
        )
    }
}