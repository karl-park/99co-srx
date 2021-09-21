package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterFloorBinding

class FilterFloorPill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterFloorBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_floor, this, true)
}