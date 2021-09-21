package sg.searchhouse.agentconnect.view.adapter.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemWatchlistTransactionBinding
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse

class DashboardWatchlistTransactionAdapter constructor(private val transactions: List<TableListResponse.Transactions.Result>) :
    RecyclerView.Adapter<DashboardWatchlistTransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            ListItemWatchlistTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.binding.transaction = transactions[position]
        holder.binding.executePendingBindings()
    }

    class TransactionViewHolder(val binding: ListItemWatchlistTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)
}