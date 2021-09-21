package sg.searchhouse.agentconnect.view.adapter.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemAllSubZonesBinding
import sg.searchhouse.agentconnect.databinding.ListItemSubZoneBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.community.CommunityTopDownPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.model.app.PlanningAreaSubZone
import sg.searchhouse.agentconnect.model.app.SelectAll
import sg.searchhouse.agentconnect.event.community.UpdateCommunityListEvent
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class SubZoneAdapter(private val planningAreaId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Any> = listOf()
    var selectedSubZones: List<Int?> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ALL_SUB_ZONES -> {
                val binding =
                    ListItemAllSubZonesBinding.inflate(LayoutInflater.from(parent.context))
                AllSubZonesViewHolder(binding)
            }
            VIEW_TYPE_SUB_ZONE -> {
                val binding = ListItemSubZoneBinding.inflate(LayoutInflater.from(parent.context))
                SubZoneViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_indicator, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                throw Throwable("Wrong view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ALL_SUB_ZONES -> {
                val subZoneViewHolder = holder as AllSubZonesViewHolder
                val allStations = items[position] as SelectAll
                subZoneViewHolder.binding.title = allStations.label
                subZoneViewHolder.binding.layoutContainer.setOnClickListener {
                    val planningAreaSubZone =
                        PlanningAreaSubZone(planningAreaId, null)
                    RxBus.publish(UpdateCommunityListEvent(planningAreaSubZone))
                }
                subZoneViewHolder.binding.isSelected =
                    selectedSubZones.any { it == null }
                holder.binding.executePendingBindings()
            }
            VIEW_TYPE_SUB_ZONE -> {
                val subZoneViewHolder = holder as SubZoneViewHolder
                val subZone = items[position] as CommunityTopDownPO
                subZoneViewHolder.binding.subZone = subZone
                subZoneViewHolder.binding.isSelected =
                    selectedSubZones.any { it == subZone.getCommunityId() }
                subZoneViewHolder.binding.layoutContainer.setOnClickListener {
                    val planningAreaSubZone =
                        PlanningAreaSubZone(planningAreaId, subZone.getCommunityId())
                    RxBus.publish(UpdateCommunityListEvent(planningAreaSubZone))
                }
                holder.binding.executePendingBindings()
            }
            VIEW_TYPE_LOADING -> {
            }
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SelectAll -> VIEW_TYPE_ALL_SUB_ZONES
            is CommunityTopDownPO -> VIEW_TYPE_SUB_ZONE
            is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException("Wrong list item object")
        }
    }

    class SubZoneViewHolder(val binding: ListItemSubZoneBinding) :
        RecyclerView.ViewHolder(binding.root)

    class AllSubZonesViewHolder(val binding: ListItemAllSubZonesBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_ALL_SUB_ZONES = 1
        const val VIEW_TYPE_SUB_ZONE = 2
        const val VIEW_TYPE_LOADING = 3
    }
}