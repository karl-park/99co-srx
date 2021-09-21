package sg.searchhouse.agentconnect.view.adapter.transaction.group

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.*
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter

class GroupTransactionAdapter(var ownershipType: ListingEnum.OwnershipType) :
    BaseListAdapter() {
    companion object {
        const val VIEW_TYPE_TRANSACTION = 1
        const val VIEW_TYPE_LOADING = 2
    }

    // TODO Apply this
    var propertyMainType: ListingEnum.PropertyMainType? = null

    var scale: Float = 1.0f

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
        // TODO Implement later
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
                val binding = ListItemLoadingTransactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
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
                holder.binding.scale = scale
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is NlpSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is LandedSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is CommercialSaleTransactionViewHolder -> {
                val transaction = listItems[position] as TableListResponse.Transactions.Result
                holder.binding.transaction = transaction
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
                val container = holder.itemView as LinearLayout
                setupDisplayItemFullText(container)
                holder.binding.executePendingBindings()
            }
            is LoadingViewHolder -> {
                holder.binding.scale = scale
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

    class LoadingViewHolder(val binding: ListItemLoadingTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)
}