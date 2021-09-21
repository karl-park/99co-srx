package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemProjectTransactionOfficialHdbBinding
import sg.searchhouse.agentconnect.model.api.transaction.TransactionListItem

// TODO: Refactor with ProjectRentalOfficialTransactionAdapter
class ProjectRentalOfficialHdbTransactionAdapter :
    RecyclerView.Adapter<ProjectRentalOfficialHdbTransactionAdapter.ProjectRentalOfficialHdbViewHolder>() {
    var items: List<TransactionListItem> = emptyList()
    var scale: Float = 1.0f

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjectRentalOfficialHdbViewHolder {
        val binding = ListItemProjectTransactionOfficialHdbBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectRentalOfficialHdbViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ProjectRentalOfficialHdbViewHolder, position: Int) {
        val transaction = items[position]
        holder.binding.transaction = transaction
        holder.binding.scale = scale
        holder.binding.executePendingBindings()
    }

    class ProjectRentalOfficialHdbViewHolder(val binding: ListItemProjectTransactionOfficialHdbBinding) :
        RecyclerView.ViewHolder(binding.root)
}