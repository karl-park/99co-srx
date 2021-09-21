package sg.searchhouse.agentconnect.view.widget.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * To detect swipe up or down
 */
class SwipeDetectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    // y-position of global scroll
    private var swipingY: Float? = null

    @SuppressLint("ClickableViewAccessibility")
    fun setOnSwipeListener(onSwipeListener: (swipeDirection: SwipeDirection) -> Unit) {
        setOnTouchListener { _, event ->
            val thisY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    swipingY = thisY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    swipingY?.run {
                        when {
                            thisY > this -> {
                                // Down
                                swipingY = null
                                onSwipeListener.invoke(SwipeDirection.DOWN)
                                true
                            }
                            thisY < this -> {
                                // Up
                                swipingY = null
                                onSwipeListener.invoke(SwipeDirection.UP)
                                true
                            }
                            else -> false
                        }
                    } ?: false
                }
                else -> {
                    false
                }
            }
        }
    }

    enum class SwipeDirection {
        UP, DOWN
    }
}