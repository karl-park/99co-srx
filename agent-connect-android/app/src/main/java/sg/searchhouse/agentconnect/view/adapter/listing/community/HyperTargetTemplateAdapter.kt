package sg.searchhouse.agentconnect.view.adapter.listing.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemHyperTargetTemplateBinding
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.util.NumberUtil
import kotlin.math.max

class HyperTargetTemplateAdapter(private val onClickListener: (hyperTargetTemplatePO: CommunityHyperTargetTemplatePO) -> Unit) :
    RecyclerView.Adapter<HyperTargetTemplateAdapter.HyperTargetTemplateViewHolder>() {
    var templates = listOf<CommunityHyperTargetTemplatePO>()
    var memberCounts = listOf<Int?>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HyperTargetTemplateViewHolder {
        return HyperTargetTemplateViewHolder(
            ListItemHyperTargetTemplateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return max(templates.size, memberCounts.size)
    }

    override fun onBindViewHolder(holder: HyperTargetTemplateViewHolder, position: Int) {
        holder.binding.hyperTargetTemplate = templates.getOrNull(position)
        holder.binding.memberCountLabel =
            memberCounts.getOrNull(position)?.getLabel(holder.binding.root.context)
        holder.binding.container.setOnClickListener { onClickListener.invoke(templates[position]) }
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
}