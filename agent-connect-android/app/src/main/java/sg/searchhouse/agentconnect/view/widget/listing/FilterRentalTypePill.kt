package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterRentalTypeBinding

class FilterRentalTypePill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterRentalTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_rental_type, this, true)
}