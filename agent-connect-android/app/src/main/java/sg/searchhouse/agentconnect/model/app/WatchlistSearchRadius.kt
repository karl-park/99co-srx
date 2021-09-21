package sg.searchhouse.agentconnect.model.app

import android.content.Context
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil

class WatchlistSearchRadius(val valueInMeter: Int?) {
    fun getLabel(context: Context): String {
        return valueInMeter?.run {
            val inKm = when {
                this % 1000 == 0 -> (this / 1000).toString()
                else -> NumberUtil.roundDouble(this / 1000.0, 1).toString()
            }
            context.getString(R.string.label_watchlist_search_radius, inKm)
        } ?: context.getString(R.string.label_watchlist_search_radius_any)
    }
}