package sg.searchhouse.agentconnect.view.widget.watchlist

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillWatchlistPropertySubTypeBinding

class WatchlistPropertySubTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillWatchlistPropertySubTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_watchlist_property_sub_type,
        this,
        true
    )
}