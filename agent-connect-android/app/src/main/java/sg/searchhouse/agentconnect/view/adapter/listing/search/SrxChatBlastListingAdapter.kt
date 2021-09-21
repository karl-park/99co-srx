package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSrxChatListingBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO

class SrxChatBlastListingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listings: List<ListingPO> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemSrxChatListingViewHolder(
            ListItemSrxChatListingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listings.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemSrxChatListingViewHolder -> {
                val listing = listings[position]
                holder.binding.listingPO = listing
            }
        }
    }

    fun updateListings(items: List<ListingPO>) {
        this.listings = items
        notifyDataSetChanged()
    }

    class ListItemSrxChatListingViewHolder(val binding: ListItemSrxChatListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}