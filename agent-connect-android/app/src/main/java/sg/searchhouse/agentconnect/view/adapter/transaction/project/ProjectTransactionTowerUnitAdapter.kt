package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.GridItemTransactionTowerUnitBinding
import sg.searchhouse.agentconnect.databinding.GridItemTransactionTowerUnitEmptyBinding
import sg.searchhouse.agentconnect.model.api.transaction.TowerViewForLastSoldTransactionResponse
import sg.searchhouse.agentconnect.model.app.Empty

class ProjectTransactionTowerUnitAdapter constructor(val onClickListener: ((unit: TowerViewForLastSoldTransactionResponse.UnitsItem) -> Unit)) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<Any> =
        emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_UNIT -> {
                val binding = GridItemTransactionTowerUnitBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                UnitViewHolder(
                    binding
                )
            }
            VIEW_TYPE_EMPTY -> {
                val binding = GridItemTransactionTowerUnitEmptyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                EmptyViewHolder(
                    binding
                )
            }
            else -> throw IllegalArgumentException("Wrong view type in tower unit adapter")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TowerViewForLastSoldTransactionResponse.UnitsItem -> VIEW_TYPE_UNIT
            is Empty -> VIEW_TYPE_EMPTY
            else -> throw IllegalArgumentException("Invalid item class in tower unit adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_UNIT -> {
                val unit = items[position] as TowerViewForLastSoldTransactionResponse.UnitsItem
                val unitViewHolder = holder as UnitViewHolder
                unitViewHolder.binding.unit = unit
                holder.itemView.setOnClickListener { onClickListener.invoke(unit) }
            }
        }
    }

    class UnitViewHolder(val binding: GridItemTransactionTowerUnitBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EmptyViewHolder(val binding: GridItemTransactionTowerUnitEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_EMPTY = 0
        const val VIEW_TYPE_UNIT = 1
    }
}