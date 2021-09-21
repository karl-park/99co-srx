package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemProjectKeyInfoBinding
import sg.searchhouse.agentconnect.model.app.ProjectKeyInfo

class ProjectKeyInfoAdapter :
    RecyclerView.Adapter<ProjectKeyInfoAdapter.KeyInfoViewHolder>() {
    var items: List<ProjectKeyInfo> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KeyInfoViewHolder {
        val binding = ListItemProjectKeyInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KeyInfoViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: KeyInfoViewHolder, position: Int) {
        holder.binding.keyInfo = items[position]
    }

    class KeyInfoViewHolder(val binding: ListItemProjectKeyInfoBinding) :
        RecyclerView.ViewHolder(binding.root)
}