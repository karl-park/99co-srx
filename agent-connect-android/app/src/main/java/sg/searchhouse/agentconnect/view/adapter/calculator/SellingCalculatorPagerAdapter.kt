package sg.searchhouse.agentconnect.view.adapter.calculator

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum
import sg.searchhouse.agentconnect.enumeration.app.SellingCalculatorTab
import sg.searchhouse.agentconnect.view.fragment.calculator.SellingCalculatorFragment

class SellingCalculatorPagerAdapter(
    val context: Context,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SellingCalculatorFragment.newInstance(CalculatorAppEnum.SellingCalculatorEntryType.SELLING)
            1 -> SellingCalculatorFragment.newInstance(CalculatorAppEnum.SellingCalculatorEntryType.SELLING_STAMP_DUTY)
            else -> throw IllegalArgumentException("Invalid fragment pager position")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(SellingCalculatorTab.LOOK_UP_PROPERTY.label)
            1 -> context.getString(SellingCalculatorTab.ENTER_DETAILS.label)
            else -> throw IllegalArgumentException("Invalid fragment pager position")
        }
    }
}