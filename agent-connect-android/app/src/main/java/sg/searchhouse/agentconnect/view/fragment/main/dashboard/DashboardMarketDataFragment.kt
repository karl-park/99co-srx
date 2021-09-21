package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.card_dashboard_market_watch_index_hdb.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_market_data.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardMarketDataBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.PropertyIndexEnum
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchIndicesResponse
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardMarketDataViewModel

class DashboardMarketDataFragment :
    ViewModelFragment<DashboardMarketDataViewModel, FragmentDashboardMarketDataBinding>() {

    companion object {
        fun newInstance() = DashboardMarketDataFragment()
    }

    private var marketWatchGraphDialogFragment: MarketWatchGraphDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeRxBuses()
        performRequest()
        setOnClickListeners()
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
    }

    private fun observeRxBuses() {
        listenRxBus(LoginAsAgentEvent::class.java) {
            performRequest()
        }
    }

    private fun performRequest() {
        viewModel.performRequest()
    }

    private fun setOnClickListeners() {
        setCardOnClickListeners()
        setViewGraphOnClickListeners()
    }

    // Expand collapse index layouts
    private fun setCardOnClickListeners() {
        card_dashboard_market_watch_index_condo_resale.layout_main.setOnClickListener {
            viewModel.isExpandCondoResale.postValue(viewModel.isExpandCondoResale.value != true)
        }

        card_dashboard_market_watch_index_condo_rental.layout_main.setOnClickListener {
            viewModel.isExpandCondoRent.postValue(viewModel.isExpandCondoRent.value != true)
        }

        card_dashboard_market_watch_index_hdb_resale.layout_main.setOnClickListener {
            viewModel.isExpandHdbResale.postValue(viewModel.isExpandHdbResale.value != true)
        }

        card_dashboard_market_watch_index_hdb_rental.layout_main.setOnClickListener {
            viewModel.isExpandHdbRent.postValue(viewModel.isExpandHdbRent.value != true)
        }
    }

    // View graph
    private fun setViewGraphOnClickListeners() {
        card_dashboard_market_watch_index_condo_resale.layout_view_graph.setOnClickListener {
            showGraph(
                PropertyIndexEnum.PropertyType.NLP,
                PropertyIndexEnum.TransactionType.SALE,
                viewModel.mainResponse.value?.nonLandedPrivate
            )
        }

        card_dashboard_market_watch_index_condo_rental.layout_view_graph.setOnClickListener {
            showGraph(
                PropertyIndexEnum.PropertyType.NLP, PropertyIndexEnum.TransactionType.RENTAL,
                viewModel.mainResponse.value?.nonLandedPrivateRent
            )
        }

        card_dashboard_market_watch_index_hdb_resale.layout_view_graph.setOnClickListener {
            showGraph(
                PropertyIndexEnum.PropertyType.HDB, PropertyIndexEnum.TransactionType.SALE,
                viewModel.mainResponse.value?.hdb
            )
        }

        card_dashboard_market_watch_index_hdb_rental.layout_view_graph.setOnClickListener {
            showGraph(
                PropertyIndexEnum.PropertyType.HDB, PropertyIndexEnum.TransactionType.SALE,
                viewModel.mainResponse.value?.hdbRent
            )
        }
    }

    private fun showGraph(
        propertyType: PropertyIndexEnum.PropertyType,
        transactionType: PropertyIndexEnum.TransactionType,
        srxIndexPO: LoadMarketWatchIndicesResponse.SrxIndexPO?
    ) {
        if (srxIndexPO == null) return
        if (marketWatchGraphDialogFragment?.isVisible != true) {
            marketWatchGraphDialogFragment = MarketWatchGraphDialogFragment.newInstance(
                propertyType,
                transactionType,
                srxIndexPO
            )
            marketWatchGraphDialogFragment?.show(
                childFragmentManager,
                MarketWatchGraphDialogFragment::class.java.name
            )
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_market_data
    }

    override fun getViewModelClass(): Class<DashboardMarketDataViewModel> {
        return DashboardMarketDataViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}