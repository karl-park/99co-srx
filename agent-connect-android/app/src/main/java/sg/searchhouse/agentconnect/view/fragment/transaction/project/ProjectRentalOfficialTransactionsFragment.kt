package sg.searchhouse.agentconnect.view.fragment.transaction.project

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_project_transactions.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.FragmentProjectRentalOfficialTransactionsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.transaction.TransactionListItem
import sg.searchhouse.agentconnect.event.transaction.LockFragmentScrollEvent
import sg.searchhouse.agentconnect.event.transaction.RequestProjectTransactionsCsvEvent
import sg.searchhouse.agentconnect.event.transaction.SendProjectTransactionsCsvEvent
import sg.searchhouse.agentconnect.event.transaction.UnlockActivityScrollEvent
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectRentalOfficialHdbTransactionAdapter
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectRentalOfficialTransactionAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.helper.transaction.TransactionsHorizontalSwipeIndicatorHelper
import sg.searchhouse.agentconnect.viewmodel.fragment.transaction.ProjectRentalOfficialTransactionsViewModel

class ProjectRentalOfficialTransactionsFragment :
    ViewModelFragment<ProjectRentalOfficialTransactionsViewModel, FragmentProjectRentalOfficialTransactionsBinding>() {

    companion object {
        private const val ARGUMENT_PROJECT_ID = "ARGUMENT_PROJECT_ID"
        private const val ARGUMENT_IS_HDB = "ARGUMENT_IS_HDB"

        fun newInstance(projectId: Int, isHdb: Boolean): ProjectRentalOfficialTransactionsFragment {
            val fragment = ProjectRentalOfficialTransactionsFragment()
            val bundle = Bundle()
            bundle.putInt(ARGUMENT_PROJECT_ID, projectId)
            bundle.putBoolean(ARGUMENT_IS_HDB, isHdb)
            fragment.arguments = bundle
            return fragment
        }
    }

    // TODO: Refactor
    private var genericAdapter = ProjectRentalOfficialTransactionAdapter()
    private var hdbAdapter = ProjectRentalOfficialHdbTransactionAdapter()

    private var projectId: Int = 0
    private var isHdb: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArguments()
    }

    private fun setupArguments() {
        projectId = arguments?.getInt(ARGUMENT_PROJECT_ID)
            ?: throw IllegalArgumentException("Missing project ID argument")
        isHdb = arguments?.getBoolean(ARGUMENT_IS_HDB)
            ?: throw IllegalArgumentException("Missing project ID argument")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModelArguments()
        setupList()
        listenRxBuses()
        observeLiveData()
        setOnClickListeners()
        viewModel.performRequest()
    }

    private fun setOnClickListeners() {
        layout_empty.setOnClickListener {
            maybeUnlockActivityScroll()
        }
        layout_loading_fail_error.setOnClickListener {
            maybeUnlockActivityScroll()
        }
    }

    private fun setupViewModelArguments() {
        viewModel.isHdb.postValue(isHdb)
        viewModel.projectId = projectId
    }

    private fun observeLiveData() {
        viewModel.listItems.observe(viewLifecycleOwner) {
            if (isHdb) {
                hdbAdapter.items = it ?: emptyList()
                hdbAdapter.notifyDataSetChanged()
            } else {
                genericAdapter.items = it ?: emptyList()
                genericAdapter.notifyDataSetChanged()
            }
            scroll_view_horizontal.scrollX = 0
            maybeShowHorizontalSwipeIndicator()
            maybeRescale()
        }
        viewModel.scale.observeNotNull(this) {
            when (viewModel.isHdb.value) {
                true -> {
                    hdbAdapter.scale = it
                    hdbAdapter.notifyDataSetChanged()
                }
                else -> {
                    genericAdapter.scale = it
                    genericAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun maybeRescale() {
        list.postDelayed {
            val listWidth = list?.layoutManager?.width ?: return@postDelayed
            val mActivity = activity ?: return@postDelayed
            val screenWidth = ViewUtil.getScreenWidth(mActivity)
            viewModel.maybeRescale(listWidth, screenWidth)
        }
    }

    private fun maybeShowHorizontalSwipeIndicator() {
        if (list.isNestedScrollingEnabled) {
            TransactionsHorizontalSwipeIndicatorHelper.maybeShowProjectIndicator(
                layout_indicate_swipe
            )
        }
    }

    override fun onResume() {
        super.onResume()
        scroll_view_horizontal.scrollX = 0
    }

    private fun listenRxBuses() {
        listenRxBus(LockFragmentScrollEvent::class.java) {
            list.isNestedScrollingEnabled = !it.isLock
            maybeShowHorizontalSwipeIndicator()
        }
        listenRxBus(RequestProjectTransactionsCsvEvent::class.java) {
            if (isResumed) {
                sendExportCsvToActivity(it.isLimited)
            }
        }
    }

    private fun sendExportCsvToActivity(isLimited: Boolean) {
        val thisContext = context ?: return
        val listItems = if (!isLimited) {
            viewModel.listItems.value ?: emptyList()
        } else {
            (viewModel.listItems.value
                ?: emptyList()).take(AppConstant.MAX_LIMIT_EXPORT_TRANSACTIONS)
        }
        val csvContent =
            getTransactionsCsvContent(thisContext, listItems)
        RxBus.publish(SendProjectTransactionsCsvEvent(csvContent))
    }

    private fun getTransactionsCsvContent(
        context: Context,
        transactionListItems: List<TransactionListItem>
    ): String {
        return when (viewModel.isHdb.value) {
            true -> {
                transactionListItems.getCustomCsvContent(title = {
                    TransactionListItem.getProjectOfficialHdbCsvTitle(context)
                }, content = {
                    it.getProjectOfficialHdbCsvContent()
                })
            }
            else -> {
                transactionListItems.getCustomCsvContent(title = {
                    TransactionListItem.getProjectOfficialGenericCsvTitle(context)
                }, content = {
                    it.getProjectOfficialNlpCsvContent()
                })
            }
        }
    }

    private fun List<TransactionListItem>.getCustomCsvContent(
        title: () -> String,
        content: (TransactionListItem) -> String
    ) = run {
        val titleResult = title.invoke()
        val contentResult = joinToString("\n") { content.invoke(it) }
        "$titleResult\n$contentResult"
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = when (isHdb) {
            true -> hdbAdapter
            false -> genericAdapter
        }
        ViewUtil.listenVerticalScrollTopEnd(list, reachTop = {
            publishRxBus(UnlockActivityScrollEvent())
        })
    }

    private fun maybeUnlockActivityScroll() {
        if (list.isNestedScrollingEnabled) {
            publishRxBus(UnlockActivityScrollEvent())
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_project_rental_official_transactions
    }

    override fun getViewModelClass(): Class<ProjectRentalOfficialTransactionsViewModel> {
        return ProjectRentalOfficialTransactionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}
