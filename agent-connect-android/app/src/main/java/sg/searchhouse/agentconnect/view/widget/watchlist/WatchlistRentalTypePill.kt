package sg.searchhouse.agentconnect.view.widget.watchlist

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillWatchlistRentalTypeBinding

class WatchlistRentalTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillWatchlistRentalTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_watchlist_rental_type,
        this,
        true
    )
}