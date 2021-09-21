package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.*
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.adapter.transaction.group.GroupTransactionAdapter

class ProjectTransactionAdapter(
    val propertySubType: ListingEnum.PropertySubType,
    val ownershipType: ListingEnum.OwnershipType
) :
    BaseListAdapter() {

    companion object {
        const val VIEW_TYPE_TRANSACTION = 1
        const val VIEW_TYPE_LOADING = 2
    }

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
        // TODO Use when free
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TRANSACTION -> {
                when {
                    PropertyTypeUtil.isHDB(propertySubType.type) -> {
                        val binding = ListItemProjectTransactionRealTimeHdbBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        ProjectHdbViewHolder(
                            binding
                        )
                    }
                    PropertyTypeUtil.isCondo(propertySubType.type) -> {
                        val binding = ListItemProjectTransactionRealTimeNlpBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        ProjectNlpViewHolder(
                            binding
                        )
                    }
                    PropertyTypeUtil.isLanded(propertySubType.type) -> {
                        val binding = ListItemProjectTransactionRealTimeLandedBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        ProjectLandedViewHolder(
                            binding
                        )
                    }
                    PropertyTypeUtil.isCommercial(propertySubType.type) -> {
                        val binding = ListItemProjectTransactionRealTimeCommercialBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        ProjectCommercialViewHolder(
                            binding
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("Invalid property sub type ${propertySubType.type} om `ProjectTransactionAdapter`!")
                    }
                }
            }
            VIEW_TYPE_LOADING -> {
                val binding = ListItemLoadingTransactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GroupTransactionAdapter.LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return listItems.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProjectHdbViewHolder -> {
                val transaction = listItems[position]
                holder.binding.transaction = transaction as TableListResponse.Transactions.Result
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
            }
            is ProjectNlpViewHolder -> {
                val transaction = listItems[position]
                holder.binding.transaction = transaction as TableListResponse.Transactions.Result
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
            }
            is ProjectLandedViewHolder -> {
                val transaction = listItems[position]
                holder.binding.transaction = transaction as TableListResponse.Transactions.Result
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
            }
            is ProjectCommercialViewHolder -> {
                val transaction = listItems[position]
                holder.binding.transaction = transaction as TableListResponse.Transactions.Result
                holder.binding.ownershipType = ownershipType
                holder.binding.scale = scale
            }
            is LoadingViewHolder -> {
                holder.binding.scale = scale
                holder.binding.executePendingBindings()
            }
        }
    }

    class ProjectHdbViewHolder(val binding: ListItemProjectTransactionRealTimeHdbBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ProjectNlpViewHolder(val binding: ListItemProjectTransactionRealTimeNlpBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ProjectLandedViewHolder(val binding: ListItemProjectTransactionRealTimeLandedBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ProjectCommercialViewHolder(val binding: ListItemProjectTransactionRealTimeCommercialBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LoadingViewHolder(val binding: ListItemLoadingTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)
}