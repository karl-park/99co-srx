package sg.searchhouse.agentconnect.view.widget.transaction

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterRadiusBinding

class FilterRadiusPill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterRadiusBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_radius, this, true)
}