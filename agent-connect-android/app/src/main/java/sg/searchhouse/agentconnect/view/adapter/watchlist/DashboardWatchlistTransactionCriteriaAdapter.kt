package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistTransactionCriteriaBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus

class DashboardWatchlistTransactionCriteriaAdapter constructor(
    private val onExpand: (WatchlistPropertyCriteriaPO) -> Unit,
    private val onClickViewAll: (WatchlistPropertyCriteriaPO) -> Unit
) :
    RecyclerView.Adapter<DashboardWatchlistTransactionCriteriaAdapter.CriteriaViewHolder>() {

    var watchLists = listOf<WatchlistPropertyCriteriaPO>()
    var transactionsMap = mapOf<Int, List<TableListResponse.Transactions.Result>>()

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriteriaViewHolder {
        return CriteriaViewHolder(
            ListItemWatchlistTransactionCriteriaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return watchLists.size
    }

    override fun onBindViewHolder(holder: CriteriaViewHolder, position: Int) {
        val criteria = watchLists[position]
        val criteriaId = criteria.id ?: throw IllegalStateException("Criteria must have ID here!")
        val transactions = transactionsMap[criteriaId]
        // If transactions null, status = LOADING, if empty, status = FAIL
        val itemsStatus = transactions?.run {
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
        transactions?.run { populateTransactions(holder, this) }
        holder.binding.executePendingBindings()
    }

    private fun populateTransactions(
        holder: CriteriaViewHolder,
        transactions: List<TableListResponse.Transactions.Result>
    ) {
        val adapter = DashboardWatchlistTransactionAdapter(transactions)
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
            onExpand.invoke(watchLists[position])
        }
        notifyItemChanged(position)
    }

    class CriteriaViewHolder(val binding: ListItemWatchlistTransactionCriteriaBinding) :
        RecyclerView.ViewHolder(binding.root)
}