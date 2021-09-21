package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistListingMainBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class WatchlistMainListingAdapter constructor(private val onItemClick: (ListingPO) -> Unit) :
    BaseListAdapter() {
    override fun updateListItems(newListItems: List<Any>) {
        listItems = newListItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LISTING -> {
                ListingViewHolder(
                    ListItemWatchlistListingMainBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_LOADING -> {
                // TODO Refactor
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_indicator, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type $viewType!")
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListingViewHolder -> {
                val listing = listItems[position] as ListingPO
                holder.binding.listingPO = listing
                holder.binding.layoutWatchlistListingCore.card.setOnClickListener { onItemClick.invoke(listing) }
                holder.binding.executePendingBindings()
            }
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is ListingPO -> VIEW_TYPE_LISTING
            is Loading -> VIEW_TYPE_LOADING
            else -> throw IllegalArgumentException("Invalid list item class ${listItems[position].javaClass.name}!")
        }
    }

    class ListingViewHolder(val binding: ListItemWatchlistListingMainBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_LISTING = 1
        const val VIEW_TYPE_LOADING = 2
    }
}