package sg.searchhouse.agentconnect.view.adapter.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemReportsAndResearchBinding
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum.*

class ReportsAndResearchAdapter(
    private val items: List<ReportType>,
    private var onSelectReport: (type: ReportType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemReportsAndResearchViewHolder(
            ListItemReportsAndResearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemReportsAndResearchViewHolder -> {
                val report = items[position]
                holder.binding.report = report
                when (report) {
                    ReportType.NEW_LAUNCHES_REPORT -> holder.binding.showIndicator = true
                    else -> holder.binding.showIndicator = false
                }
                holder.binding.cardReportResearch.setOnClickListener {
                    onSelectReport.invoke(report)
                }
            }
        }
    }

    class ListItemReportsAndResearchViewHolder(val binding: ListItemReportsAndResearchBinding) :
        RecyclerView.ViewHolder(binding.root)

}