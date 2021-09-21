package sg.searchhouse.agentconnect.view.adapter.transaction.caveat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemTransactionCaveatBinding
import sg.searchhouse.agentconnect.model.api.transaction.TowerViewForLastSoldTransactionResponse

class TransactionCaveatAdapter :
    RecyclerView.Adapter<TransactionCaveatAdapter.TransactionCaveatViewHolder>() {

    var items: List<TowerViewForLastSoldTransactionResponse.PreviousUnitTransactionsItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionCaveatViewHolder {
        val binding = ListItemTransactionCaveatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionCaveatViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TransactionCaveatViewHolder, position: Int) {
        holder.binding.caveat = items[position]
    }

    class TransactionCaveatViewHolder(val binding: ListItemTransactionCaveatBinding) :
        RecyclerView.ViewHolder(binding.root)
}