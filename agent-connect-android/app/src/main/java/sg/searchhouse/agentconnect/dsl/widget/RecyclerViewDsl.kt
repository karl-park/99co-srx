package sg.searchhouse.agentconnect.dsl.widget

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.util.ViewUtil

fun RecyclerView.scrollToBottom() = run {
    val itemCount = adapter?.itemCount ?: 0
    if (itemCount > 0) {
        scrollToPosition(itemCount - 1)
    }
}

// Quick setup linear layout manager
// Orientation: RecyclerView.HORIZONTAL or RecyclerView.VERTICAL
fun RecyclerView.setupLayoutManager(orientation: Int = RecyclerView.VERTICAL) = run {
    val layoutManager = LinearLayoutManager(context)
    layoutManager.orientation = orientation
    this.layoutManager = layoutManager
}

fun RecyclerView.listenScrollToBottom(reachBottom: () -> Unit) =
    ViewUtil.listenVerticalScrollEnd(this, reachBottom = {
        reachBottom.invoke()
    })