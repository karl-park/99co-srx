package sg.searchhouse.agentconnect.view.activity.base

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_listings_watchlist.*
import sg.searchhouse.agentconnect.dsl.widget.listenScrollToBottom
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.scrollToBottom
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.viewmodel.base.PaginatedApiBaseViewModel

abstract class PaginatedViewModelActivity<T : PaginatedApiBaseViewModel<*, *>, U : ViewDataBinding> :
    ViewModelActivity<T, U>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupList()
        observeListLiveData()
        if (isPreloadList()) {
            viewModel.loadFirstPage()
        }
    }

    private fun observeListLiveData() {
        viewModel.listItems.observeNotNull(this) { getAdapter().updateListItems(it) }

        viewModel.page.observeNotNull(this) {
            viewModel.performRequest()
            if (it > 1) {
                getAdapter().addListItem(Loading())
                list.scrollToBottom()
            }
        }
    }

    private fun setupList() {
        getList().setupLayoutManager()
        getList().adapter = getAdapter()
        getList().listenScrollToBottom { viewModel.maybeAddPage() }
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    abstract fun getList(): RecyclerView

    // NOTE Initialize adapter outside the scope
    abstract fun getAdapter(): BaseListAdapter

    // If true, no need to load list on child activity
    // Otherwise, you can always post-load by calling `viewModel.loadFirstPage()` on your child activity
    abstract fun isPreloadList(): Boolean
}