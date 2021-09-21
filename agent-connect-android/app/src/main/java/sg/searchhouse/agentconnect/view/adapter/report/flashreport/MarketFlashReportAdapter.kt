package sg.searchhouse.agentconnect.view.adapter.report.flashreport

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemMarketFlashReportBinding
import sg.searchhouse.agentconnect.model.api.flashreport.MarketingFlashReportPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class MarketFlashReportAdapter(
    private val reports: List<Any>,
    private val onItemClickListener: (MarketingFlashReportPO) -> Unit,
    private val onDownloadReport: (MarketingFlashReportPO) -> Unit,
    private val onGenerateAnalyticsGraph: (MarketingFlashReportPO) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var allowGenerateAnalyticGraph: Boolean = false

    companion object {
        private const val VIEW_TYPE_MARKETING_FLASH_REPORT = 1
        private const val VIEW_TYPE_LOADING = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MARKETING_FLASH_REPORT -> {
                ListItemMarketFlashReportViewHolder(
                    ListItemMarketFlashReportBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_LOADING -> {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.loading_indicator,
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("Wrong view type in market flash report adapter")
        }
    }

    override fun getItemCount(): Int {
        return reports.count()
    }

    override fun getItemViewType(position: Int): Int {
        return when (reports[position]) {
            is MarketingFlashReportPO -> VIEW_TYPE_MARKETING_FLASH_REPORT
            is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException("Wrong view type in agent directory adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is ListItemMarketFlashReportViewHolder -> {
                val report = reports[position] as MarketingFlashReportPO
                holder.binding.marketingReportPO = report
                holder.binding.showAnalytic =
                    !TextUtils.isEmpty(report.graphicUrl) && allowGenerateAnalyticGraph
                holder.binding.cardMarketingReport.setOnClickListener {
                    onItemClickListener.invoke(report)
                }
                holder.binding.layoutAnalyticsGraph.setOnClickListener {
                    onGenerateAnalyticsGraph.invoke(report)
                }
                holder.binding.layoutReportDownload.setOnClickListener {
                    onDownloadReport.invoke(report)
                }
            }
            is LoadingViewHolder -> {
                println("showing loading indicator for flash report")
            }
            else -> {
                throw Throwable("Wrong view type in market flash report adapter")
            }
        }
    }

    class ListItemMarketFlashReportViewHolder(val binding: ListItemMarketFlashReportBinding) :
        RecyclerView.ViewHolder(binding.root)
}