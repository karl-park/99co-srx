package sg.searchhouse.agentconnect.view.adapter.search

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertyPurpose
import sg.searchhouse.agentconnect.view.fragment.search.SearchListingsFragment
import sg.searchhouse.agentconnect.view.fragment.search.SearchTransactionsFragment
import sg.searchhouse.agentconnect.view.fragment.search.SearchXValueFragment

/**
 * Fragment pager adapter of SearchListingDialogFragment
 */
class SearchPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private lateinit var tabTitles: Array<Int>

    private var propertyPurpose: PropertyPurpose = PropertyPurpose.RESIDENTIAL

    init {
        setTabTitles()
    }

    fun setPropertyPurpose(propertyPurpose: PropertyPurpose) {
        this.propertyPurpose = propertyPurpose
        setTabTitles()
        notifyDataSetChanged()
    }

    private fun setTabTitles() {
        tabTitles = when (propertyPurpose) {
            PropertyPurpose.RESIDENTIAL -> TAB_TITLES_RESIDENTIAL
            PropertyPurpose.COMMERCIAL -> TAB_TITLES_COMMERCIAL
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SearchListingsFragment.newInstance(propertyPurpose)
            1 -> SearchTransactionsFragment.newInstance(propertyPurpose)
            2 -> SearchXValueFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid fragment pager position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabTitles[position])
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    companion object {
        private val TAB_TITLES_RESIDENTIAL = arrayOf(
            R.string.search_tab_label_listings,
            R.string.search_tab_label_transactions,
            R.string.search_tab_label_x_value
        )

        private val TAB_TITLES_COMMERCIAL = arrayOf(
            R.string.search_tab_label_listings,
            R.string.search_tab_label_transactions
        )
    }
}