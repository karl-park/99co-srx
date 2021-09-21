package sg.searchhouse.agentconnect.view.activity.watchlist

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_listings_watchlist.*
import kotlinx.android.synthetic.main.layout_watchlist_listings_criteria_all.*
import kotlinx.android.synthetic.main.list_item_watchlist_main_listing_criteria_expanded.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityListingsWatchlistBinding
import sg.searchhouse.agentconnect.dsl.*
import sg.searchhouse.agentconnect.dsl.widget.listenScrollToBottom
import sg.searchhouse.agentconnect.dsl.widget.scrollToBottom
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.model.app.WatchlistHeader
import sg.searchhouse.agentconnect.view.activity.base.PaginatedViewModelActivity
import sg.searchhouse.agentconnect.view.activity.listing.ListingDetailsActivity
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.adapter.watchlist.WatchlistMainListingAdapter
import sg.searchhouse.agentconnect.view.adapter.watchlist.WatchlistMainListingCriteriaAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.watchlist.ListingsWatchlistViewModel

class ListingsWatchlistActivity :
    PaginatedViewModelActivity<ListingsWatchlistViewModel, ActivityListingsWatchlistBinding>() {
    private val adapter = WatchlistMainListingCriteriaAdapter {
        val numRecentItems = it.numRecentItems ?: 0
        if (numRecentItems > 0) {
            viewModel.selectedCriteria.postValue(it)
        }
    }

    private val listingAdapter = WatchlistMainListingAdapter {
        ListingDetailsActivity.launch(this, it.id, it.listingType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupViews()
        observeLiveData()
    }

    override fun onBackPressed() {
        if (viewModel.selectedCriteria.value != null) {
            viewModel.selectedCriteria.postValue(null)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupExtras() {
        val criteria = intent.getSerializableExtra(EXTRA_CRITERIA) as WatchlistPropertyCriteriaPO?
        viewModel.selectedCriteria.postValue(criteria)
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observeNotNull(this) {
            viewModel.watchlistHeader.postValue(WatchlistHeader(it.total, 0))
        }
        viewModel.selectedCriteria.observeNotNull(this) {
            viewModel.listingsPage.postValue(1)
        }
        viewModel.listingsPage.observeNotNull(this) {
            listingAdapter.addListItem(Loading())
            list_listing.scrollToBottom()
            viewModel.performGetListings()
        }
        viewModel.listings.observeNotNull(this) {
            listingAdapter.updateListItems(it)
        }
    }

    private fun setupViews() {
        setupListingList()
        setOnClickListeners()
    }

    private fun setupListingList() {
        list_listing.setupLayoutManager()
        list_listing.adapter = listingAdapter
        list_listing.listenScrollToBottom { viewModel.maybeAddListingPage() }
    }

    private fun setOnClickListeners() {
        layout_criteria_header.setOnClickQuickDelayListener {
            viewModel.selectedCriteria.postValue(null)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_listings_watchlist
    }

    override fun getViewModelClass(): Class<ListingsWatchlistViewModel> {
        return ListingsWatchlistViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun getList(): RecyclerView {
        return list
    }

    override fun getAdapter(): BaseListAdapter {
        return adapter
    }

    override fun isPreloadList(): Boolean {
        return true
    }

    companion object {
        private const val EXTRA_CRITERIA = "EXTRA_CRITERIA"

        fun launchByCriteria(activity: Activity, criteria: WatchlistPropertyCriteriaPO) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_CRITERIA, criteria)
            activity.launchActivity(ListingsWatchlistActivity::class.java, extras)
        }
    }
}