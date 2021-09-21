package sg.searchhouse.agentconnect.view.fragment.transaction.group

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_group_transaction_projects.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentGroupTransactionProjectsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.scrollToBottom
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.transaction.GetProjectTransactionsEvent
import sg.searchhouse.agentconnect.event.transaction.LockFragmentScrollEvent
import sg.searchhouse.agentconnect.event.transaction.UnlockActivityScrollEvent
import sg.searchhouse.agentconnect.event.transaction.UpdateGroupProjectsEvent
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.transaction.group.GroupTransactionProjectAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.transaction.TransactionProjectsViewModel

class GroupTransactionProjectsFragment :
    ViewModelFragment<TransactionProjectsViewModel, FragmentGroupTransactionProjectsBinding>() {

    companion object {
        fun newInstance() =
            GroupTransactionProjectsFragment()
    }

    private val adapter =
        GroupTransactionProjectAdapter(
            onClickListener = { project ->
                RxBus.publish(
                    GetProjectTransactionsEvent(
                        "${project.projectId}",
                        project.name,
                        "",
                        project.propertyType
                    )
                )
            })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupListeners()
        listenRxBuses()
        observeLiveData()
    }

    private fun setupListeners() {
        layout_root.setOnClickListener { maybeUnlockActivityScroll() }
    }

    private fun observeLiveData() {
        viewModel.listItems.observe(viewLifecycleOwner) {
            adapter.listItems = it ?: emptyList()
            adapter.notifyDataSetChanged()
        }
        viewModel.transactionSearchCriteriaV2VO.observeNotNull(viewLifecycleOwner) {
            viewModel.page.postValue(1)
        }
        viewModel.page.observeNotNull(viewLifecycleOwner) {
            if (adapter.isLoading()) {
                return@observeNotNull
            }
            if (it > 1) {
                adapter.listItems += Loading()
                adapter.notifyDataSetChanged()
                list.scrollToBottom()
            }
            viewModel.performRequest()
        }
    }

    private fun listenRxBuses() {
        listenRxBus(LockFragmentScrollEvent::class.java) {
            list.isNestedScrollingEnabled = !it.isLock
        }
        listenRxBus(UpdateGroupProjectsEvent::class.java) {
            viewModel.transactionSearchCriteriaV2VO.postValue(it.transactionSearchCriteriaV2VO)
        }
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
        layout_swipe_refresh.setOnRefreshListener {
            maybeUnlockActivityScroll()
            layout_swipe_refresh.isRefreshing = false
        }
        ViewUtil.listenVerticalScrollEnd(list, reachBottom = {
            viewModel.maybeAddPage()
        })
    }

    private fun maybeUnlockActivityScroll() {
        if (list.isNestedScrollingEnabled) {
            publishRxBus(UnlockActivityScrollEvent())
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_group_transaction_projects
    }

    override fun getViewModelClass(): Class<TransactionProjectsViewModel> {
        return TransactionProjectsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}
