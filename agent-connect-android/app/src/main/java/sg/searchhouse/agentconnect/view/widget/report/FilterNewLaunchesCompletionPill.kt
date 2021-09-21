package sg.searchhouse.agentconnect.view.widget.report

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterNewLaunchesCompletionBinding

class FilterNewLaunchesCompletionPill(context: Context) : FrameLayout(context, null) {
    val binding: PillFilterNewLaunchesCompletionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_filter_new_launches_completion,
        this, true
    )
}