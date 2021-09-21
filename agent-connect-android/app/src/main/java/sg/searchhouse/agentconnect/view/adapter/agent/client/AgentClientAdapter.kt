package sg.searchhouse.agentconnect.view.adapter.agent.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemAgentClientBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.model.api.agent.SRXPropertyUserPO

class AgentClientAdapter constructor(
    private val onItemClicked: (client: SRXPropertyUserPO) -> Unit,
    private val onPhoneActionClicked: (mobileNum: Int) -> Unit,
    private val onCheckBoxClicked: (client: SRXPropertyUserPO) -> Unit
) :
    RecyclerView.Adapter<AgentClientAdapter.ClientViewHolder>() {
    var items = emptyList<SRXPropertyUserPO>()
    var selectedItems = emptyList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        return ClientViewHolder(
            ListItemAgentClientBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = items[position]
        holder.binding.client = client
        //TODO: find unique attribute
        holder.binding.checkboxClient.isChecked =
            selectedItems.contains(client.ptUserId)
        holder.binding.btnCall.setOnClickListener {
            onPhoneActionClicked.invoke(client.mobileNum)
        }
        holder.binding.checkboxClient.setOnClickListener {
            onCheckBoxClicked.invoke(client)
        }
        holder.binding.cardAgentClient.setOnLongClickListener {
            onCheckBoxClicked.invoke(client)
            true
        }

        holder.binding.cardAgentClient.setOnClickQuickDelayListener {
            if (selectedItems.isNotEmpty()) {
                onCheckBoxClicked.invoke(client)
            } else {
                onItemClicked.invoke(client)
            }
        }
        holder.binding.executePendingBindings()
    }

    class ClientViewHolder(val binding: ListItemAgentClientBinding) :
        RecyclerView.ViewHolder(binding.root)
}