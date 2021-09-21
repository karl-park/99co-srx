package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_fragment_market_watch_graph.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentMarketWatchGraphBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.PropertyIndexEnum
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchIndicesResponse
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.view.helper.marketwatch.MarketWatchLineGraphWrapper
import sg.searchhouse.agentconnect.view.helper.marketwatch.MarketWatchBarGraphWrapper
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.MarketWatchGraphViewModel

class MarketWatchGraphDialogFragment :
    ViewModelDialogFragment<MarketWatchGraphViewModel, DialogFragmentMarketWatchGraphBinding>() {
    companion object {
        private const val ARGUMENT_PROPERTY_TYPE = "ARGUMENT_PROPERTY_TYPE"
        private const val ARGUMENT_TRANSACTION_TYPE = "ARGUMENT_TRANSACTION_TYPE"
        private const val ARGUMENT_SRX_INDEX_PO = "ARGUMENT_SRX_INDEX_PO"

        fun newInstance(
            propertyType: PropertyIndexEnum.PropertyType,
            transactionType: PropertyIndexEnum.TransactionType,
            srxIndexPO: LoadMarketWatchIndicesResponse.SrxIndexPO
        ): MarketWatchGraphDialogFragment {
            val arguments = Bundle()
            arguments.putSerializable(ARGUMENT_PROPERTY_TYPE, propertyType)
            arguments.putSerializable(ARGUMENT_TRANSACTION_TYPE, transactionType)
            arguments.putSerializable(ARGUMENT_SRX_INDEX_PO, srxIndexPO)
            val fragment = MarketWatchGraphDialogFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow(backgroundColor = R.color.transparent)
    }

    private lateinit var propertyType: PropertyIndexEnum.PropertyType
    private lateinit var transactionType: PropertyIndexEnum.TransactionType
    private lateinit var srxIndexPO: LoadMarketWatchIndicesResponse.SrxIndexPO

    private var priceGraphWrapper: MarketWatchLineGraphWrapper? = null
    private var volumeGraphWrapper: MarketWatchBarGraphWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArguments()
    }

    private fun setupArguments() {
        arguments?.run {
            propertyType =
                getSerializable(ARGUMENT_PROPERTY_TYPE) as PropertyIndexEnum.PropertyType?
                    ?: throw IllegalArgumentException("Missing property type")
            transactionType =
                getSerializable(ARGUMENT_TRANSACTION_TYPE) as PropertyIndexEnum.TransactionType?
                    ?: throw IllegalArgumentException("Missing transaction type")
            srxIndexPO =
                getSerializable(ARGUMENT_SRX_INDEX_PO) as LoadMarketWatchIndicesResponse.SrxIndexPO?
                    ?: throw IllegalArgumentException("Missing SRX index PO")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setupView()
        setupViewModelParams()
        viewModel.performRequest(propertyType, transactionType)
    }

    private fun setupView() {
        layout_container.setupLayoutAnimation()
        setOnClickListeners()
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(viewLifecycleOwner) { response ->
            refreshGraph()
        }
        viewModel.selectedIndicator.observeNotNull(viewLifecycleOwner) { indicator ->
            btn_price_graph.invalidate()
            btn_volume_graph.invalidate()
            refreshGraph()
        }
        viewModel.selectedTimeScale.observeNotNull(viewLifecycleOwner) { timeScale ->
            refreshGraph()
        }
    }

    private fun refreshGraph() {
        when (viewModel.selectedIndicator.value) {
            PropertyIndexEnum.Indicator.PRICE -> updatePriceGraph()
            PropertyIndexEnum.Indicator.VOLUME -> updateVolumeGraph()
        }
    }

    private fun updatePriceGraph() {
        val thisContext = context ?: return
        val response = viewModel.mainResponse.value ?: return
        val timeScale = viewModel.selectedTimeScale.value
            ?: throw IllegalArgumentException("Missing time scale")
        val graphData = response.priceGraphData
        if (priceGraphWrapper == null) {
            priceGraphWrapper =
                MarketWatchLineGraphWrapper(thisContext, chart_market_watch_price, graphData)
        }
        priceGraphWrapper?.updateGraph(PropertyIndexEnum.Indicator.PRICE, timeScale)
    }

    private fun updateVolumeGraph() {
        val thisContext = context ?: return
        val response = viewModel.mainResponse.value ?: return
        val timeScale = viewModel.selectedTimeScale.value
            ?: throw IllegalArgumentException("Missing time scale")
        val graphData = response.priceGraphData
        if (volumeGraphWrapper == null) {
            volumeGraphWrapper =
                MarketWatchBarGraphWrapper(thisContext, chart_market_watch_volume, graphData)
        }
        volumeGraphWrapper?.updateGraph(PropertyIndexEnum.Indicator.VOLUME, timeScale)
    }

    private fun setupViewModelParams() {
        viewModel.title.postValue(getTitle())
        viewModel.srxIndexPO.postValue(srxIndexPO)
    }

    private fun getTitle(): String {
        return context?.resources?.run {
            when {
                propertyType == PropertyIndexEnum.PropertyType.NLP && transactionType == PropertyIndexEnum.TransactionType.SALE -> {
                    getString(
                        R.string.title_market_graph_condo_resale,
                        srxIndexPO.getFormattedDate(),
                        srxIndexPO.getFormattedDatePreviousMonth()
                    )
                }
                propertyType == PropertyIndexEnum.PropertyType.NLP && transactionType == PropertyIndexEnum.TransactionType.RENTAL -> {
                    getString(
                        R.string.title_market_graph_condo_rental,
                        srxIndexPO.getFormattedDate(),
                        srxIndexPO.getFormattedDatePreviousMonth()
                    )
                }
                propertyType == PropertyIndexEnum.PropertyType.HDB && transactionType == PropertyIndexEnum.TransactionType.SALE -> {
                    getString(
                        R.string.title_market_graph_hdb_resale,
                        srxIndexPO.getFormattedDate(),
                        srxIndexPO.getFormattedDatePreviousMonth()
                    )
                }
                propertyType == PropertyIndexEnum.PropertyType.HDB && transactionType == PropertyIndexEnum.TransactionType.RENTAL -> {
                    getString(
                        R.string.title_market_graph_hdb_rental,
                        srxIndexPO.getFormattedDate(),
                        srxIndexPO.getFormattedDatePreviousMonth()
                    )
                }
                else -> throw IllegalArgumentException("Invalid property type and/or transaction type")
            }
        } ?: run { "" }
    }

    private fun setOnClickListeners() {
        view_blank.setOnClickListener { dismiss() }
        btn_dismiss.setOnClickListener { dismiss() }
        layout_header.setOnClickListener {
            when (view_blank.visibility) {
                View.VISIBLE -> view_blank.visibility = View.GONE
                else -> dismiss()
            }
        }
        btn_price_graph.setOnClickListener {
            viewModel.toggleIndicator(PropertyIndexEnum.Indicator.PRICE)
        }
        btn_volume_graph.setOnClickListener {
            viewModel.toggleIndicator(PropertyIndexEnum.Indicator.VOLUME)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_market_watch_graph
    }

    override fun getViewModelClass(): Class<MarketWatchGraphViewModel> {
        return MarketWatchGraphViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}