package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemGroupTransactionCommercialBinding
import sg.searchhouse.agentconnect.databinding.ListItemGroupTransactionHdbBinding
import sg.searchhouse.agentconnect.databinding.ListItemGroupTransactionLandedBinding
import sg.searchhouse.agentconnect.databinding.ListItemGroupTransactionNlpBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class WatchlistMainTransactionAdapter : BaseListAdapter() {
    private var ownershipType: ListingEnum.OwnershipType? = null
    private var propertyMainType: ListingEnum.PropertyMainType? = null

    // NOTE: Call this before populate list items
    fun setup(
        ownershipType: ListingEnum.OwnershipType,
        propertyMainType: ListingEnum.PropertyMainType
    ) {
        this.ownershipType = ownershipType
        this.propertyMainType = propertyMainType
    }

    companion object {
        const val VIEW_TYPE_TRANSACTION = 1
        const val VIEW_TYPE_LOADING = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is TableListResponse.Transactions.Result -> {
                VIEW_TYPE_TRANSACTION
            }
            is Loading -> {
                VIEW_TYPE_LOADING
            }
            else -> throw IllegalArgumentException("Invalid adapter item class!")
        }
    }

    override fun updateListItems(newListItems: List<Any>) {
        listItems = newListItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TRANSACTION -> {
                return when (propertyMainType) {
                    ListingEnum.PropertyMainType.HDB -> {
                        val binding = ListItemGroupTransactionHdbBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        HdbSaleTransactionViewHolder(
                            binding
                        )
                    }
                    ListingEnum.PropertyMainType.CONDO -> {
                        val binding = ListItemGroupTransactionNlpBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        NlpSaleTransactionViewHolder(
                            binding
                        )
                    }
                    ListingEnum.PropertyMainType.LANDED -> {
                        val binding = ListItemGroupTransactionLandedBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        LandedSaleTransactionViewHolder(
                            binding
                        )
                    }
                    ListingEnum.PropertyMainType.COMMERCIAL -> {
                        val binding = ListItemGroupTransactionCommercialBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        CommercialSaleTransactionViewHolder(
                            binding
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("`propertyMainType` must not be null")
                    }
                }
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_loading_transaction, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return listItems.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HdbSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is NlpSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is LandedSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is CommercialSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
        }
    }

    private fun setupDisplayItemFullText(container: LinearLayout) {
        (0 until container.childCount).map {
            val child = container.getChildAt(it)
            if (child is TextView) {
                child.setOnLongClickListener {
                    ViewUtil.showMessage(child.text.toString())
                    true
                }
            }
        }
    }

    class HdbSaleTransactionViewHolder(val binding: ListItemGroupTransactionHdbBinding) :
        RecyclerView.ViewHolder(binding.root)

    class NlpSaleTransactionViewHolder(val binding: ListItemGroupTransactionNlpBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LandedSaleTransactionViewHolder(val binding: ListItemGroupTransactionLandedBinding) :
        RecyclerView.ViewHolder(binding.root)

    class CommercialSaleTransactionViewHolder(val binding: ListItemGroupTransactionCommercialBinding) :
        RecyclerView.ViewHolder(binding.root)
}