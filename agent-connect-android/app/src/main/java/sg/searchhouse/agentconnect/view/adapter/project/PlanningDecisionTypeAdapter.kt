package sg.searchhouse.agentconnect.view.adapter.project

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_transaction_tower_block.view.*
import sg.searchhouse.agentconnect.databinding.ListItemPlanningDecisionTypeBinding
import sg.searchhouse.agentconnect.databinding.ListItemPlanningDecisionTypeHeaderBinding
import sg.searchhouse.agentconnect.model.api.common.NameValuePO

class PlanningDecisionTypeAdapter constructor(var onClickListener: ((decision: NameValuePO) -> Unit)? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Any> = emptyList()

    var selectedItem: NameValuePO? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding =
                    ListItemPlanningDecisionTypeHeaderBinding.inflate(LayoutInflater.from(parent.context))
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_DECISION_TYPE -> {
                val binding =
                    ListItemPlanningDecisionTypeBinding.inflate(LayoutInflater.from(parent.context))
                DecisionTypeViewHolder(
                    binding
                )
            }
            else -> throw IllegalArgumentException("Invalid view type of PlanningDecisionTypeAdapter")
        }
    }

    fun setListItems(decisions: List<NameValuePO>?) {
        items = if (decisions?.isNotEmpty() == true) {
            listOf(Header()) + decisions
        } else {
            emptyList()
        }
        notifyDataSetChanged()
    }

    fun getFirstItem(): NameValuePO? {
        return when (val firstItem = items.getOrNull(1)) {
            null -> null
            is NameValuePO -> firstItem
            else -> throw IllegalArgumentException("Wrong cast of decision type item")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Header -> VIEW_TYPE_HEADER
            is NameValuePO -> VIEW_TYPE_DECISION_TYPE
            else -> throw IllegalArgumentException("Invalid item type of PlanningDecisionTypeAdapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DecisionTypeViewHolder -> {
                val decisionType = items[position] as NameValuePO
                holder.binding.decisionType = decisionType
                holder.binding.isSelected =
                    TextUtils.equals(decisionType.value, selectedItem?.value)
                holder.binding.isLast = position == items.size - 1
                holder.binding.root.button.setOnClickListener { onClickListener?.invoke(decisionType) }
                holder.binding.executePendingBindings()
            }
        }
    }

    class DecisionTypeViewHolder(val binding: ListItemPlanningDecisionTypeBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(val binding: ListItemPlanningDecisionTypeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class Header

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_DECISION_TYPE = 2
    }
}