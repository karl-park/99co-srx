package sg.searchhouse.agentconnect.view.widget.transaction

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterTenureTypeBinding

class FilterTenureTypePill (context: Context) : FrameLayout(context, null) {
    val binding: PillFilterTenureTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_filter_tenure_type, this, true)
}