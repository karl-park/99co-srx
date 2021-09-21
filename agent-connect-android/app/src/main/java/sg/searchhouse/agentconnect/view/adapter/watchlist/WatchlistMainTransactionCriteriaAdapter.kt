package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistMainTransactionCriteriaBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class WatchlistMainTransactionCriteriaAdapter(private val onCriteriaSelected: (item: WatchlistPropertyCriteriaPO) -> Unit) :
    BaseListAdapter() {
    override fun updateListItems(newListItems: List<Any>) {
        listItems = newListItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CRITERIA -> {
                CriteriaViewHolder(
                    ListItemWatchlistMainTransactionCriteriaBinding.inflate(
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
            else -> throw IllegalArgumentException("Invalid view type!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is WatchlistPropertyCriteriaPO -> VIEW_TYPE_CRITERIA
            else -> throw IllegalArgumentException("Invalid item class!")
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CriteriaViewHolder -> {
                val criteria = listItems[position] as WatchlistPropertyCriteriaPO
                holder.binding.criteria = criteria
                holder.binding.card.setOnClickQuickDelayListener {
                    onCriteriaSelected.invoke(criteria)
                }
                holder.binding.executePendingBindings()
            }
            else -> {
                // Do nothing
            }
        }
    }

    class CriteriaViewHolder(val binding: ListItemWatchlistMainTransactionCriteriaBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_CRITERIA = 2
        private const val VIEW_TYPE_LOADING = 4
    }
}