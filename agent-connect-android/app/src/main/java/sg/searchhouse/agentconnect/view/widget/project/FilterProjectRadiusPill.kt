package sg.searchhouse.agentconnect.view.widget.project

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterProjectRadiusBinding

class FilterProjectRadiusPill(context: Context) : FrameLayout(context, null) {
    val binding: PillFilterProjectRadiusBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_filter_project_radius, this, true
    )
}