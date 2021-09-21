package sg.searchhouse.agentconnect.view.widget.transaction

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterAreaTypeBinding

class FilterAreaTypePill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterAreaTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_area_type, this, true)
}