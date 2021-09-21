package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_dashboard_market_flash_report.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardMarketFlashReportBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.report.flashreport.MarketFlashReportFullListActivity
import sg.searchhouse.agentconnect.view.adapter.report.flashreport.MarketFlashReportAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardMarketFlashReportViewModel

class DashboardMarketFlashReportFragment :
    ViewModelFragment<DashboardMarketFlashReportViewModel, FragmentDashboardMarketFlashReportBinding>() {

    private lateinit var adapter: MarketFlashReportAdapter
    private var flashReportDetailFragment: MarketFlashReportDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAndAdapter()
        performRequest()
        observeRxBuses()
        observeLiveData()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        val mContext = context ?: return
        adapter =
            MarketFlashReportAdapter(
                viewModel.reports,
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
        binding.listReports.layoutManager = LinearLayoutManager(mContext)
        binding.listReports.adapter = adapter
    }

    private fun performRequest() {
        viewModel.performRequest()
    }

    private fun observeRxBuses() {
        listenRxBus(LoginAsAgentEvent::class.java) {
            performRequest()
        }
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(viewLifecycleOwner, Observer { response ->
            val activeListings = response?.marketFlashReports ?: return@Observer
            viewModel.reports.clear()
            viewModel.reports.addAll(activeListings)
            adapter.allowGenerateAnalyticGraph = response.hasHistoricalAccess
            viewModel.hasHistoricalAccess.value = response.hasHistoricalAccess
            adapter.notifyDataSetChanged()
        })
    }

    private fun showDetailFlashReport(report: MarketingFlashReportPO) {
        if (flashReportDetailFragment?.isVisible != true) {
            flashReportDetailFragment = MarketFlashReportDialogFragment.newInstance(report)
            flashReportDetailFragment?.show(
                childFragmentManager,
                MarketFlashReportDialogFragment.TAG_MARKET_FLASH_REPORT_SUMMARY
            )
        }
    }

    private fun generateAnalyticsGraph(report: MarketingFlashReportPO) {
        val mContext = context ?: return
        IntentUtil.visitUrl(mContext, report.graphicUrl)
    }

    private fun handleListeners() {
        tv_view_all.setOnClickListener {
            val mContext = context ?: return@setOnClickListener
            val hasAccess = viewModel.hasHistoricalAccess.value ?: false
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.FLASH_REPORT,
                onSuccessAccessibility = {
                    if (hasAccess) {
                        MarketFlashReportFullListActivity.launch(mContext)
                    }
                })
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_market_flash_report
    }

    override fun getViewModelClass(): Class<DashboardMarketFlashReportViewModel> {
        return DashboardMarketFlashReportViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}