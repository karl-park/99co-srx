package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.GridItemSimilarListingBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

class SimilarListingAdapter constructor(var onClickListener: ((listingPO: ListingPO) -> Unit)? = null) :
    RecyclerView.Adapter<SimilarListingAdapter.SimilarListingViewHolder>() {
    var items: List<ListingPO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarListingViewHolder {
        val binding = GridItemSimilarListingBinding.inflate(LayoutInflater.from(parent.context))
        return SimilarListingViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: SimilarListingViewHolder, position: Int) {
        val listingPO = items[position]
        holder.binding.listingPO = listingPO
        holder.binding.isFirst = position == 0
        holder.binding.isLast = position == items.size - 1
        holder.binding.card.setOnClickListener { onClickListener?.invoke(listingPO) }
    }

    class SimilarListingViewHolder(val binding: GridItemSimilarListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}