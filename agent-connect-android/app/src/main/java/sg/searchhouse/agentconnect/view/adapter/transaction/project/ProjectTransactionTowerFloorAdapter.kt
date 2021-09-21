package sg.searchhouse.agentconnect.view.adapter.transaction.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.GridItemTransactionTowerFloorBinding

class ProjectTransactionTowerFloorAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<TowerFloor> =
        emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = GridItemTransactionTowerFloorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FloorViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val floor = items[position]
        val floorViewHolder = holder as FloorViewHolder
        floorViewHolder.binding.floor = floor.floorNumber
    }

    class FloorViewHolder(val binding: GridItemTransactionTowerFloorBinding) :
        RecyclerView.ViewHolder(binding.root)

    class TowerFloor(val floorNumber: String)
}