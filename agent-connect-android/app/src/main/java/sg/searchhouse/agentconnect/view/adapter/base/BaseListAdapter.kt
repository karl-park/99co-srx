package sg.searchhouse.agentconnect.view.adapter.base

import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.model.app.Loading

abstract class BaseListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listItems = emptyList<Any>()

    // Update list items in adapter
    // By default `this.listItems = newListItems`
    // Can customise when necessary
    // Note:  Call `notifyDataSetChanged` after changes made
    abstract fun updateListItems(newListItems: List<Any>)

    fun addListItem(newListItem: Any) {
        updateListItems(listItems.filter { it !is Loading } + newListItem)
    }

    fun addListItems(newListItems: List<Any>) {
        updateListItems(listItems.filter { it !is Loading } + newListItems)
    }

    fun isLoading(): Boolean {
        return listItems.lastOrNull() is Loading
    }
}