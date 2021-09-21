package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistProfileCriteriaBinding
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class WatchlistProfileCriteriaAdapter(
    private val onCriteriaSelected: (item: WatchlistPropertyCriteriaPO) -> Unit,
    private val onCriteriaHiddenCheckChanged: (item: WatchlistPropertyCriteriaPO, isDisplay: Boolean) -> Unit
) :
    BaseListAdapter() {
    override fun updateListItems(newListItems: List<Any>) {
        listItems = newListItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CRITERIA -> {
                CriteriaViewHolder(
                    ListItemWatchlistProfileCriteriaBinding.inflate(
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
            is Loading -> VIEW_TYPE_LOADING
            else -> throw IllegalArgumentException("Invalid item class `${listItems[position].javaClass.name}`!")
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
                holder.binding.layoutContainer.setOnClickListener {
                    onCriteriaSelected.invoke(
                        criteria
                    )
                }
                holder.binding.cbVisibility.setOnClickListener {
                    val isChecked = holder.binding.cbVisibility.isChecked
                    it.post {
                        onCriteriaHiddenCheckChanged.invoke(criteria, !isChecked)
                    }
                }
                holder.binding.executePendingBindings()
            }
            else -> {
                // Do nothing
            }
        }
    }

    class CriteriaViewHolder(val binding: ListItemWatchlistProfileCriteriaBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_CRITERIA = 2
        private const val VIEW_TYPE_LOADING = 4
    }
}