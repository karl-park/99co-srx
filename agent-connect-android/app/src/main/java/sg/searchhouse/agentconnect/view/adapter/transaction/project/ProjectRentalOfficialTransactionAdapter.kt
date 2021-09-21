package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemProjectTransactionOfficialBinding
import sg.searchhouse.agentconnect.model.api.transaction.TransactionListItem
import sg.searchhouse.agentconnect.util.ViewUtil

class ProjectRentalOfficialTransactionAdapter :
    RecyclerView.Adapter<ProjectRentalOfficialTransactionAdapter.ProjectRentalOfficialViewHolder>() {
    var items: List<TransactionListItem> = emptyList()
    var scale: Float = 1.0f

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjectRentalOfficialViewHolder {
        val binding = ListItemProjectTransactionOfficialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectRentalOfficialViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ProjectRentalOfficialViewHolder, position: Int) {
        val transaction = items[position]
        holder.binding.transaction = transaction
        holder.binding.scale = scale
        holder.binding.executePendingBindings()
    }

    class ProjectRentalOfficialViewHolder(val binding: ListItemProjectTransactionOfficialBinding) :
        RecyclerView.ViewHolder(binding.root)
}