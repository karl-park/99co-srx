package sg.searchhouse.agentconnect.view.widget.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * To detect single direction scroll
 */
class ScrollDetectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var initial: Float? = null

    private var orientation = Orientation.VERTICAL

    @SuppressLint("ClickableViewAccessibility")
    fun setListeners(
        onBeginTouch: () -> Pair<Float, Orientation>, // Pair of initial distance, orientation
        onScroll: (distance: Float, orientation: Orientation) -> Unit
    ) {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val pair = onBeginTouch.invoke()
                    orientation = pair.second
                    initial = getEventPosition(event) + pair.first
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    initial?.run {
                        val distance = getEventPosition(event) - this
                        onScroll.invoke(distance, orientation)
                        true
                    } ?: false
                }
                MotionEvent.ACTION_UP -> {
                    initial?.run {
                        val distance = getEventPosition(event) - this
                        onScroll.invoke(distance, orientation)
                        initial = null
                        true
                    } ?: false
                }
                else -> false
            }
        }
    }

    private fun getEventPosition(event: MotionEvent): Float {
        return when (orientation) {
            Orientation.HORIZONTAL -> event.x
            Orientation.VERTICAL -> event.y
        }
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}