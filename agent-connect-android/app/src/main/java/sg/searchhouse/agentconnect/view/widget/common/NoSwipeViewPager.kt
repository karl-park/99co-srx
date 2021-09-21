package sg.searchhouse.agentconnect.view.widget.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NoSwipeViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    var isAnimateTransition = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun setCurrentItem(item: Int) {
        //disable smooth scroll means disable animation when dragging
        // Chun Hoe 20190917: Disable scroll, but still enable animation
        super.setCurrentItem(item, isAnimateTransition)
    }
}