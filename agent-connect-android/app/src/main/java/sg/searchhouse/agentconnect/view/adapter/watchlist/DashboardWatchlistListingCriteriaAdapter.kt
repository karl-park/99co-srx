package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistListingCriteriaBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus

class DashboardWatchlistListingCriteriaAdapter constructor(
    private val onExpand: (WatchlistPropertyCriteriaPO) -> Unit,
    private val onClickViewAll: (WatchlistPropertyCriteriaPO) -> Unit,
    private val onClickListItem: (ListingPO) -> Unit
) :
    RecyclerView.Adapter<DashboardWatchlistListingCriteriaAdapter.CriteriaViewHolder>() {

    var watchlists = listOf<WatchlistPropertyCriteriaPO>()
    var listingsMap = mapOf<Int, List<ListingPO>>()

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriteriaViewHolder {
        return CriteriaViewHolder(
            ListItemWatchlistListingCriteriaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return watchlists.size
    }

    override fun onBindViewHolder(holder: CriteriaViewHolder, position: Int) {
        val criteria = watchlists[position]
        val criteriaId = criteria.id ?: throw IllegalStateException("Criteria must have ID here!")
        val listings = listingsMap[criteriaId]
        // If listings null, status = LOADING, if empty, status = FAIL
        val itemsStatus = listings?.run {
            if (isNotEmpty()) {
                ApiStatus.StatusKey.SUCCESS
            } else {
                ApiStatus.StatusKey.FAIL
            }
        } ?: ApiStatus.StatusKey.LOADING
        holder.binding.itemsStatus = itemsStatus
        holder.binding.isExpand = selectedPosition == position
        holder.binding.layoutHeader.setOnClickListener {
            if ((criteria.numRecentItems ?: 0) > 0) {
                selectDeselectItem(position)
            }
        }
        holder.binding.criteria = criteria
        holder.binding.btnViewAll.setOnClickQuickDelayListener { onClickViewAll.invoke(criteria) }
        listings?.run { populateListings(holder, this) }
    }

    private fun populateListings(holder: CriteriaViewHolder, listings: List<ListingPO>) {
        val adapter = DashboardWatchlistListingAdapter(listings) {
            onClickListItem.invoke(it)
        }
        holder.binding.list.setupLayoutManager()
        holder.binding.list.adapter = adapter
    }

    private fun selectDeselectItem(position: Int) {
        if (selectedPosition == position) {
            selectedPosition = -1
        } else {
            val oldPosition = selectedPosition
            selectedPosition = position
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
            onExpand.invoke(watchlists[position])
        }
        notifyItemChanged(position)
    }

    class CriteriaViewHolder(val binding: ListItemWatchlistListingCriteriaBinding) :
        RecyclerView.ViewHolder(binding.root)
}