package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_fragment_market_flash_report.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentMarketFlashReportBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.view.widget.common.SwipeDetectorView
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.MarketFlashReportViewModel

class MarketFlashReportDialogFragment :
    ViewModelDialogFragment<MarketFlashReportViewModel, DialogFragmentMarketFlashReportBinding>() {

    private var flashReportPO: MarketingFlashReportPO? = null

    companion object {
        const val TAG_MARKET_FLASH_REPORT_SUMMARY = "TAG_MARKET_FLASH_REPORT_SUMMARY"
        private const val EXTRA_KEY_FLASH_REPORT = "EXTRA_KEY_FLASH_REPORT"

        fun newInstance(marketingReport: MarketingFlashReportPO): MarketFlashReportDialogFragment {
            val fragment = MarketFlashReportDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_FLASH_REPORT, marketingReport)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow(backgroundColor = R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateViewModelData()
        observeLiveData()
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupListeners()
    }

    private fun setupExtraParams() {
        val bundle = arguments ?: return
        val data = bundle.getSerializable(EXTRA_KEY_FLASH_REPORT) ?: return
        flashReportPO = data as MarketingFlashReportPO
    }

    private fun updateViewModelData() {
        viewModel.marketingFlashReportPO.postValue(flashReportPO)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun observeLiveData() {
        viewModel.marketingFlashReportPO.observe(viewLifecycleOwner, Observer {
            binding.webViewContent.loadData(
                it.getFormattedObservation(), "text/html",
                "utf-8"
            )
            binding.webViewContent.settings.javaScriptEnabled = true
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        view_blank.setOnClickListener { dismiss() }
        tv_download_full_report.setOnClickListener {
            val report = viewModel.marketingFlashReportPO.value ?: return@setOnClickListener
            ViewUtil.showMessage(getString(R.string.message_download_flash_report, report.title))
            viewModel.downloadReport()
        }
        btn_dismiss.setOnClickListener { dismiss() }
        layout_header.setOnClickListener {
            when (view_blank.visibility) {
                View.VISIBLE -> view_blank.visibility = View.GONE
                else -> dismiss()
            }
        }
        layout_swipe_refresh.setOnRefreshListener { dismiss() }
        view_global_scroll.setOnSwipeListener {
            when (it) {
                SwipeDetectorView.SwipeDirection.UP -> {
                    view_global_scroll.visibility = View.INVISIBLE
                    view_blank.visibility = View.GONE
                }
                SwipeDetectorView.SwipeDirection.DOWN -> {
                    dismiss()
                }
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_market_flash_report
    }

    override fun getViewModelClass(): Class<MarketFlashReportViewModel> {
        return MarketFlashReportViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}