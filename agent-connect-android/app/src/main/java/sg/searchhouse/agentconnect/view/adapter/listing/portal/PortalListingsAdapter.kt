package sg.searchhouse.agentconnect.view.adapter.listing.portal

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemPortalListingBinding
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.util.IntentUtil

class PortalListingsAdapter(
    private val context: Context,
    private val onSelectListing: ((PortalListingPO) -> Unit),
    private val onToggleSync: ((PortalListingPO, Boolean) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listings = listOf<PortalListingPO>()
    var selectedListingIds = listOf<String>()
    var failedListingIds = listOf<String>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PortalListingViewHolder(
            ListItemPortalListingBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listings.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is PortalListingViewHolder -> {
                val listing = listings[position]
                holder.binding.portalListing = listing
                holder.binding.checkbox.isChecked = selectedListingIds.contains(listing.id)
                holder.binding.switchSync.isChecked = listing.getPortalListingSyncIndicator()
                holder.binding.switchSync.jumpDrawablesToCurrentState()
                holder.binding.showSyncIndicatorLayout =
                    !(listing.isHideSyncIndicator() || listing.srxImportedDate == 0L)
                if (holder.binding.showSyncIndicatorLayout == true) {
                    holder.binding.showErrorLayout = false
                } else holder.binding.showErrorLayout = !TextUtils.isEmpty(listing.error)

                //LISTENERS
                holder.binding.layoutPortalListing.setOnClickListener {
                    onSelectListing.invoke(listing)
                }
                holder.binding.checkbox.setOnClickListener {
                    onSelectListing.invoke(listing)
                }
                holder.binding.switchSync.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (compoundButton.isPressed) {
                        onToggleSync.invoke(listing, isChecked)
                    }
                }
                holder.binding.tvViewInPortal.setOnClickListener {
                    IntentUtil.visitUrl(context, listing.portalUrl)
                }
            }
            else -> {
                throw Throwable("Wrong View Type in portal listing adapter")
            }
        }
    }

    fun populateListings(items: List<PortalListingPO>) {
        this.listings = items
        notifyDataSetChanged()
    }

    class PortalListingViewHolder(val binding: ListItemPortalListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}