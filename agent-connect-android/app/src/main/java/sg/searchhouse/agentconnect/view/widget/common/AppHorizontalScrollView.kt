package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class AppHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    HorizontalScrollView(context, attrs, defStyleAttr) {

    private var isScrollEnabled: Boolean = true

    override fun setEnabled(enabled: Boolean) {
        isScrollEnabled = enabled
        super.setEnabled(enabled)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isScrollEnabled) return false
        return super.onInterceptTouchEvent(ev)
    }
}