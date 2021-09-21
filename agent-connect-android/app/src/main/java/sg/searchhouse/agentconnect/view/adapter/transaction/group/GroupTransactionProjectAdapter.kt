package sg.searchhouse.agentconnect.view.adapter.transaction.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemTransactionProjectBinding
import sg.searchhouse.agentconnect.model.api.transaction.PaginatedProjectListResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class GroupTransactionProjectAdapter constructor(
    val onClickListener: ((project: PaginatedProjectListResponse.Projects.Project) -> Unit)
) :
    BaseListAdapter() {

    override fun updateListItems(newListItems: List<Any>) {
        // TODO Update items, update when implemented paginated view model
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROJECT -> {
                val binding = ListItemTransactionProjectBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TransactionProjectViewHolder(
                    binding
                )
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_indicator, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type!")
        }
    }

    override fun getItemCount(): Int {
        return listItems.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionProjectViewHolder -> {
                val project = listItems[position] as PaginatedProjectListResponse.Projects.Project
                holder.binding.project = project
                holder.itemView.setOnClickListener { onClickListener.invoke(project) }
                holder.binding.executePendingBindings()
            }
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is PaginatedProjectListResponse.Projects.Project -> VIEW_TYPE_PROJECT
            is Loading -> VIEW_TYPE_LOADING
            else -> throw IllegalArgumentException("Invalid list item class!")
        }
    }

    class TransactionProjectViewHolder(val binding: ListItemTransactionProjectBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_PROJECT = 1
        const val VIEW_TYPE_LOADING = 2
    }
}