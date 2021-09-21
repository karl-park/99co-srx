package sg.searchhouse.agentconnect.view.activity.xvalue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_x_value.*
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import kotlinx.android.synthetic.main.layout_x_value_asking_price_calculator.*
import kotlinx.android.synthetic.main.layout_x_value_comparables.*
import kotlinx.android.synthetic.main.layout_x_value_latest_transactions.*
import kotlinx.android.synthetic.main.layout_x_value_x_trend.*
import kotlinx.android.synthetic.main.layout_x_value_x_trend_latest_transactions.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityXValueBinding
import sg.searchhouse.agentconnect.dsl.getOrNull
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.xvalue.GetValuationDetailResponse
import sg.searchhouse.agentconnect.model.api.xvalue.XTrendKeyValue
import sg.searchhouse.agentconnect.model.api.xvalue.XValue
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.agent.cv.AgentCvActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.xvalue.XValueComparableAdapter
import sg.searchhouse.agentconnect.view.adapter.xvalue.XValueLatestTransactionAdapter
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry
import sg.searchhouse.agentconnect.view.helper.xvalue.XTrendGraphWrapper
import sg.searchhouse.agentconnect.viewmodel.activity.xvalue.XValueViewModel

class XValueActivity : ViewModelActivity<XValueViewModel, ActivityXValueBinding>() {
    private val latestTransactionAdapter = XValueLatestTransactionAdapter()
    private val comparableAdapter = XValueComparableAdapter()
    private var xTrendGraphWrapper: XTrendGraphWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setupParamsFromExtras()
        observeLiveData()
        setOnClickListeners()
    }

    private fun observeLiveData() {
        // New x-value
        viewModel.entryParams.observe(this) {
            viewModel.performGetProject()
        }
        viewModel.getProjectResponse.observe(this) {
            viewModel.performCalculate()
        }
        viewModel.calculateResponse.observeNotNull(this) {
            viewModel.mainXValue.postValue(it.xvalue)
        }
        viewModel.calculateStatus.observeNotNull(this) {
            if (it == ApiStatus.StatusKey.SUCCESS) {
                appBarLayout.setExpanded(true)
            }
        }

        // Existing and new x-value
        viewModel.mainXValue.observeNotNull(this) {
            viewModel.title.postValue(it.fullAddress)

            // Asking price calculator
            viewModel.goodWill.postValue(it.goodWillPercent)
            viewModel.xValue.postValue(it.xValue)
            viewModel.xValuePlus.postValue(it.xValuePlus)
            viewModel.xListingPrice.postValue(it.listingPrice)
            viewModel.renovationYear.postValue(it.renovationYear?.let { year ->
                when {
                    year > 0 -> year
                    else -> null
                }
            })
            viewModel.renovationCost.postValue(it.renovationCost)
            viewModel.performLoadHomeReportTransaction(it.projectId)

            val requestId = when {
                it.requestId > 0 -> it.requestId
                else -> it.rentalId
            }
            performGetValuationDetails(requestId)
            viewModel.performGetXValueTrend()

            if (it.getOwnershipType() == XValueEnum.OwnershipType.RENT) {
                viewModel.ownershipType.postValue(ListingEnum.OwnershipType.RENT)
            }
        }

        viewModel.goodWill.observe(this) {
            if (it != viewModel.mainXValue.value?.goodWillPercent) {
                viewModel.performUpdateRequest()
            }
        }

        viewModel.renovationYear.observe(this) {
            if (it != viewModel.mainXValue.value?.renovationYear) {
                viewModel.performUpdateRequest()
            }
        }

        viewModel.renovationCost.observe(this) {
            if (it != viewModel.mainXValue.value?.renovationCost) {
                viewModel.performUpdateRequest()
            }
        }

        viewModel.updateRequestResponse.observeNotNull(this) {
            viewModel.xValue.postValue(it.result.xValue)
            viewModel.xValuePlus.postValue(it.result.xValuePlus)
            viewModel.xListingPrice.postValue(it.result.listingPrice)
        }

        viewModel.loadHomeReportSaleTransactionRevisedResponse.observe(this) {
            latestTransactionAdapter.listItems = it?.getTransactionList() ?: emptyList()
            latestTransactionAdapter.notifyDataSetChanged()
        }

        viewModel.loadHomeReportRentalTransactionRevisedResponse.observe(this) {
            latestTransactionAdapter.listItems = it?.getTransactionList() ?: emptyList()
            latestTransactionAdapter.notifyDataSetChanged()
        }

        viewModel.ownershipType.observe(this) {
            when (it) {
                ListingEnum.OwnershipType.SALE -> {
                    if (viewModel.loadHomeReportSaleTransactionRevisedResponse.value != null) {
                        latestTransactionAdapter.listItems =
                            viewModel.loadHomeReportSaleTransactionRevisedResponse.value?.getTransactionList()
                                ?: emptyList()
                        latestTransactionAdapter.notifyDataSetChanged()
                    } else {
                        reloadLatestTransactions()
                    }
                }
                ListingEnum.OwnershipType.RENT -> {
                    if (viewModel.loadHomeReportRentalTransactionRevisedResponse.value != null) {
                        latestTransactionAdapter.listItems =
                            viewModel.loadHomeReportRentalTransactionRevisedResponse.value?.getTransactionList()
                                ?: emptyList()
                        latestTransactionAdapter.notifyDataSetChanged()
                    } else {
                        reloadLatestTransactions()
                    }
                }
                else -> {
                }
            }
        }

        viewModel.getValuationDetailResponse.observeNotNull(this) {
            populateComparables(it.data.comparables)
        }

        viewModel.xTrendList.observe(this) {
            plotGraph(it ?: emptyList())
        }

        viewModel.selectedTimeScale.observeNotNull(this) {
            xTrendGraphWrapper?.updateTimeScale(it)
        }
    }

    private fun populateComparables(comparables: List<GetValuationDetailResponse.Data.Comparable>) {
        calculateAverageComparablePsf(comparables)
        comparableAdapter.listItems = comparables
        comparableAdapter.notifyDataSetChanged()
    }

    private fun performGetValuationDetails(srxValuationRequestId: Int) {
        if (srxValuationRequestId > 0) {
            viewModel.performGetValuationDetails(srxValuationRequestId)
        }
    }

    private fun calculateAverageComparablePsf(comparables: List<GetValuationDetailResponse.Data.Comparable>) {
        if (comparables.isNotEmpty()) {
            val figure =
                comparables.sumByDouble { comparable -> comparable.valuerPsf } / comparables.size
            val label = getString(R.string.label_price, NumberUtil.formatThousand(figure, 2))
            viewModel.averageComparablePsf.postValue(label)
        }
    }

    private fun reloadLatestTransactions() {
        latestTransactionAdapter.listItems = emptyList()
        latestTransactionAdapter.notifyDataSetChanged()
        viewModel.mainXValue.value?.projectId?.let { viewModel.performLoadHomeReportTransaction(it) }
    }

    private fun setupParamsFromExtras() {
        val entryParams = intent.getSerializableExtra(EXTRA_ENTRY_PARAMS) as XValueEntryParams?
        if (entryParams != null) {
            return viewModel.entryParams.postValue(entryParams)
        }

        val existingResult =
            intent.getSerializableExtra(EXTRA_EXISTING_RESULT) as XValue?
        if (existingResult != null) {
            return viewModel.mainXValue.postValue(existingResult)
        }

        throw IllegalArgumentException("Missing entryParam / existing X-value result!")
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupOwnershipToggle()
        setupAskingPriceCalculator()
        setupComparablesList()
        setupLatestTransactionsList()
    }

    private fun setupComparablesList() {
        list_comparables.layoutManager = LinearLayoutManager(this)
        list_comparables.adapter = comparableAdapter
    }

    private fun setupLatestTransactionsList() {
        list_latest_transactions.layoutManager = LinearLayoutManager(this)
        list_latest_transactions.adapter = latestTransactionAdapter
    }

    private fun setupAskingPriceCalculator() {
        et_renovation_cost.setup(numberTextTransformer = { number: Int?, formattedNumberString: String? ->
            when (number) {
                null -> ""
                0 -> "\$"
                else -> "\$$formattedNumberString"
            }
        }, onNumberChangeListener = {
            viewModel.renovationCost.postValue(it)
        })
        et_renovation_cost.setNumber(0)
        et_renovation_cost.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                ViewUtil.hideKeyboard(et_renovation_cost.et_display)
                true
            } else {
                false
            }
        }
    }

    private fun setupOwnershipToggle() {
        // TODO Use consistent ownership type
        radio_group_ownership_type.setValue(viewModel.ownershipType.value!!) // Guarantee not null
        radio_group_ownership_type.onSelectOwnershipListener = {
            viewModel.ownershipType.postValue(it)
        }
    }

    private fun plotGraph(xTrendKeyValues: List<XTrendKeyValue>) {
        xTrendGraphWrapper = XTrendGraphWrapper(this, chart_x_trend, xTrendKeyValues)
        viewModel.selectedTimeScale.value?.let { xTrendGraphWrapper?.updateTimeScale(it) }
    }

    private fun setOnClickListeners() {
        layout_comparables_header.setOnClickListener {
            viewModel.isExpandComparables.postValue(
                viewModel.isExpandComparables.value != true
            )
        }

        layout_x_trend_header.setOnClickListener {
            viewModel.isExpandXTrend.postValue(
                viewModel.isExpandXTrend.value != true
            )
        }

        layout_asking_price_calculator_header.setOnClickListener {
            viewModel.isExpandAskingPriceCalculator.postValue(
                viewModel.isExpandAskingPriceCalculator.value != true
            )
        }

        select_renovation_year.setOnClickListener {

            val currentYear = DateTimeUtil.getCurrentYear()
            val yearBegin = currentYear - 10

            val years =
                listOf(getString(R.string.selection_no_renovation)) + (yearBegin..currentYear).map {
                    NumberUtil.toString(it)
                }

            dialogUtil.showStringListDialog(years, { _, position ->
                when (position) {
                    0 -> viewModel.renovationYear.postValue(null)
                    else -> viewModel.renovationYear.postValue(NumberUtil.toInt(years[position]))
                }
            })
        }

        btn_add_goodwill.setOnClickListener {
            val existingGoodWill = viewModel.goodWill.value ?: 0
            if (existingGoodWill < MAX_LIMIT_GOOD_WILL) {
                val newGoodWill = existingGoodWill + 1
                viewModel.goodWill.postValue(newGoodWill)
            }
        }

        btn_minus_goodwill.setOnClickListener {
            val existingGoodWill = viewModel.goodWill.value ?: 0
            if (existingGoodWill > -MAX_LIMIT_GOOD_WILL) {
                val newGoodWill = existingGoodWill - 1
                viewModel.goodWill.postValue(newGoodWill)
            }
        }

        btn_download_report.setOnClickListener {
            downloadXValueReport()
        }

        btn_view_latest_transactions.setOnClickListener {
            if (viewModel.isExpandXTrend.value != true) {
                viewModel.isExpandXTrend.postValue(true)
            }
        }

        btn_view_nearby_listings.setOnClickListener {
            val postalCode = viewModel.getPostalCode() ?: return@setOnClickListener
            val title =
                viewModel.mainXValue.value?.getBlockAndProjectName()?.getOrNull()
            SearchResultActivityEntry.launchListingsBySearchText(
                this,
                "$postalCode",
                ListingEnum.PropertyPurpose.RESIDENTIAL,
                ListingEnum.OwnershipType.SALE,
                isFinishOrigin = false,
                optionalTitle = title
            )
        }
    }

    private fun downloadXValueReport() {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.XVALUE_REPORT,
            onSuccessAccessibility = {
                viewModel.performGenerateXValuePropertyReport()
            }
        )
    }

    companion object {
        private const val EXTRA_ENTRY_PARAMS = "EXTRA_ENTRY_PARAMS"
        private const val EXTRA_EXISTING_RESULT = "EXTRA_EXISTING_RESULT"
        private const val MAX_LIMIT_GOOD_WILL = 100

        fun launch(activity: Activity, xValueEntryParams: XValueEntryParams) {
            val intent = Intent(activity, XValueActivity::class.java)
            intent.putExtra(EXTRA_ENTRY_PARAMS, xValueEntryParams)
            activity.startActivity(intent)
        }

        fun launch(activity: Activity, existingResult: XValue) {
            val intent = Intent(activity, XValueActivity::class.java)
            intent.putExtra(EXTRA_EXISTING_RESULT, existingResult)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_x_value
    }

    override fun getViewModelClass(): Class<XValueViewModel> {
        return XValueViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}
