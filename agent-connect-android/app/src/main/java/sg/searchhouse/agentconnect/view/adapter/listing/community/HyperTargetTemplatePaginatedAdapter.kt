package sg.searchhouse.agentconnect.view.adapter.listing.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutLoadingBinding
import sg.searchhouse.agentconnect.databinding.ListItemHyperTargetTemplateBinding
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder
import kotlin.math.max

class HyperTargetTemplatePaginatedAdapter(private val onClickListener: (hyperTargetTemplatePO: CommunityHyperTargetTemplatePO) -> Unit) :
    BaseListAdapter() {
    var memberCounts = listOf<Int?>()
    override fun updateListItems(newListItems: List<Any>) {
        listItems = newListItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TEMPLATE -> {
                HyperTargetTemplateViewHolder(
                    ListItemHyperTargetTemplateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_LOADING -> {
                LoadingViewHolder(
                    LayoutLoadingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ).root
                )
            }
            else -> throw IllegalArgumentException("Invalid view type!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is CommunityHyperTargetTemplatePO -> VIEW_TYPE_TEMPLATE
            is Loading -> VIEW_TYPE_LOADING
            else -> throw IllegalArgumentException("Invalid list item!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HyperTargetTemplateViewHolder) {
            val listItem = listItems.getOrNull(position)
            if (listItem is CommunityHyperTargetTemplatePO) {
                holder.binding.hyperTargetTemplate = listItem
                holder.binding.memberCountLabel =
                    memberCounts.getOrNull(position)?.getLabel(holder.binding.root.context)
                holder.binding.container.setOnClickListener { onClickListener.invoke(listItem) }
                holder.binding.executePendingBindings()
            }
        }
    }

    override fun getItemCount(): Int {
        return max(listItems.size, memberCounts.size)
    }

    private fun Int.getLabel(context: Context): String = run {
        context.resources.getQuantityString(
            R.plurals.label_address_count,
            this,
            NumberUtil.formatThousand(this)
        )
    }

    class HyperTargetTemplateViewHolder(val binding: ListItemHyperTargetTemplateBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_TEMPLATE = 2
        private const val VIEW_TYPE_LOADING = 3
    }
}