package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_listing.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.GridItemListingEndBinding
import sg.searchhouse.agentconnect.databinding.GridItemListingStartBinding
import sg.searchhouse.agentconnect.databinding.ListItemListingFeaturedPlusBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingsViewModel

class ListingGridAdapter(
    val onClickListener: (listingId: String, listingType: String) -> Unit,
    private val onCheckListener: (listingId: String, listingType: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isSelectMode: Boolean = false
    var items: ArrayList<Any> = arrayListOf()
    var selectedItems: ArrayList<Pair<String, String>> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LISTING_FEATURED_PLUS -> {
                val binding =
                    ListItemListingFeaturedPlusBinding.inflate(LayoutInflater.from(parent.context))
                FeaturedPlusViewHolder(
                    binding
                )
            }
            VIEW_TYPE_LISTING_START -> {
                val binding =
                    GridItemListingStartBinding.inflate(LayoutInflater.from(parent.context))
                StartGridViewHolder(
                    binding
                )
            }
            VIEW_TYPE_LISTING_END -> {
                val binding = GridItemListingEndBinding.inflate(LayoutInflater.from(parent.context))
                EndGridViewHolder(
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
            VIEW_TYPE_HEADER_NEARBY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_header_near_by, parent, false)
                NearByHeaderViewHolder(
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StartGridViewHolder -> {
                val listingPO = items[position] as ListingPO
                holder.binding.listingPO = listingPO
                holder.binding.isSelected = isSelected(listingPO)
                holder.binding.isSelectMode = isSelectMode
                holder.itemView.setOnClickListener {
                    onClickListener.invoke(
                        listingPO.id,
                        listingPO.listingType
                    )
                }
                holder.itemView.setOnLongClickListener {
                    onCheckListener.invoke(listingPO.id, listingPO.listingType)
                    true
                }
                holder.itemView.checkbox.setOnClickListener {
                    onCheckListener.invoke(listingPO.id, listingPO.listingType)
                }
                holder.binding.executePendingBindings()
            }
            is EndGridViewHolder -> {
                val listingPO = items[position] as ListingPO
                holder.binding.listingPO = listingPO
                holder.binding.isSelected = isSelected(listingPO)
                holder.binding.isSelectMode = isSelectMode
                holder.itemView.setOnClickListener {
                    if (isSelectMode) {
                        onCheckListener.invoke(listingPO.id, listingPO.listingType)
                    } else {
                        onClickListener.invoke(
                            listingPO.id,
                            listingPO.listingType
                        )
                    }
                }
                holder.itemView.setOnLongClickListener {
                    onCheckListener.invoke(listingPO.id, listingPO.listingType)
                    true
                }
                holder.itemView.checkbox.setOnClickListener {
                    onCheckListener.invoke(listingPO.id, listingPO.listingType)
                }
                holder.binding.executePendingBindings()
            }
        }
    }

    private fun isSelected(listingPO: ListingPO): Boolean {
        return selectedItems.any {
            listingPO.hasId(it.first, it.second)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListingPO -> {
                val spans = items.subList(0, position + 1).map {
                    if (it is ListingPO) {
                        1
                    } else {
                        2
                    }
                }.sum()

                when {
                    spans % 2 == 1 -> VIEW_TYPE_LISTING_START
                    else -> VIEW_TYPE_LISTING_END
                }
            }
            is Loading -> VIEW_TYPE_LOADING
            is ListingsViewModel.NearByHeader -> VIEW_TYPE_HEADER_NEARBY
            else -> throw ClassCastException("Wrong list item object")
        }
    }

    class StartGridViewHolder(val binding: GridItemListingStartBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EndGridViewHolder(val binding: GridItemListingEndBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FeaturedPlusViewHolder(val binding: ListItemListingFeaturedPlusBinding) :
        RecyclerView.ViewHolder(binding.root)

    open class NearByHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_LISTING_START = 2
        const val VIEW_TYPE_LISTING_END = 3
        const val VIEW_TYPE_LISTING_FEATURED_PLUS = 4
        const val VIEW_TYPE_HEADER_NEARBY = 5
    }
}