package sg.searchhouse.agentconnect.dsl.widget

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import sg.searchhouse.agentconnect.util.ViewUtil
import kotlin.math.abs

fun NumberPicker.setValueIfNotEqual(value: Int) = run {
    if (this.value != value) {
        this.value = value
    }
}

// Coordinate animation of child items on each height change
fun LinearLayout.setupLayoutAnimation() = run {
    val layoutTransition = LayoutTransition()
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    this.layoutTransition = layoutTransition
}

// Coordinate animation of child items on each height change
fun RelativeLayout.setupLayoutAnimation() = run {
    val layoutTransition = LayoutTransition()
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    this.layoutTransition = layoutTransition
}

// Coordinate animation of child items on each height change
fun FrameLayout.setupLayoutAnimation() = run {
    val layoutTransition = LayoutTransition()
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    this.layoutTransition = layoutTransition
}

// Shortcut for `addOnPageChangeListener` if only `onPageSelected` method is needed
fun ViewPager.addOnPageSelectedListener(onPageSelected: (position: Int) -> Unit) =
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })

fun SeekBar.setOnSeekBarProgressChangeListener(onProgressChanged: (progress: Int, fromUser: Boolean) -> Unit) =
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChanged.invoke(progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    })

fun AppBarLayout.addOnOffsetChangedListener(onCollapse: () -> Unit, onExpand: () -> Unit) =
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        when {
            abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                // Collapse
                onCollapse.invoke()
            }
            verticalOffset == 0 -> {
                //Expand
                onExpand.invoke()
            }
            else -> {
                // scrolling
                println("Do nothing for scrolling state")
            }
        }
    })

fun NestedScrollView.listenScrollToBottom(reachBottom: () -> Unit) =
    ViewUtil.listenVerticalScrollEnd(this, reachBottom = {
        reachBottom.invoke()
    })

fun View.setDimension(height: Int, width: Int) = run {
    val layoutParams = layoutParams as ViewGroup.LayoutParams
    layoutParams.height = height
    layoutParams.width = width
    this.layoutParams = layoutParams
    invalidate()
}

fun View.moveMarginInFrameLayout(dX: Int, dY: Int) = run {
    val layoutParams = layoutParams as FrameLayout.LayoutParams
    layoutParams.topMargin = dY
    layoutParams.bottomMargin = -dY
    layoutParams.leftMargin = dX
    layoutParams.rightMargin = -dX
    this.layoutParams = layoutParams
    invalidate()
}