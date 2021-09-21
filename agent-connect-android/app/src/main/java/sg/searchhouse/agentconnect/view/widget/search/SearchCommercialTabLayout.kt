package sg.searchhouse.agentconnect.view.widget.search

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.tab_layout_search_commercial.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.view.widget.common.BaseTabLayout

class SearchCommercialTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabLayout<Button>(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.tab_layout_search_commercial, this)
    }

    override fun setupWithViewPager(viewPager: ViewPager) {
        val buttons: Array<Button> = arrayOf(btn_tab_listings, btn_tab_transactions)

        // Set tab title
        buttons.mapIndexed { index, button -> button.text = viewPager.adapter?.getPageTitle(index) }

        // Set tab highlight view
        setupWithViewPager(viewPager, buttons) { tab, isSelected -> tab.isSelected = isSelected }
    }
}