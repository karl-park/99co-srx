package sg.searchhouse.agentconnect.view.widget.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatImageButton
import androidx.viewpager.widget.ViewPager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.BottomAppBarBinding
import sg.searchhouse.agentconnect.tracking.NavigationBarTabTracker
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.main.MainBottomNavigationTab
import sg.searchhouse.agentconnect.view.widget.common.BaseTabLayout

class BottomAppBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabLayout<AppCompatImageButton>(context, attrs, defStyleAttr) {
    var onFloatingActionButtonClick: ((Action) -> Unit)? = null

    private val binding: BottomAppBarBinding =
        BottomAppBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.fabMain.setOnClickListener {
            // track only for open time
            if(binding.fabAddListing.visibility == View.GONE){
                NavigationBarTabTracker.trackNavigationBarTabClicked(
                    context,
                    MainBottomNavigationTab.X_TAB
                )
            }
            showSubFloatingActionButtons(binding.fabAddListing.visibility != View.VISIBLE)
        }
        binding.layoutFabGroupOverlay.setOnClickListener {
            showSubFloatingActionButtons(false)
        }
        binding.fabHomeReport.setOnClickListener {
            onFloatingActionButtonClick?.invoke(Action.REPORT_AND_RESEARCH)
            showSubFloatingActionButtons(false)
        }
        binding.fabAddListing.setOnClickListener {
            onFloatingActionButtonClick?.invoke(Action.ADD_LISTING)
            showSubFloatingActionButtons(false)
        }
        binding.fabSearch.setOnClickListener {
            onFloatingActionButtonClick?.invoke(Action.SEARCH)
            showSubFloatingActionButtons(false)
        }
    }

    fun showSubFloatingActionButtons(isShow: Boolean) {
        if (isShow) {
            animateShowSubFloatingActionButton(binding.fabHomeReport, TRANSLATION_DELTA)
            animateShowSubFloatingActionButton(binding.fabAddListing, 0f)
            animateShowSubFloatingActionButton(binding.fabSearch, -TRANSLATION_DELTA)
            ViewUtil.fadeInView(binding.layoutFabGroupOverlay)
        } else {
            animateHideSubFloatingActionButton(binding.fabHomeReport, TRANSLATION_DELTA)
            animateHideSubFloatingActionButton(binding.fabAddListing, 0f)
            animateHideSubFloatingActionButton(binding.fabSearch, -TRANSLATION_DELTA)
            ViewUtil.fadeOutView(binding.layoutFabGroupOverlay)
        }
    }

    private fun animateShowSubFloatingActionButton(
        button: SubFloatingActionButton,
        fromXDelta: Float
    ) {
        val animation = AnimationSet(true)
        val translate = TranslateAnimation(fromXDelta, 0f, TRANSLATION_DELTA, 0f)
        val alpha = AlphaAnimation(0f, 1f)
        animation.addAnimation(translate)
        animation.addAnimation(alpha)
        animation.duration = ANIMATION_DURATION
        animation.fillAfter = false
        button.startAnimation(animation)
        button.visibility = View.VISIBLE
    }

    private fun animateHideSubFloatingActionButton(
        button: SubFloatingActionButton,
        toXDelta: Float
    ) {
        val animation = AnimationSet(true)
        val translate = TranslateAnimation(0f, toXDelta, 0f, TRANSLATION_DELTA)
        val alpha = AlphaAnimation(1f, 0f)
        animation.addAnimation(translate)
        animation.addAnimation(alpha)
        animation.duration = ANIMATION_DURATION
        animation.fillAfter = false
        button.startAnimation(animation)
        button.visibility = View.GONE
    }

    override fun setupWithViewPager(viewPager: ViewPager) {
        setupWithViewPager(
            viewPager,
            arrayOf(
                binding.ibMenuDashboard,
                binding.ibMenuChat,
                binding.ibMenuAgentCv,
                binding.ibMenuMore,
            )
        ) { tab, isSelected ->
            tab.setColorFilter(
                tab.context.getColor(
                    if (isSelected) {
                        R.color.cyan
                    } else {
                        R.color.black_invertible
                    }
                )
            )
        }
    }

    fun isFloatActionActive(): Boolean {
        return binding.layoutFabGroupOverlay.visibility == View.VISIBLE
    }

    companion object {
        const val ANIMATION_DURATION: Long = 100
        const val TRANSLATION_DELTA: Float = 10f
    }

    enum class Action(val position: Int, val key: String) {
        REPORT_AND_RESEARCH(0, "report and research"),
        ADD_LISTING(1, "add listings"),
        SEARCH(2, "search")
    }
}