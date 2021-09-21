package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.GridItemProjectInfoFacilityLeftBinding
import sg.searchhouse.agentconnect.databinding.GridItemProjectInfoFacilityRightBinding
import sg.searchhouse.agentconnect.model.api.common.NameValuePO

class ProjectInfoFacilityAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<NameValuePO> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FACILITY_START -> {
                val binding = GridItemProjectInfoFacilityLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                StartFacilityViewHolder(binding)
            }
            VIEW_TYPE_FACILITY_END -> {
                val binding = GridItemProjectInfoFacilityRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                EndFacilityViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type for facility adapter")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StartFacilityViewHolder -> {
                holder.binding.nameValuePO = items[position]
            }
            is EndFacilityViewHolder -> {
                holder.binding.nameValuePO = items[position]
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position % 2 == 0 -> VIEW_TYPE_FACILITY_START
            else -> VIEW_TYPE_FACILITY_END
        }
    }

    class StartFacilityViewHolder(val binding: GridItemProjectInfoFacilityLeftBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EndFacilityViewHolder(val binding: GridItemProjectInfoFacilityRightBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_FACILITY_START = 1
        const val VIEW_TYPE_FACILITY_END = 2
    }
}