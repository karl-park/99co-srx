package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemAmenityOptionBinding
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum

class AmenityOptionAdapter constructor(var onClickListener: ((amenityOption: LocationEnum.AmenityOption) -> Unit)? = null) :
    RecyclerView.Adapter<AmenityOptionAdapter.AmenityViewHolder>() {
    var items: Array<LocationEnum.AmenityOption> = LocationEnum.AmenityOption.values()
    var selectedItem: LocationEnum.AmenityOption? = items[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmenityViewHolder {
        val binding = ListItemAmenityOptionBinding.inflate(LayoutInflater.from(parent.context))
        return AmenityViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: AmenityViewHolder, position: Int) {
        val amenityOption = items[position]
        holder.binding.amenityOption = amenityOption
        holder.binding.isSelected = TextUtils.equals(amenityOption.value, selectedItem?.value)
        holder.binding.root.setOnClickListener { onClickListener?.invoke(amenityOption) }
        holder.binding.executePendingBindings()
    }

    class AmenityViewHolder(val binding: ListItemAmenityOptionBinding) :
        RecyclerView.ViewHolder(binding.root)
}