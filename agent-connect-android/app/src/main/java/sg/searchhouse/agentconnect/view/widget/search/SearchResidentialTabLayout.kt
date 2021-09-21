package sg.searchhouse.agentconnect.view.widget.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import sg.searchhouse.agentconnect.databinding.TabLayoutSearchResidentialBinding
import sg.searchhouse.agentconnect.view.widget.common.BaseTabLayout

class SearchResidentialTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabLayout<Button>(context, attrs, defStyleAttr) {
    private val binding =
        TabLayoutSearchResidentialBinding.inflate(LayoutInflater.from(context), this, true)

    override fun setupWithViewPager(viewPager: ViewPager) {
        throw IllegalArgumentException("Use `setupWithViewPager(viewPager: ViewPager, onTabSelected: (position: Int) -> Unit)`")
    }

    fun setupWithViewPager(viewPager: ViewPager, onTabSelected: (position: Int) -> Unit) {
        val buttons: Array<Button> =
            binding.run { arrayOf(btnTabListings, btnTabTransactions, btnTabXValues) }

        // Set tab title
        buttons.mapIndexed { index, button ->
            button.text = viewPager.adapter?.getPageTitle(index)
        }
        // Set tab highlight view
        setupWithViewPager(viewPager, buttons) { tab, isSelected ->
            tab.isSelected = isSelected
            if (isSelected) {
                val position = buttons.indexOfFirst { it.id == tab.id }
                onTabSelected.invoke(position)
            }
        }
    }
}