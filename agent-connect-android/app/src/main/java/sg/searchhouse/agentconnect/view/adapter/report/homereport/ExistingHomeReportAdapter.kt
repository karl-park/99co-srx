package sg.searchhouse.agentconnect.view.adapter.report.homereport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemExistingHomeReportBinding
import sg.searchhouse.agentconnect.model.app.ExistingHomeReport

class ExistingHomeReportAdapter(private val onSelectItem: ((ExistingHomeReport) -> Unit)) :
    RecyclerView.Adapter<ExistingHomeReportAdapter.HomeReportViewHolder>() {

    var listItems: List<ExistingHomeReport> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeReportViewHolder {
        return HomeReportViewHolder(
            ListItemExistingHomeReportBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: HomeReportViewHolder, position: Int) {
        val result = listItems[position]
        holder.binding.result = result
        holder.binding.root.setOnClickListener {
            onSelectItem.invoke(result)
        }
    }

    class HomeReportViewHolder(val binding: ListItemExistingHomeReportBinding) :
        RecyclerView.ViewHolder(binding.root)
}