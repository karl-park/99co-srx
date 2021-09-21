package sg.searchhouse.agentconnect.view.helper.listing.create

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.view.adapter.listing.create.PostListingImagesAdapter
import java.util.*

class ItemTouchHelperCallback(val adapter: PostListingImagesAdapter) :
    ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.START or ItemTouchHelper.END,
        0
    ) {

    private var dragFrom: Int = -1
    private var dragTo: Int = -1

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }

        if (viewHolder.adapterPosition != -1 && target.adapterPosition != -1) {

            //assign position to variables
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            if (dragFrom == -1) {
                dragFrom = fromPosition
            }
            dragTo = toPosition

            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(adapter.items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(adapter.items, i, i - 1)
                }
            }
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            adapter.notifyItemChanged(viewHolder.adapterPosition)
            adapter.notifyItemChanged(target.adapterPosition)

        }

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        println("On Swiped -> Do nothing")
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    adapter.onItemDropped(fromPosition = dragFrom, toPosition = dragTo)
                    dragFrom = -1
                    dragTo = -1
                }
            }
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder?.run { adapter.onItemSelected(this) }
            }
            else -> {
                //do nothing
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.clearView(viewHolder)
    }

}