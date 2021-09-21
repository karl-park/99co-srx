package sg.searchhouse.agentconnect.view.adapter.findagent

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemAgent2Binding
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import java.lang.ClassCastException

class FindAgentAdapter(
    private var agents: List<Any>,
    private val onItemClickListener: ((AgentPO) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_AGENT = 0
        const val VIEW_TYPE_LOADING = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_AGENT -> {
                ListItemAgentViewHolder(ListItemAgent2Binding.inflate(LayoutInflater.from(parent.context)))
            }
            VIEW_TYPE_LOADING -> {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.loading_indicator,
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("Wrong View Type in Find Agent Adapter")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (agents[position]) {
            is AgentPO -> VIEW_TYPE_AGENT
            is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException(" Wrong view type in agent directory adapter")
        }
    }

    override fun getItemCount(): Int {
        return agents.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun clearAllAgents() {
        agents = emptyList()
        notifyDataSetChanged()
    }

    fun updateAllAgents(items: List<Any>) {
        agents = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemAgentViewHolder -> {
                val agent = agents[position] as AgentPO
                holder.binding.agentPO = agent
                holder.binding.layoutAgentItem2.setOnClickListener {
                    onItemClickListener.invoke(agent)
                }
            }
            is LoadingViewHolder -> Log.i("FindAgentAdapter", "Show Loading Indicator")
            else -> throw Throwable("Wrong View Type in onBindViewHolder of Find Agent Adapter")
        }
    }

    class ListItemAgentViewHolder(val binding: ListItemAgent2Binding) :
        RecyclerView.ViewHolder(binding.root)
}