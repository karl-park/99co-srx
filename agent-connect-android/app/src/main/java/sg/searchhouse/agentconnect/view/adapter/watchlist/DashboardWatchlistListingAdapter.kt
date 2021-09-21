package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistListingDashboardBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

class DashboardWatchlistListingAdapter constructor(
    private val listings: List<ListingPO>,
    private val onItemClick: (ListingPO) -> Unit
) :
    RecyclerView.Adapter<DashboardWatchlistListingAdapter.ListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        return ListingViewHolder(
            ListItemWatchlistListingDashboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listing = listings[position]
        holder.binding.listingPO = listing
        holder.binding.layoutWatchlistListingCore.card.setOnClickListener {
            onItemClick.invoke(
                listing
            )
        }
        holder.binding.executePendingBindings()
    }

    class ListingViewHolder(val binding: ListItemWatchlistListingDashboardBinding) :
        RecyclerView.ViewHolder(binding.root)
}