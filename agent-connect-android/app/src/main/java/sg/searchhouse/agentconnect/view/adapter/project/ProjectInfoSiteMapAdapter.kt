package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemProjectInfoSiteMapBinding
import sg.searchhouse.agentconnect.databinding.ListItemProjectInfoSiteMapEndBinding
import sg.searchhouse.agentconnect.databinding.ListItemProjectInfoSiteMapStartBinding
import sg.searchhouse.agentconnect.model.api.project.PhotoPO

class ProjectInfoSiteMapAdapter(val onClickListener: (items: List<PhotoPO>, position: Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<PhotoPO> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> {
                val binding = ListItemProjectInfoSiteMapBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SiteMapViewHolder(
                    binding
                )
            }
            VIEW_TYPE_START -> {
                val binding = ListItemProjectInfoSiteMapStartBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SiteMapStartViewHolder(
                    binding
                )
            }
            VIEW_TYPE_END -> {
                val binding = ListItemProjectInfoSiteMapEndBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SiteMapEndViewHolder(
                    binding
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SiteMapViewHolder -> {
                holder.binding.photoPO = items[position]
                holder.itemView.setOnClickListener {
                    onClickListener.invoke(items, position)
                }
            }
            is SiteMapStartViewHolder -> {
                holder.binding.photoPO = items[position]
                holder.itemView.setOnClickListener {
                    onClickListener.invoke(items, position)
                }
            }
            is SiteMapEndViewHolder -> {
                holder.binding.photoPO = items[position]
                holder.itemView.setOnClickListener {
                    onClickListener.invoke(items, position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_START
            items.size - 1 -> VIEW_TYPE_END
            else -> VIEW_TYPE_DEFAULT
        }
    }

    class SiteMapViewHolder(val binding: ListItemProjectInfoSiteMapBinding) :
        RecyclerView.ViewHolder(binding.root)

    class SiteMapStartViewHolder(val binding: ListItemProjectInfoSiteMapStartBinding) :
        RecyclerView.ViewHolder(binding.root)

    class SiteMapEndViewHolder(val binding: ListItemProjectInfoSiteMapEndBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_DEFAULT = 1
        const val VIEW_TYPE_START = 2
        const val VIEW_TYPE_END = 3
    }
}