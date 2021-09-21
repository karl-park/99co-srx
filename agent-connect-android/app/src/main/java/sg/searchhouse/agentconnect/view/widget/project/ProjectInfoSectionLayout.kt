package sg.searchhouse.agentconnect.view.widget.project

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutProjectInfoSectionBinding

class ProjectInfoSectionLayout(context: Context) : FrameLayout(context, null) {
    val binding: LayoutProjectInfoSectionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_project_info_section,
        this,
        true
    )
}