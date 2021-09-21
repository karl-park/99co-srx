package sg.searchhouse.agentconnect.view.activity.watchlist

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.*
import sg.searchhouse.agentconnect.dsl.*
import sg.searchhouse.agentconnect.dsl.widget.listenScrollToBottom
import sg.searchhouse.agentconnect.dsl.widget.scrollToBottom
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.model.app.WatchlistHeader
import sg.searchhouse.agentconnect.view.activity.base.PaginatedViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.adapter.watchlist.WatchlistMainTransactionAdapter
import sg.searchhouse.agentconnect.view.adapter.watchlist.WatchlistMainTransactionCriteriaAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.watchlist.TransactionsWatchlistViewModel

class TransactionsWatchlistActivity :
    PaginatedViewModelActivity<TransactionsWatchlistViewModel, ActivityTransactionsWatchlistBinding>() {
    private val adapter = WatchlistMainTransactionCriteriaAdapter {
        val numRecentItems = it.numRecentItems ?: 0
        if (numRecentItems > 0) {
            viewModel.selectedCriteria.postValue(it)
        }
    }

    private val transactionAdapter = WatchlistMainTransactionAdapter()

    private fun getLayoutWatchlistTransactionsCriteriaAllBinding(): LayoutWatchlistTransactionsCriteriaAllBinding =
        binding.findChildLayoutBindingById(R.id.layout_watchlist_transactions_criteria_all)

    private fun getListItemWatchlistMainTransactionCriteriaExpandedBinding(): ListItemWatchlistMainTransactionCriteriaExpandedBinding =
        getLayoutWatchlistTransactionsCriteriaAllBinding().findChildLayoutBindingById(R.id.layout_header)

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
            // For title and background
            viewModel.ownershipType.postValue(it.getSaleTypeEnum())
            val propertyMainType = it.getPropertyMainType()
                ?: ListingEnum.PropertyMainType.CONDO // TODO Confirm, might change
            viewModel.propertyMainType.postValue(propertyMainType)

            // For table
            transactionAdapter.setup(it.getSaleTypeEnum(), propertyMainType)

            viewModel.transactionsPage.postValue(1)
        }
        viewModel.transactionsPage.observeNotNull(this) {
            transactionAdapter.addListItem(Loading())
            getLayoutWatchlistTransactionsCriteriaAllBinding().listTransaction.scrollToBottom()
            viewModel.performGetTransactions()
        }
        viewModel.transactions.observeNotNull(this) {
            transactionAdapter.updateListItems(it)
        }
    }

    private fun setupViews() {
        setupTransactionList()
        setOnClickListeners()
    }

    private fun setupTransactionList() {
        getLayoutWatchlistTransactionsCriteriaAllBinding().listTransaction.setupLayoutManager()
        getLayoutWatchlistTransactionsCriteriaAllBinding().listTransaction.adapter = transactionAdapter
        binding.scrollView.listenScrollToBottom { viewModel.maybeAddTransactionPage() }
    }

    private fun setOnClickListeners() {
        getListItemWatchlistMainTransactionCriteriaExpandedBinding().layoutCriteriaHeader.setOnClickQuickDelayListener {
            viewModel.selectedCriteria.postValue(null)
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_transactions_watchlist

    override fun getViewModelClass(): Class<TransactionsWatchlistViewModel> =
        TransactionsWatchlistViewModel::class.java

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar = binding.toolbar

    override fun getList(): RecyclerView = binding.list

    override fun getAdapter(): BaseListAdapter = adapter

    override fun isPreloadList(): Boolean = true

    companion object {
        private const val EXTRA_CRITERIA = "EXTRA_CRITERIA"

        fun launchByCriteria(activity: Activity, criteria: WatchlistPropertyCriteriaPO) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_CRITERIA, criteria)
            activity.launchActivity(TransactionsWatchlistActivity::class.java, extras)
        }
    }
}