package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemProjectInfoFixtureBinding
import sg.searchhouse.agentconnect.model.api.common.NameValuePO

class ProjectInfoFixtureAdapter :
    RecyclerView.Adapter<ProjectInfoFixtureAdapter.KeyInfoViewHolder>() {
    var items: List<NameValuePO> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KeyInfoViewHolder {
        val binding = ListItemProjectInfoFixtureBinding.inflate(
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
        holder.binding.nameValuePO = items[position]
    }

    class KeyInfoViewHolder(val binding: ListItemProjectInfoFixtureBinding) :
        RecyclerView.ViewHolder(binding.root)
}