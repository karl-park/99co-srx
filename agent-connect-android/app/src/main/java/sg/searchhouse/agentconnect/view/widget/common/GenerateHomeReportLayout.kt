package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import sg.searchhouse.agentconnect.R

class GenerateHomeReportLayout constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    var root: View =
        LayoutInflater.from(context).inflate(R.layout.layout_home_report_progress, this, true)
}