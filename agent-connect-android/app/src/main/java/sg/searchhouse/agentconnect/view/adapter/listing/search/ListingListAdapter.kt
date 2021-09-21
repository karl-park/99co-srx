package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemListingBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingsViewModel

class ListingListAdapter(
    val onClickListener: (listingId: String, listingType: String) -> Unit,
    private val onCheckListener: (listingId: String, listingType: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isSelectMode: Boolean = false
    var isShowCheckBox: Boolean = true
    var items: ArrayList<Any> = arrayListOf()
    var selectedItems: ArrayList<Pair<String, String>> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LISTING -> {
                val binding = ListItemListingBinding.inflate(LayoutInflater.from(parent.context))
                ListingViewHolder(
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListingViewHolder -> {
                val listingPO = items[position] as ListingPO
                holder.binding.isSelected = selectedItems.any {
                    listingPO.hasId(it.first, it.second)
                }
                holder.binding.isSelectMode = isSelectMode
                holder.binding.isShowCheckBox = isShowCheckBox
                holder.binding.listingPO = listingPO
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
                holder.binding.checkbox.setOnClickListener {
                    onCheckListener.invoke(listingPO.id, listingPO.listingType)
                }
                holder.binding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListingPO -> VIEW_TYPE_LISTING
            is Loading -> VIEW_TYPE_LOADING
            is ListingsViewModel.NearByHeader -> VIEW_TYPE_HEADER_NEARBY
            else -> throw ClassCastException("Wrong list item object")
        }
    }

    class ListingViewHolder(val binding: ListItemListingBinding) :
        RecyclerView.ViewHolder(binding.root)

    open class NearByHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_LISTING = 2
        const val VIEW_TYPE_HEADER_NEARBY = 3
    }
}