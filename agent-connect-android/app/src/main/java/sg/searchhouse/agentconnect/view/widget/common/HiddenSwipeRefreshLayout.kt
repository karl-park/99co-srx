package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class HiddenSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {
    init {
        setProgressViewOffset(false, -progressCircleDiameter - 8, -progressCircleDiameter - 8)
        setDistanceToTriggerSync(1)
    }
}