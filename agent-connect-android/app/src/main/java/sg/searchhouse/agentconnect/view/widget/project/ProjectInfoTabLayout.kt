package sg.searchhouse.agentconnect.view.widget.project

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import sg.searchhouse.agentconnect.databinding.TabLayoutProjectInfoBinding
import sg.searchhouse.agentconnect.view.widget.common.BaseTabLayout

class ProjectInfoTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabLayout<Button>(context, attrs, defStyleAttr) {
    private val binding = TabLayoutProjectInfoBinding.inflate(LayoutInflater.from(context), this, true)

    override fun setupWithViewPager(viewPager: ViewPager) {
        val buttons: Array<Button> =
            binding.run { arrayOf(btnTabProjectInfo, btnTabPlanningDecisions) }

        // Set tab highlight view
        setupWithViewPager(viewPager, buttons) { tab, isSelected -> tab.isSelected = isSelected }
    }
}