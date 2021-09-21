package sg.searchhouse.agentconnect.view.widget.watchlist

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillWatchlistAreaTypeBinding

class WatchlistAreaTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillWatchlistAreaTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_watchlist_area_type,
        this,
        true
    )
}