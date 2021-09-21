package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import sg.searchhouse.agentconnect.R

class CustomSwipeRefreshLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    SwipeRefreshLayout(context, attributeSet) {

    init {
        setColorSchemeColors(
            ContextCompat.getColor(context, R.color.cyan),
            ContextCompat.getColor(context, R.color.blue),
            ContextCompat.getColor(context, R.color.cyan)
        )
    }
}