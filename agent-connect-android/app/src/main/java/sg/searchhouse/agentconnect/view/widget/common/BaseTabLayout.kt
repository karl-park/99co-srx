package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager

abstract class BaseTabLayout<T: View> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : CoordinatorLayout(context, attrs, defStyleAttr) {

    lateinit var mTabs: Array<T>

    // tabs: The View array of tab buttons
    // onTabSelected: Highlight tab with your custom way
    protected fun setupWithViewPager(viewPager: ViewPager, tabs: Array<T>, onTabSelected: (T, Boolean) -> Unit) {
        require(tabs.size == viewPager.adapter?.count) { "Inconsistent size between tabs array and view pager size" }
        mTabs = tabs
        selectTab(mTabs[viewPager.currentItem], onTabSelected)

        // Highlight default tab
        mTabs.mapIndexed { index, tab ->
            tab.setOnClickListener {
                viewPager.currentItem = index
                selectTab(tab, onTabSelected)
            }
        }

        // Setup text, click tab update pager and highlight
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position >= 0 && position < mTabs.size) {
                    selectTab(mTabs[position], onTabSelected)
                }
            }
        })
    }

    // Highlight tab
    fun selectTab(tab: T, onTabSelected: (T, Boolean) -> Unit) {
        mTabs.map { thisTab -> onTabSelected.invoke(thisTab, thisTab.id == tab.id) }
    }

    // Call setupWithViewPager(tabs: Array<View>, viewPager: ViewPager, onTabSelected: (View, Boolean) -> Unit)
    // from super class
    abstract fun setupWithViewPager(viewPager: ViewPager)
}