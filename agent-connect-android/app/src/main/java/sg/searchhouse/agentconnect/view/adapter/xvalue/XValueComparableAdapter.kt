package sg.searchhouse.agentconnect.view.adapter.xvalue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemXValueComparableBinding
import sg.searchhouse.agentconnect.model.api.xvalue.GetValuationDetailResponse

class XValueComparableAdapter :
    RecyclerView.Adapter<XValueComparableAdapter.ComparableViewHolder>() {

    var listItems: List<GetValuationDetailResponse.Data.Comparable> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ComparableViewHolder {
        return ComparableViewHolder(
            ListItemXValueComparableBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: ComparableViewHolder, position: Int) {
        holder.binding.comparable = listItems[position]
    }

    class ComparableViewHolder(val binding: ListItemXValueComparableBinding) :
        RecyclerView.ViewHolder(binding.root)
}