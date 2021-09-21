package sg.searchhouse.agentconnect.view.adapter.xvalue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemXValueLatestTransactionBinding
import sg.searchhouse.agentconnect.model.api.xvalue.HomeReportTransaction

class XValueLatestTransactionAdapter :
    RecyclerView.Adapter<XValueLatestTransactionAdapter.HomeReportTransactionViewHolder>() {

    var listItems: List<HomeReportTransaction> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeReportTransactionViewHolder {
        return HomeReportTransactionViewHolder(
            ListItemXValueLatestTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: HomeReportTransactionViewHolder, position: Int) {
        holder.binding.homeReportTransaction = listItems[position]
    }

    class HomeReportTransactionViewHolder(val binding: ListItemXValueLatestTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)
}