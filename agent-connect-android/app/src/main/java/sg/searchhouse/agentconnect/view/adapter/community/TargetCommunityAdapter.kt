package sg.searchhouse.agentconnect.view.adapter.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemTargetCommunityBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.community.CommunityTopDownPO
import sg.searchhouse.agentconnect.event.community.RemoveTargetCommunityEvent

class TargetCommunityAdapter : RecyclerView.Adapter<TargetCommunityAdapter.CommunityViewHolder>() {
    var items: List<CommunityTopDownPO> = listOf()
    var canRemoveItem: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ListItemTargetCommunityBinding.inflate(LayoutInflater.from(parent.context))
        return CommunityViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        val item = items[position]
        holder.binding.community = item
        holder.binding.canRemoveItem = canRemoveItem
        holder.binding.isStart = position == 0
        holder.binding.isEnd = position == items.size - 1
        holder.binding.btnRemove.setOnClickListener {
            RxBus.publish(RemoveTargetCommunityEvent(item.getCommunityId()))
        }
    }

    class CommunityViewHolder(val binding: ListItemTargetCommunityBinding) :
        RecyclerView.ViewHolder(binding.root)
}