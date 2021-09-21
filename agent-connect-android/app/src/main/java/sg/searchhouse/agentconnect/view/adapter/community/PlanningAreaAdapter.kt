package sg.searchhouse.agentconnect.view.adapter.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemPlanningAreaBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.community.CommunityTopDownPO
import sg.searchhouse.agentconnect.model.app.PlanningAreaSubZone
import sg.searchhouse.agentconnect.model.app.SelectAll
import sg.searchhouse.agentconnect.event.community.ScrollToExpandedPlanningAreaEvent

class PlanningAreaAdapter : RecyclerView.Adapter<PlanningAreaAdapter.PlanningAreaViewHolder>() {
    var items: List<CommunityTopDownPO> = listOf()

    var selectedItems = emptyList<PlanningAreaSubZone>()

    var expandedPlanningAreaId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningAreaViewHolder {
        val binding = ListItemPlanningAreaBinding.inflate(LayoutInflater.from(parent.context))
        return PlanningAreaViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: PlanningAreaViewHolder, position: Int) {
        val planningArea = items[position]
        holder.binding.planningArea = planningArea
        holder.binding.layoutHeader.setOnClickListener {
            expandedPlanningAreaId = if (expandedPlanningAreaId != planningArea.getCommunityId()) {
                planningArea.getCommunityId()
            } else {
                null
            }
            notifyDataSetChanged()
            expandedPlanningAreaId?.run {
                // Animate scroll to expanded item position
                RxBus.publish(ScrollToExpandedPlanningAreaEvent(this))
            }
        }
        holder.binding.isExpand = expandedPlanningAreaId == planningArea.getCommunityId()
        maybePopulateSubZones(holder, planningArea)
        holder.binding.executePendingBindings()
    }

    private fun maybePopulateSubZones(
        holder: PlanningAreaViewHolder,
        planningArea: CommunityTopDownPO
    ) {
        if (holder.binding.isExpand == true) {
            // TODO Maybe wrap these into the adapter
            val allSubZonesLabel = holder.binding.root.context.getString(
                R.string.label_all_sub_zones,
                planningArea.community.name
            )
            val subZones = listOf(SelectAll(allSubZonesLabel)) + planningArea.children
            val subZoneAdapter = SubZoneAdapter(planningArea.getCommunityId())
            subZoneAdapter.items = subZones
            subZoneAdapter.selectedSubZones = getSelectedSubZones(planningArea)
            holder.binding.listSubZones.setupLayoutManager()
            holder.binding.listSubZones.adapter = subZoneAdapter
            holder.binding.listSubZones.adapter?.notifyDataSetChanged()
        }
    }

    private fun getSelectedSubZones(planningArea: CommunityTopDownPO): List<Int?> {
        return selectedItems.filter {
            it.planningAreaId == planningArea.getCommunityId()
        }.map { it.subZoneId }
    }

    fun selectItems(communityIds: List<Int>) {
        selectedItems = items.map { planningArea ->
            if (communityIds.any { it == planningArea.getCommunityId() }) {
                val all = PlanningAreaSubZone(planningArea.getCommunityId(), null)
                val subZoneItems = planningArea.children.map { subZone ->
                    PlanningAreaSubZone(planningArea.getCommunityId(), subZone.getCommunityId())
                }
                subZoneItems + listOf(all)
            } else {
                val childCommunityIds = planningArea.children.map { it.getCommunityId() }
                childCommunityIds.intersect(communityIds).map {
                    PlanningAreaSubZone(planningArea.getCommunityId(), it)
                }
            }
        }.flatten()
        notifyDataSetChanged()
    }

    fun updatePlanningAreaSubZone(planningAreaSubZone: PlanningAreaSubZone) {
        val alreadyExist = selectedItems.contains(planningAreaSubZone)
        if (alreadyExist) {
            deselectPlanningAreaSubZone(planningAreaSubZone)
        } else {
            selectPlanningAreaSubZone(planningAreaSubZone)
        }
        notifyDataSetChanged()
    }

    private fun selectPlanningAreaSubZone(planningAreaSubZone: PlanningAreaSubZone) {
        if (planningAreaSubZone.subZoneId == null) {
            selectPlanningAreaAllSubZones(planningAreaSubZone)
        } else {
            selectedItems = selectedItems + planningAreaSubZone

            // If corresponding planning area is full, also add `null` (select all) under this planning area
            maybeAppendSelectAllItem(planningAreaSubZone)
        }
    }

    private fun maybeAppendSelectAllItem(planningAreaSubZone: PlanningAreaSubZone) {
        val allSubZones = getPlanningAreaAllSubZones(planningAreaSubZone.planningAreaId)
        val hasNonSelectedSubZones =
            allSubZones.any { !selectedItems.contains(it) }
        val hasSelectAll =
            selectedItems.any { it.planningAreaId == planningAreaSubZone.planningAreaId && it.subZoneId == null }
        if (!hasNonSelectedSubZones && !hasSelectAll) {
            selectedItems = selectedItems + PlanningAreaSubZone(
                planningAreaSubZone.planningAreaId,
                null
            )
        }
    }

    private fun selectPlanningAreaAllSubZones(planningAreaSubZone: PlanningAreaSubZone) {
        // If sub zone is null, select all in the planning area
        val thisPlanningAreaSubZones =
            getPlanningAreaAllSubZones(planningAreaSubZone.planningAreaId)
        val notThisPlanningAreaSubZones =
            selectedItems.filter { it != planningAreaSubZone }
        val selectAllItem = PlanningAreaSubZone(planningAreaSubZone.planningAreaId, null)
        selectedItems =
            notThisPlanningAreaSubZones + thisPlanningAreaSubZones + listOf(selectAllItem)
    }

    // NOTE: All sub-zones, except null (select all)
    private fun getPlanningAreaAllSubZones(planningAreaId: Int): List<PlanningAreaSubZone> {
        return items.find { it.getCommunityId() == planningAreaId }?.children?.map {
            PlanningAreaSubZone(planningAreaId, it.getCommunityId())
        } ?: emptyList()
    }

    private fun hasPlanningAreaSelectAll(planningAreaId: Int): Boolean {
        return selectedItems.any { it.planningAreaId == planningAreaId && it.subZoneId == null }
    }

    private fun deselectPlanningAreaSubZone(planningAreaSubZone: PlanningAreaSubZone) {
        selectedItems = when {
            planningAreaSubZone.subZoneId == null -> {
                // If sub zone is null, deselect all in the planning area
                selectedItems.filter { it.planningAreaId != planningAreaSubZone.planningAreaId }
            }
            hasPlanningAreaSelectAll(planningAreaSubZone.planningAreaId) -> {
                // If originally selected all in this planning area, remove sub zone and null (all) of the planning area
                selectedItems.filter {
                    val isSelectAll =
                        it.planningAreaId == planningAreaSubZone.planningAreaId && it.subZoneId == null
                    it != planningAreaSubZone && !isSelectAll
                }
            }
            else -> {
                selectedItems.filter { it != planningAreaSubZone }
            }
        }
    }

    fun getPositionByPlanningAreaId(planningAreaId: Int): Int {
        return items.indexOfFirst { it.getCommunityId() == planningAreaId }
    }

    class PlanningAreaViewHolder(val binding: ListItemPlanningAreaBinding) :
        RecyclerView.ViewHolder(binding.root)
}