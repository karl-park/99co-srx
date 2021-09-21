package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_dashboard_watchlist_transactions.*
import kotlinx.android.synthetic.main.layout_dashboard_watchlist_transactions_empty.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_SHOULD_REFRESH_DASHBOARD_TRANSACTIONS_WATCHLIST
import sg.searchhouse.agentconnect.databinding.FragmentDashboardWatchlistTransactionsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.view.activity.agent.profile.AgentProfileActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.AddWatchlistCriteriaActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.TransactionsWatchlistActivity
import sg.searchhouse.agentconnect.view.adapter.watchlist.DashboardWatchlistTransactionCriteriaAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardWatchlistTransactionsViewModel

@Suppress("unused")
class DashboardWatchlistTransactionsFragment :
    ViewModelFragment<DashboardWatchlistTransactionsViewModel, FragmentDashboardWatchlistTransactionsBinding>() {

    private val adapter = DashboardWatchlistTransactionCriteriaAdapter(onExpand = {
        viewModel.performGetWatchlistDetails(it)
    }, onClickViewAll = {
        activity?.run { TransactionsWatchlistActivity.launchByCriteria(this, it) }
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
            adapter.watchLists = it.watchlists.map { watchlist ->
                watchlist.type = WatchlistEnum.WatchlistType.TRANSACTIONS.value
                watchlist
            }
            adapter.notifyDataSetChanged()
        }
        viewModel.transactionPairs.observeNotNull(viewLifecycleOwner) {
            adapter.transactionsMap = it
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
            launchActivity(TransactionsWatchlistActivity::class.java)
        }
        btn_add_criteria.setOnClickListener {
            activity?.run {
                AddWatchlistCriteriaActivity.launch(this, WatchlistEnum.WatchlistType.TRANSACTIONS)
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
        if (Prefs.getBoolean(PREF_SHOULD_REFRESH_DASHBOARD_TRANSACTIONS_WATCHLIST, false)) {
            Prefs.putBoolean(PREF_SHOULD_REFRESH_DASHBOARD_TRANSACTIONS_WATCHLIST, false)
            viewModel.transactionPairs.postValue(null)
            viewModel.performRequest()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_watchlist_transactions
    }

    override fun getViewModelClass(): Class<DashboardWatchlistTransactionsViewModel> {
        return DashboardWatchlistTransactionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}