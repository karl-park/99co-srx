package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSelectedListingBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

class SelectedListingAdapter(private val onDeselect: (listingId: String, listingType: String) -> Unit) :
    RecyclerView.Adapter<SelectedListingAdapter.ListingViewHolder>() {
    var items: List<ListingPO> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListingViewHolder {
        val binding = ListItemSelectedListingBinding.inflate(LayoutInflater.from(parent.context))
        return ListingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getListingPosition(listingId: String, listingType: String): Int? {
        return items.indexOfFirst { it.id == listingId && it.listingType == listingType }
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listingPO = items[position]
        holder.binding.listingPO = listingPO
        holder.binding.btnRemove.setOnClickListener {
            onDeselect.invoke(listingPO.id, listingPO.listingType)
        }
        holder.binding.executePendingBindings()
    }

    class ListingViewHolder(val binding: ListItemSelectedListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}