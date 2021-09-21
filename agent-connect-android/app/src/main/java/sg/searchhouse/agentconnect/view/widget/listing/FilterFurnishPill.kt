package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterFurnishBinding

class FilterFurnishPill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterFurnishBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_furnish, this, true)
}