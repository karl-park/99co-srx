package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemPlanningDecisionBinding
import sg.searchhouse.agentconnect.model.api.project.PlanningDecisionPO

class PlanningDecisionAdapter :
    RecyclerView.Adapter<PlanningDecisionAdapter.PlanningDecisionViewHolder>() {
    private var items: List<DecisionWrapper> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningDecisionViewHolder {
        val binding = ListItemPlanningDecisionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlanningDecisionViewHolder(
            binding
        )
    }

    fun setListItems(planningDecisionPOs: List<PlanningDecisionPO>) {
        items = planningDecisionPOs.map {
            DecisionWrapper(it, false)
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: PlanningDecisionViewHolder, position: Int) {
        val decisionWrapper = items[position]
        holder.binding.planningDecisionPO = decisionWrapper.planningDecisionPO
        holder.binding.isExpand = decisionWrapper.isExpand

        holder.binding.root.setOnClickListener { onExpandCollapse(it, position) }
        holder.binding.layoutHeader.setOnClickListener { onExpandCollapse(it, position) }

        holder.binding.executePendingBindings()
    }

    private fun onExpandCollapse(view: View, position: Int) {
        view.postDelayed({
            items = items.mapIndexed { index, decisionWrapper ->
                if (index == position) {
                    decisionWrapper.isExpand = !decisionWrapper.isExpand
                }
                decisionWrapper
            }
            notifyItemChanged(position)
        }, view.context.resources.getInteger(R.integer.anim_time_fast).toLong())
    }

    class DecisionWrapper(val planningDecisionPO: PlanningDecisionPO, var isExpand: Boolean)

    class PlanningDecisionViewHolder(val binding: ListItemPlanningDecisionBinding) :
        RecyclerView.ViewHolder(binding.root)
}