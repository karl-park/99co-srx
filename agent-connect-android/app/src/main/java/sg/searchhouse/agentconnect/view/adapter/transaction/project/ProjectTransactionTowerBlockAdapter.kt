package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_transaction_tower_block.view.*
import sg.searchhouse.agentconnect.databinding.ListItemTransactionTowerBlockBinding
import sg.searchhouse.agentconnect.model.api.transaction.TowerViewForLastSoldTransactionResponse

class ProjectTransactionTowerBlockAdapter constructor(var onClickListener: ((block: TowerViewForLastSoldTransactionResponse.BlocksItem) -> Unit)? = null) :
    RecyclerView.Adapter<ProjectTransactionTowerBlockAdapter.BlockViewHolder>() {
    var items: List<TowerViewForLastSoldTransactionResponse.BlocksItem> = emptyList()
    var selectedItem: TowerViewForLastSoldTransactionResponse.BlocksItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val binding = ListItemTransactionTowerBlockBinding.inflate(LayoutInflater.from(parent.context))
        return BlockViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = items[position]
        holder.binding.block = block
        holder.binding.isSelected = TextUtils.equals(block.block, selectedItem?.block)
        holder.binding.isLast = position == items.size - 1
        holder.binding.root.button.setOnClickListener { onClickListener?.invoke(block) }
        holder.binding.executePendingBindings()
    }

    class BlockViewHolder(val binding: ListItemTransactionTowerBlockBinding) :
        RecyclerView.ViewHolder(binding.root)
}