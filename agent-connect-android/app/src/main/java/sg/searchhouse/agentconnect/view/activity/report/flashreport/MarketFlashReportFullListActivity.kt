package sg.searchhouse.agentconnect.view.activity.report.flashreport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_market_flash_report.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityMarketFlashReportBinding
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.report.flashreport.MarketFlashReportAdapter
import sg.searchhouse.agentconnect.view.fragment.main.dashboard.MarketFlashReportDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.report.flashreport.MarketFlashReportFullListViewModel

class MarketFlashReportFullListActivity :
    ViewModelActivity<MarketFlashReportFullListViewModel, ActivityMarketFlashReportBinding>() {

    private lateinit var adapter: MarketFlashReportAdapter

    private var flashReportDetailFragment: MarketFlashReportDialogFragment? = null

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MarketFlashReportFullListActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
        performRequest()
        observeLiveData()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        adapter =
            MarketFlashReportAdapter(viewModel.reports,
                onItemClickListener = {
                    showDetailFlashReport(it)
                },
                onDownloadReport = {
                    if (!TextUtils.isEmpty(it.reportLink)) {
                        ViewUtil.showMessage(
                            getString(
                                R.string.message_download_flash_report,
                                it.title
                            )
                        )
                        viewModel.downloadReport(it)
                    }
                },
                onGenerateAnalyticsGraph = {
                    generateAnalyticsGraph(it)
                })
        binding.listFlashReports.layoutManager = LinearLayoutManager(this)
        binding.listFlashReports.adapter = adapter
    }

    private fun performRequest() {
        viewModel.performRequest()
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            val marketingFlashReports = response?.marketFlashReports ?: return@Observer
            val page = viewModel.page.value ?: 1

            viewModel.total.postValue(marketingFlashReports.total)
            viewModel.reports.removeAll(viewModel.reports.filterIsInstance<Loading>())

            if (marketingFlashReports.results.isNotEmpty()) {
                if (page == 1) {
                    viewModel.reports.clear()
                }
                viewModel.reports.addAll(marketingFlashReports.results)
            } else {
                viewModel.reports.clear()
            }
            adapter.allowGenerateAnalyticGraph = true
            adapter.notifyDataSetChanged()
        })

        viewModel.page.observe(this, Observer { page ->
            if (page != null) {
                viewModel.performRequest()
            }
        })
    }

    private fun showDetailFlashReport(report: MarketingFlashReportPO) {
        if (flashReportDetailFragment?.isVisible != true) {
            flashReportDetailFragment = MarketFlashReportDialogFragment.newInstance(report)
            flashReportDetailFragment?.show(
                supportFragmentManager,
                MarketFlashReportDialogFragment.TAG_MARKET_FLASH_REPORT_SUMMARY
            )
        }
    }

    private fun handleListeners() {
        btn_sort.setOnClickListener { showSortOptionsDialog() }
        ViewUtil.listenVerticalScrollEnd(list_flash_reports, reachBottom = {
            if (viewModel.canLoadNext() && viewModel.reports.last() !is Loading) {
                val pageIndex = viewModel.page.value ?: 1
                viewModel.reports.add(Loading())
                adapter.notifyItemChanged(viewModel.reports.size - 1)
                viewModel.page.value = pageIndex + 1
            }
        })
    }

    private fun showSortOptionsDialog() {
        val orderCriteriaGroup = ReportEnum.OrderCriteria.values()
        val orderCriteriaLabels = orderCriteriaGroup.map { it.label }
        dialogUtil.showListDialog(orderCriteriaLabels, { _, position ->
            viewModel.orderCriteria.postValue(orderCriteriaGroup[position])
            viewModel.page.postValue(1)
        }, R.string.label_sort_by)
    }

    private fun generateAnalyticsGraph(report: MarketingFlashReportPO) {
        IntentUtil.visitUrl(this, report.graphicUrl)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_market_flash_report
    }

    override fun getViewModelClass(): Class<MarketFlashReportFullListViewModel> {
        return MarketFlashReportFullListViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}