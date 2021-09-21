package sg.searchhouse.agentconnect.view.adapter.report.newlaunches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemNewLaunchReportBinding
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse.*
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class NewLaunchesReportsAdapter(
    private val reports: List<Any>,
    private var onCheckEachReport: (report: NewLaunchProject) -> Unit,
    private var onDownloadReport: (report: NewLaunchProject) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedReportIds = listOf<Int>()

    companion object {
        const val VIEW_TYPE_NEW_LAUNCH_PROJECT = 0
        const val VIEW_TYPE_LOADING = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NEW_LAUNCH_PROJECT -> ListItemNewLaunchesReportViewHolder(
                ListItemNewLaunchReportBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loading_indicator,
                    parent,
                    false
                )
            )
            else -> throw Throwable("WrongView Type in new launches reports adapter")
        }
    }

    override fun getItemCount(): Int {
        return reports.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return when (reports[position]) {
            is NewLaunchProject -> VIEW_TYPE_NEW_LAUNCH_PROJECT
            is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException(" Wrong view type in new launches reports")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemNewLaunchesReportViewHolder -> {
                val report = reports[position] as NewLaunchProject
                holder.binding.report = report
                holder.binding.checkboxNewLaunchesReport.isChecked =
                    selectedReportIds.contains(report.crsId)
                holder.binding.checkboxNewLaunchesReport.setOnClickListener {
                    onCheckEachReport.invoke(report)
                }
                holder.binding.cardNewLaunches.setOnClickListener {
                    onDownloadReport.invoke(report)
                }
            }
            is LoadingViewHolder -> println("Showing loading indicator")
            else -> throw Throwable("Wrong view type in new launches reports adapter")
        }
    }

    fun updateSelectedReportIds(reportIds: List<Int>) {
        this.selectedReportIds = reportIds
        notifyDataSetChanged()
    }

    class ListItemNewLaunchesReportViewHolder(val binding: ListItemNewLaunchReportBinding) :
        RecyclerView.ViewHolder(binding.root)
}