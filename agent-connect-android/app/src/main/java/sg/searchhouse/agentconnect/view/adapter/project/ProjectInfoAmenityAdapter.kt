package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemProjectInfoAmenityBinding
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO

class ProjectInfoAmenityAdapter :
    RecyclerView.Adapter<ProjectInfoAmenityAdapter.AmenityViewHolder>() {
    var items: List<AmenitiesPO> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AmenityViewHolder {
        val binding = ListItemProjectInfoAmenityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AmenityViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: AmenityViewHolder, position: Int) {
        holder.binding.amenity = items[position]
    }

    class AmenityViewHolder(val binding: ListItemProjectInfoAmenityBinding) :
        RecyclerView.ViewHolder(binding.root)
}