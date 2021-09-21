package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.GridItemProjectDirectoryEndBinding
import sg.searchhouse.agentconnect.databinding.GridItemProjectDirectoryStartBinding
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchResultPO

class ProjectDirectoryAdapter(private val onClickItem: ((TransactionSearchResultPO) -> Unit)) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var projects = listOf<TransactionSearchResultPO>()

    companion object {
        private const val VIEW_TYPE_PROJECT_DIRECTORY_START = 0
        private const val VIEW_TYPE_PROJECT_DIRECTORY_END = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROJECT_DIRECTORY_START -> {
                GridItemProjectDirectoryStartViewHolder(
                    GridItemProjectDirectoryStartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_PROJECT_DIRECTORY_END -> {
                GridItemProjectDirectoryEndViewHolder(
                    GridItemProjectDirectoryEndBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("Wrong view type in project directory adapter")
        }
    }

    override fun getItemCount(): Int {
        return projects.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridItemProjectDirectoryStartViewHolder -> {
                val project = projects[position]
                holder.binding.project = project
                holder.itemView.setOnClickListener {
                    onClickItem.invoke(project)
                }
                holder.binding.executePendingBindings()
            }
            is GridItemProjectDirectoryEndViewHolder -> {
                val project = projects[position]
                holder.binding.project = project
                holder.itemView.setOnClickListener {
                    onClickItem.invoke(project)
                }
                holder.binding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position % 2 == 0 -> VIEW_TYPE_PROJECT_DIRECTORY_START
            else -> VIEW_TYPE_PROJECT_DIRECTORY_END
        }
    }

    fun updateProjectList(items: List<TransactionSearchResultPO>) {
        this.projects = items
        notifyDataSetChanged()
    }

    class GridItemProjectDirectoryStartViewHolder(val binding: GridItemProjectDirectoryStartBinding) :
        RecyclerView.ViewHolder(binding.root)

    class GridItemProjectDirectoryEndViewHolder(val binding: GridItemProjectDirectoryEndBinding) :
        RecyclerView.ViewHolder(binding.root)

}