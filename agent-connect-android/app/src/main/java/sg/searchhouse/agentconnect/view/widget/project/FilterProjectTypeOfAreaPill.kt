package sg.searchhouse.agentconnect.view.widget.project

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterProjectTypeOfAreaBinding

class FilterProjectTypeOfAreaPill(context: Context) : FrameLayout(context, null) {
    val binding: PillFilterProjectTypeOfAreaBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_filter_project_type_of_area,
        this,
        true
    )
}