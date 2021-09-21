package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.event.transaction.UpdateProjectTransactionsEvent
import sg.searchhouse.agentconnect.view.fragment.transaction.project.ProjectTransactionTowerFragment
import sg.searchhouse.agentconnect.view.fragment.transaction.project.ProjectTransactionsFragment

class ProjectSaleTransactionPagerAdapter(
    val context: Context,
    val projectId: Int,
    fm: FragmentManager,
    private val isShowTowerView: Boolean,
    val propertySubType: ListingEnum.PropertySubType
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ProjectTransactionsFragment.newInstance(
                projectId,
                UpdateProjectTransactionsEvent.ProjectTransactionType.SALE,
                propertySubType
            )
            1 -> ProjectTransactionTowerFragment.newInstance(projectId)
            else -> throw IllegalArgumentException("Invalid fragment pager position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.tab_title_transactions_table)
            1 -> context.getString(R.string.tab_title_transactions_tower)
            else -> super.getPageTitle(position)
        }
    }

    override fun getCount(): Int {
        return when (isShowTowerView) {
            true -> 2
            false -> 1
        }
    }
}