package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterListingPropertySubTypeBinding

class FilterListingPropertySubTypePill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterListingPropertySubTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_listing_property_sub_type, this, true)
}