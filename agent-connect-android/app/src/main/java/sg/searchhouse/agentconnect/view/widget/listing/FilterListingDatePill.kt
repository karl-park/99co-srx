package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterListingDateBinding

class FilterListingDatePill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterListingDateBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_listing_date, this, true)
}