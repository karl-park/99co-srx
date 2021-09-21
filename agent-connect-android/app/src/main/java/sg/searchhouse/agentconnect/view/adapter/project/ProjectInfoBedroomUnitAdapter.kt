package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemBedroomUnitBinding
import sg.searchhouse.agentconnect.model.api.project.SRXProjectBedroomPO

class ProjectInfoBedroomUnitAdapter :
    RecyclerView.Adapter<ProjectInfoBedroomUnitAdapter.BedroomUnitViewHolder>() {
    var items: List<SRXProjectBedroomPO> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BedroomUnitViewHolder {
        val binding = ListItemBedroomUnitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BedroomUnitViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: BedroomUnitViewHolder, position: Int) {
        holder.binding.bedroomPO = items[position]
    }

    class BedroomUnitViewHolder(val binding: ListItemBedroomUnitBinding) :
        RecyclerView.ViewHolder(binding.root)
}