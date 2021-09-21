package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_dashboard_market_data.layout_container
import kotlinx.android.synthetic.main.fragment_dashboard_watchlist_transactions.*
import kotlinx.android.synthetic.main.layout_dashboard_watchlist_listings_empty.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_SHOULD_REFRESH_DASHBOARD_LISTINGS_WATCHLIST
import sg.searchhouse.agentconnect.databinding.FragmentDashboardWatchlistListingsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.view.activity.agent.profile.AgentProfileActivity
import sg.searchhouse.agentconnect.view.activity.listing.ListingDetailsActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.AddWatchlistCriteriaActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.ListingsWatchlistActivity
import sg.searchhouse.agentconnect.view.adapter.watchlist.DashboardWatchlistListingCriteriaAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardWatchlistListingsViewModel

@Suppress("unused")
class DashboardWatchlistListingsFragment :
    ViewModelFragment<DashboardWatchlistListingsViewModel, FragmentDashboardWatchlistListingsBinding>() {

    private val adapter = DashboardWatchlistListingCriteriaAdapter(onExpand = {
        viewModel.performGetWatchlistDetails(it)
    }, onClickViewAll = {
        activity?.run { ListingsWatchlistActivity.launchByCriteria(this, it) }
    }, onClickListItem = {
        activity?.run {
            ListingDetailsActivity.launch(this, it.id, it.listingType)
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        performRequest()
        setOnClickListeners()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observeNotNull(viewLifecycleOwner) {
            adapter.watchlists = it.watchlists.map { watchlist ->
                watchlist.type = WatchlistEnum.WatchlistType.LISTINGS.value
                watchlist
            }
            adapter.notifyDataSetChanged()
        }
        viewModel.listingPairs.observeNotNull(viewLifecycleOwner) {
            adapter.listingsMap = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupList()
    }

    private fun setupList() {
        list.setupLayoutManager(LinearLayout.VERTICAL)
        list.adapter = adapter
    }

    private fun performRequest() {
        viewModel.performRequest()
    }

    private fun setOnClickListeners() {
        btn_view_all.setOnClickListener {
            launchActivity(ListingsWatchlistActivity::class.java)
        }
        btn_add_criteria.setOnClickListener {
            activity?.run {
                AddWatchlistCriteriaActivity.launch(this, WatchlistEnum.WatchlistType.LISTINGS)
            }
        }
        btn_manage_watchlists.setOnClickListener {
            activity?.run {
                AgentProfileActivity.launch(this, AgentProfileActivity.Companion.LaunchType.MANAGE_WATCHLIST)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.getBoolean(PREF_SHOULD_REFRESH_DASHBOARD_LISTINGS_WATCHLIST, false)) {
            Prefs.putBoolean(PREF_SHOULD_REFRESH_DASHBOARD_LISTINGS_WATCHLIST, false)
            viewModel.listingPairs.postValue(null)
            viewModel.performRequest()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_watchlist_listings
    }

    override fun getViewModelClass(): Class<DashboardWatchlistListingsViewModel> {
        return DashboardWatchlistListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}