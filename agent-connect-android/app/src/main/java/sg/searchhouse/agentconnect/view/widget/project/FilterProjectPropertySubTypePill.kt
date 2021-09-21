package sg.searchhouse.agentconnect.view.widget.project

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterProjectPropertySubTypeBinding

class FilterProjectPropertySubTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillFilterProjectPropertySubTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_filter_project_property_sub_type, this, true
    )
}