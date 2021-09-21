package sg.searchhouse.agentconnect.view.widget.main.dashboard.conversation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import sg.searchhouse.agentconnect.R.layout

class DashboardConversationsEmpty constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    init {
        View.inflate(context, layout.card_dashboard_conversations_empty, this)
    }
}