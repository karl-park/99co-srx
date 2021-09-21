package sg.searchhouse.agentconnect.view.adapter.report.newlaunches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemNewLaunchesClientBinding

//TODO: currently this adapter is only for mobile numbers first
class NewLaunchesClientsAdapter(private var onCallClients: (mobileNum: String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var clients = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemNewLaunchesClientViewHolder(
            ListItemNewLaunchesClientBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return clients.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemNewLaunchesClientViewHolder -> {
                val client = clients[position]
                holder.binding.nameOrMobileNumber = client
                holder.binding.showDivider = position != clients.size - 1
                holder.binding.ibPhone.setOnClickListener {
                    onCallClients.invoke(client)
                }
            }
        }
    }

    fun updateClients(items: List<String>) {
        clients = items
        notifyDataSetChanged()
    }

    class ListItemNewLaunchesClientViewHolder(val binding: ListItemNewLaunchesClientBinding) :
        RecyclerView.ViewHolder(binding.root)
}