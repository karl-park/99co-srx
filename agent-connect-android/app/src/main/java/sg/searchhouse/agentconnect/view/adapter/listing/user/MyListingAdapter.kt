package sg.searchhouse.agentconnect.view.adapter.listing.user

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemMyListingBinding
import sg.searchhouse.agentconnect.databinding.ListItemMyListingPreviewInfoBinding
import sg.searchhouse.agentconnect.databinding.ListItemMyListingPropertyTypeBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.event.listing.user.NotifySelectedListingsEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class MyListingAdapter(
    private val myListingsTab: MyListingsPagerAdapter.MyListingsTab,
    private val onClickListener: (listingId: String, listingType: String, listingGroup: Int) -> Unit,
    private val onViewPortalListingClickListener: (portalUrl: String, portalType: ListingManagementEnum.PortalType) -> Unit,
    private val onAddFeaturedButtonClickListener: (listingIdType: String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var items: List<Any> = emptyList()
    var selectedListings = arrayListOf<ListingPO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LISTING -> {
                val binding = ListItemMyListingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ListingViewHolder(binding)
            }
            VIEW_TYPE_PROPERTY_TYPE_HEADER -> {
                val binding = ListItemMyListingPropertyTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PropertyTypeHeaderViewHolder(
                    binding
                )
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_indicator, parent, false)
                LoadingViewHolder(
                    view
                )
            }
            else -> {
                throw Throwable("Wrong view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun isEnableSelection(): Boolean {
        return myListingsTab != MyListingsPagerAdapter.MyListingsTab.TRANSACTION
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_LISTING -> {
                val listingViewHolder = holder as ListingViewHolder
                val listingPO = items[position] as ListingPO
                listingViewHolder.bindListing(listingPO, position)
            }
            VIEW_TYPE_PROPERTY_TYPE_HEADER -> {
                val headerViewHolder = holder as PropertyTypeHeaderViewHolder
                val header = items[position] as PropertyTypeHeader
                headerViewHolder.binding.title = header.title
                headerViewHolder.binding.executePendingBindings()
            }
            VIEW_TYPE_LOADING -> {
                //show loading indicator
            }
        }
    }

    private fun ListingViewHolder.bindListing(
        listingPO: ListingPO,
        position: Int
    ) {
        binding.listingPO = listingPO
        binding.isSelected = selectedListings.any {
            TextUtils.equals(
                it.getListingIdType(),
                listingPO.getListingIdType()
            )
        }
        binding.disableSelection = !isEnableSelection()
        binding.isActive = myListingsTab == MyListingsPagerAdapter.MyListingsTab.ACTIVE
        binding.btnAddFeatured.setOnClickListener {
            onAddFeaturedButtonClickListener.invoke(listingPO.getListingIdType())
        }

        itemView.run {
            binding.card.setOnClickListener {
                if (selectedListings.isNotEmpty()) {
                    toggleSelection(listingPO, position)
                } else {
                    onClickListener.invoke(
                        listingPO.id,
                        listingPO.listingType,
                        listingPO.group ?: 0
                    )
                }
            }
            binding.card.setOnLongClickListener {
                if (selectedListings.isEmpty() && isEnableSelection()) {
                    toggleSelection(listingPO, position)
                    true
                } else {
                    false
                }
            }
            binding.getPreviewInfoBinding().viewCheckbox.setOnClickListener {
                toggleSelection(listingPO, position)
            }
            binding.layoutPg.btnViewPortalListing.setOnClickListener {
                listingPO.getPGListing()?.portalUrl?.let { portalUrl ->
                    onViewPortalListingClickListener.invoke(
                        portalUrl,
                        ListingManagementEnum.PortalType.PROPERTY_GURU
                    )
                }
            }
            binding.layoutNinetyNine.btnViewPortalListing.setOnClickListener {
                listingPO.getNinetyNineListing()?.portalUrl?.let { portalUrl ->
                    onViewPortalListingClickListener.invoke(
                        portalUrl,
                        ListingManagementEnum.PortalType.NINETY_NINE_CO
                    )
                }
            }
        }
        binding.executePendingBindings()
    }

    private fun ListItemMyListingBinding.getPreviewInfoBinding() : ListItemMyListingPreviewInfoBinding {
        return DataBindingUtil.findBinding(layoutPreviewInfo.root)
            ?: throw IllegalArgumentException("Missing `layoutPreviewInfo` binding")
    }

    private fun toggleSelection(listingPO: ListingPO, position: Int) {
        val isSelected = selectedListings.any {
            TextUtils.equals(
                it.getListingIdType(),
                listingPO.getListingIdType()
            )
        }
        if (isSelected) {
            selectedListings.remove(listingPO)
        } else {
            selectedListings.add(listingPO)
        }
        RxBus.publish(NotifySelectedListingsEvent(selectedListings))
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListingPO -> VIEW_TYPE_LISTING
            is Loading -> VIEW_TYPE_LOADING
            is PropertyTypeHeader -> VIEW_TYPE_PROPERTY_TYPE_HEADER
            else -> throw ClassCastException("Wrong list item object")
        }
    }

    class ListingViewHolder(val binding: ListItemMyListingBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PropertyTypeHeaderViewHolder(val binding: ListItemMyListingPropertyTypeBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PropertyTypeHeader(val title: String)

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_LISTING = 2
        const val VIEW_TYPE_PROPERTY_TYPE_HEADER = 3
    }
}