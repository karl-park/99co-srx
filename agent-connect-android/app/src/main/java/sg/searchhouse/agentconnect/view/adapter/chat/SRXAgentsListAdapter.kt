package sg.searchhouse.agentconnect.view.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemAgent1Binding
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class SRXAgentsListAdapter(
    private val agents: List<Any>,
    private val onSelectAgentItem: ((AgentPO) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedAgentIds: HashSet<Int> = hashSetOf()

    init {
        setHasStableIds(true)
    }

    companion object {
        const val VIEW_TYPE_AGENT_ITEM = 1
        const val VIEW_TYPE_LOADING = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_AGENT_ITEM -> ListItemAgentViewHolder(
                ListItemAgent1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loading_indicator,
                    parent,
                    false
                )
            )
            else -> throw Throwable("WrongView Type in Srx agent lists adapter")
        }
    }

    override fun getItemCount(): Int {
        return agents.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemAgentViewHolder -> {
                val agentPO = agents[position] as AgentPO
                agentPO.isSelected = selectedAgentIds.contains(agentPO.userId)
                //start binding
                holder.binding.agent = agentPO
                //binding components
                holder.binding.cbAgentItem.isChecked = agentPO.isSelected
                holder.binding.llSrxAgent.setOnClickListener {
                    agentPO.isSelected = !holder.binding.cbAgentItem.isChecked
                    holder.binding.agent = agentPO
                    onSelectAgentItem.invoke(agentPO)
                    notifyItemChanged(position)
                }
            }
            is LoadingViewHolder -> println("Show loading indicator")
            else -> throw Throwable("Wrong View Type in onBindViewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (agents[position]) {
            is AgentPO -> VIEW_TYPE_AGENT_ITEM
            is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException(" Wrong view type in agent directory adapter")
        }
    }

    fun updateSelectedAgents(agentIds: List<Int>) {
        selectedAgentIds.clear()
        selectedAgentIds.addAll(agentIds)
        notifyDataSetChanged()
    }

    class ListItemAgentViewHolder(val binding: ListItemAgent1Binding) :
        RecyclerView.ViewHolder(binding.root)
}