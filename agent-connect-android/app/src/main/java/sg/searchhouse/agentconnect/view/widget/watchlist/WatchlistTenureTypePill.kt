package sg.searchhouse.agentconnect.view.widget.watchlist

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillWatchlistTenureTypeBinding

class WatchlistTenureTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillWatchlistTenureTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_watchlist_tenure_type,
        this,
        true
    )
}