package sg.searchhouse.agentconnect.view.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.CardItemAgentBinding
import sg.searchhouse.agentconnect.model.api.agent.AgentPO

class ChatAgentListAdapter(val agents: ArrayList<AgentPO>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onRemoveAgentItem: ((AgentPO) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AgentItemViewHolder(
            CardItemAgentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return agents.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AgentItemViewHolder -> {
                val agentPO = agents[position]
                holder.binding.agentPO = agentPO
                holder.binding.ibRemoveAgent.setOnClickListener {
                    agentPO.isSelected = false
                    onRemoveAgentItem?.invoke(agentPO)
                }
            }
        }
    }

    class AgentItemViewHolder(val binding: CardItemAgentBinding) :
        RecyclerView.ViewHolder(binding.root)
}