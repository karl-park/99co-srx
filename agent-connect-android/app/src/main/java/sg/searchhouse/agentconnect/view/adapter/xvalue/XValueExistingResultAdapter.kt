package sg.searchhouse.agentconnect.view.adapter.xvalue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemExistingXValueBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.model.api.xvalue.XValue
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class XValueExistingResultAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listItems: List<Any> = emptyList()

    var onSelectItem: ((XValue) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_RESULT -> {
                XValueResultViewHolder(
                    ListItemExistingXValueBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
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
        return listItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is XValueResultViewHolder -> {
                val result = listItems[position] as XValue
                holder.binding.result = result
                holder.binding.card.setOnClickQuickDelayListener {
                    onSelectItem?.invoke(result)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position]) {
            is XValue -> VIEW_TYPE_RESULT
            is Loading -> VIEW_TYPE_LOADING
            else -> throw IllegalArgumentException("Invalid list item class!")
        }
    }

    class XValueResultViewHolder(val binding: ListItemExistingXValueBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_RESULT = 2
    }
}