package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemListingLatestTransactionBinding
import sg.searchhouse.agentconnect.model.api.xvalue.HomeReportTransaction

class ListingLatestTransactionAdapter :
    RecyclerView.Adapter<ListingLatestTransactionAdapter.TransactionViewHolder>() {

    var listItems: List<HomeReportTransaction> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        return TransactionViewHolder(
            ListItemListingLatestTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.binding.transaction = listItems[position]
    }

    class TransactionViewHolder(val binding: ListItemListingLatestTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)
}