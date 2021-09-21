package sg.searchhouse.agentconnect.view.widget.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import sg.searchhouse.agentconnect.databinding.TabLayoutTransactionListBinding
import sg.searchhouse.agentconnect.view.widget.common.BaseTabLayout

class TransactionListTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabLayout<Button>(context, attrs, defStyleAttr) {
    private val binding =
        TabLayoutTransactionListBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupWithViewPager(viewPager: ViewPager, onTabClicked: (() -> Unit)?) {
        val buttons: Array<Button> = binding.run { arrayOf(btnTabFirst, btnTabSecond) }

        // Set tab title
        buttons.mapIndexed { index, button -> button.text = viewPager.adapter?.getPageTitle(index) }

        // Set tab highlight view
        setupWithViewPager(viewPager, buttons) { tab, isSelected ->
            tab.isSelected = isSelected
            onTabClicked?.invoke()
        }
    }

    override fun setupWithViewPager(viewPager: ViewPager) {
        setupWithViewPager(viewPager, null)
    }
}