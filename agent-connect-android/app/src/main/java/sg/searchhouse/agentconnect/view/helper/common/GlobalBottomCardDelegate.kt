package sg.searchhouse.agentconnect.view.helper.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.behavior.SwipeDismissBehavior
import sg.searchhouse.agentconnect.view.widget.common.AppCardView

abstract class GlobalBottomCardDelegate<T : ViewDataBinding> constructor(
    private val activityParentLayoutRoot: FrameLayout,
    @LayoutRes val layoutResId: Int
) {
    val context: Context = activityParentLayoutRoot.context
    private var binding: T? = null

    // TODO Animate
    fun show(onBindViewData: (T) -> Unit) {
        binding?.root?.run { visibility = View.VISIBLE } ?: run { firstShow() }
        onBindViewData.invoke(binding!!) // Guaranteed
    }

    // TODO Animate
    fun dismiss() {
        binding?.root?.visibility = View.GONE
    }

    private fun firstShow() {
        binding = DataBindingUtil.inflate<T>(
            LayoutInflater.from(context),
            layoutResId,
            activityParentLayoutRoot,
            false
        )
        binding!!.root.run {
            setBottomGravity(this)
            activityParentLayoutRoot.addView(this)
        }
    }

    private fun setBottomGravity(generateHomeReportLayout: View) {
        val layoutParams = generateHomeReportLayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.BOTTOM
        generateHomeReportLayout.layoutParams = layoutParams
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupSwipeToDismiss(
        card: AppCardView,
        layoutParent: CoordinatorLayout,
        onDismiss: (() -> Unit)? = null
    ) {
        val params = card.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = SwipeDismissBehavior<CardView>()
        behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
        behavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
            override fun onDismiss(view: View?) {
                onDismiss?.invoke()
                dismiss()
            }

            override fun onDragStateChanged(state: Int) {
            }
        })
        params.behavior = behavior
        card.setOnTouchListener { _, event ->
            behavior.onInterceptTouchEvent(layoutParent, card, event)
        }
    }
}