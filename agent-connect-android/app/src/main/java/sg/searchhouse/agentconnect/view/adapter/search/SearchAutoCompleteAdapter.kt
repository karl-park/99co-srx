package sg.searchhouse.agentconnect.view.adapter.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemSearchLocationBinding
import sg.searchhouse.agentconnect.databinding.ListItemSearchLocationTypeBinding
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO

class SearchAutoCompleteAdapter(private val onItemClickListener: ((LocationEntryPO) -> Unit)? = null): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<Any> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ENTRY -> {
                val binding = DataBindingUtil.inflate<ListItemSearchLocationBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_search_location,
                    parent,
                    false
                )
                EntryViewHolder(binding)
            }
            VIEW_TYPE_ENTRY_TYPE -> {
                val binding = DataBindingUtil.inflate<ListItemSearchLocationTypeBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_search_location_type,
                    parent,
                    false
                )
                EntryTypeViewHolder(binding)
            }
            else -> throw Throwable("Wrong view type")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ENTRY -> {
                val entryViewHolder = holder as EntryViewHolder
                val entryPO = items[position] as LocationEntryPO
                entryViewHolder.binding.locationEntryPO = entryPO
                val hasHeaderBelow = items.size > position + 1 && items[position + 1] is EntryType
                val isLastItem = position == items.size - 1
                entryViewHolder.binding.isLast = hasHeaderBelow || isLastItem
                entryViewHolder.binding.root.setOnClickListener {
                    onItemClickListener?.invoke(entryPO)
                }
            }
            VIEW_TYPE_ENTRY_TYPE -> {
                val entryTypeViewHolder = holder as EntryTypeViewHolder
                entryTypeViewHolder.binding.entryType = items[position] as EntryType
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is LocationEntryPO -> {
                VIEW_TYPE_ENTRY
            }
            is EntryType -> {
                VIEW_TYPE_ENTRY_TYPE
            }
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    class EntryType(val name: String) {
        fun label(): String {
            return name.replace("_", " ")
        }
    }

    class EntryTypeViewHolder(val binding: ListItemSearchLocationTypeBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EntryViewHolder(val binding: ListItemSearchLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_ENTRY = 1
        const val VIEW_TYPE_ENTRY_TYPE = 2
    }
}