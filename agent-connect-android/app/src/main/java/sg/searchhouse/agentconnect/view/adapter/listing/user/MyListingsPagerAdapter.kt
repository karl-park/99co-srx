package sg.searchhouse.agentconnect.view.adapter.listing.user

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.view.fragment.listing.user.MyListingsFragment

class MyListingsPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    val tabs = listOf(
        MyListingsTab.ACTIVE,
        MyListingsTab.DRAFT,
        MyListingsTab.PAST,
        MyListingsTab.TRANSACTION
    )

    companion object {
        const val TAB_COUNT = 4
    }

    override fun getItem(position: Int): Fragment {
        require(!(position < 0 || position >= tabs.size)) { "Invalid fragment pager position" }
        return MyListingsFragment.newInstance(tabs[position]) // TODO: Handle multiple group IDs for "past" listings
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val thisTab = tabs[position]
        return context.getString(thisTab.labelEmpty)
    }

    override fun getCount(): Int {
        return TAB_COUNT
    }

    //TODO: if confirm to remove all count beside listing group, remove labelOccupied
    enum class MyListingsTab(
        val position: Int,
        val labelEmpty: Int,
        val labelOccupied: Int,
        val listingGroups: List<ListingManagementEnum.ListingGroup>
    ) {
        ACTIVE(
            0,
            R.string.tab_title_my_active_listings_fragment,
            R.string.tab_title_my_active_listings_fragment_occupied,
            listOf(ListingManagementEnum.ListingGroup.ACTIVE)
        ),
        DRAFT(
            1,
            R.string.tab_title_my_draft_listings_fragment,
            R.string.tab_title_my_draft_listings_fragment_occupied,
            listOf(ListingManagementEnum.ListingGroup.DRAFTS)
        ),
        PAST(
            2,
            R.string.tab_title_my_past_listings_fragment,
            R.string.tab_title_my_past_listings_fragment_occupied,
            listOf(
                ListingManagementEnum.ListingGroup.EXPIRED,
                ListingManagementEnum.ListingGroup.TAKEN_OFF
            )
        ),
        TRANSACTION(
            3,
            R.string.tab_title_my_transaction_listings_fragment,
            R.string.tab_title_my_transaction_listings_fragment_occupied,
            listOf(ListingManagementEnum.ListingGroup.TRANSACTION)
        )
    }
}