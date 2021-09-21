package sg.searchhouse.agentconnect.view.adapter.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSubZoneQueryBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.app.PlanningAreaSubZone
import sg.searchhouse.agentconnect.model.app.QuerySubZone
import sg.searchhouse.agentconnect.event.community.UpdateCommunityListEvent

class SubZoneQueryAdapter : RecyclerView.Adapter<SubZoneQueryAdapter.SubZoneQueryViewHolder>() {
    var items: List<QuerySubZone> = listOf()
    var selectedItems: List<PlanningAreaSubZone> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubZoneQueryViewHolder {
        val binding = ListItemSubZoneQueryBinding.inflate(LayoutInflater.from(parent.context))
        return SubZoneQueryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: SubZoneQueryViewHolder, position: Int) {
        val item = items[position]
        holder.binding.subZone = item
        holder.binding.isSelected = isQuerySubZoneSelected(item)
        holder.binding.layoutContainer.setOnClickListener {
            val planningAreaSubZone =
                PlanningAreaSubZone(item.planningAreaId, item.subZonePO?.getCommunityId())
            RxBus.publish(UpdateCommunityListEvent(planningAreaSubZone))
        }
        holder.binding.executePendingBindings()
    }

    private fun isQuerySubZoneSelected(querySubZone: QuerySubZone): Boolean {
        return selectedItems.any {
            it.planningAreaId == querySubZone.planningAreaId && it.subZoneId == querySubZone.subZonePO?.getCommunityId()
        }
    }

    fun updateSubZone(planningAreaSubZone: PlanningAreaSubZone) {
        val alreadyExist = selectedItems.contains(planningAreaSubZone)
        if (alreadyExist) {
            deselectSubZone(planningAreaSubZone)
        } else {
            selectSubZone(planningAreaSubZone)
        }
        notifyDataSetChanged()
    }

    private fun selectSubZone(planningAreaSubZone: PlanningAreaSubZone) {
        selectedItems = selectedItems + planningAreaSubZone
    }

    private fun deselectSubZone(planningAreaSubZone: PlanningAreaSubZone) {
        selectedItems = selectedItems.filter { it != planningAreaSubZone }
    }

    class SubZoneQueryViewHolder(val binding: ListItemSubZoneQueryBinding) :
        RecyclerView.ViewHolder(binding.root)
}