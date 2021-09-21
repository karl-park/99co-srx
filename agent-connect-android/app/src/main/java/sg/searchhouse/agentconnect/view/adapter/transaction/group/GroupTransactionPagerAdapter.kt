package sg.searchhouse.agentconnect.view.adapter.transaction.group

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.view.fragment.transaction.group.GroupTransactionProjectsFragment
import sg.searchhouse.agentconnect.view.fragment.transaction.group.GroupTransactionsFragment

class GroupTransactionPagerAdapter(val context: Context, fm: FragmentManager, private val ownershipType: ListingEnum.OwnershipType, private val isShowProjects: Boolean) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (isShowProjects) {
            true -> {
                when (position) {
                    0 -> GroupTransactionProjectsFragment.newInstance()
                    1 -> GroupTransactionsFragment.newInstance(ownershipType)
                    else -> throw IllegalArgumentException("Invalid fragment pager position")
                }
            }
            false -> {
                when (position) {
                    0 -> GroupTransactionsFragment.newInstance(ownershipType)
                    else -> throw IllegalArgumentException("Invalid fragment pager position")
                }
            }
        }


    }

    override fun getCount(): Int {
        return when (isShowProjects) {
            true -> 2
            false -> 1
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (isShowProjects) {
            true -> {
                when (position) {
                    0 -> context.getString(R.string.tab_title_transactions_projects)
                    1 -> context.getString(R.string.tab_title_transactions_transactions)
                    else -> super.getPageTitle(position)
                }
            }
            false -> {
                when (position) {
                    0 -> context.getString(R.string.tab_title_transactions_transactions)
                    else -> super.getPageTitle(position)
                }
            }
        }
    }
}