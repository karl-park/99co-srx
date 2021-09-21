package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout

class ScalableFrameLayout constructor(context: Context, attributeSet: AttributeSet? = null) :
    FrameLayout(context, attributeSet) {
    // Width and height
    private var originalSize: Pair<Int, Int>? = null

    private var scale: Float = 1.0f

    init {
        setWillNotDraw(false)
    }

    fun updateScale(scale: Float) {
        this.scale = scale
        invalidate()
    }

    private fun maybeUpdateLayoutSize() {
        if (originalSize == null) {
            originalSize = Pair(width, height)
        }
        originalSize?.run {
            val mLayoutParams = layoutParams
            mLayoutParams.width = (first * scale).toInt()
            mLayoutParams.height = (second * scale).toInt()
            layoutParams = mLayoutParams
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        maybeUpdateLayoutSize()
        canvas?.scale(scale, scale)
        super.onDraw(canvas)
    }
}