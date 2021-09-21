package sg.searchhouse.agentconnect.view.adapter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemServerUrlBinding
import sg.searchhouse.agentconnect.view.fragment.main.menu.SwitchServerDialogFragment.*

class ServerUrlAdapter(
    private val servers: List<SRXServer>,
    private var onSwitchServer: (SRXServer) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemServerViewHolder(
            ListItemServerUrlBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return servers.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemServerViewHolder -> {
                val server = servers[position]
                holder.binding.server = server
                holder.binding.layoutServer.setOnClickListener {
                    onSwitchServer.invoke(server)
                }
            }
        }
    }

    class ListItemServerViewHolder(val binding: ListItemServerUrlBinding) :
        RecyclerView.ViewHolder(binding.root)
}