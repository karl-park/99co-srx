package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import sg.searchhouse.agentconnect.R

class ActivityParentLayout constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    val root: FrameLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.layout_activity_parent, this, true) as FrameLayout
}