package sg.searchhouse.agentconnect.view.fragment.transaction.project

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_project_transactions.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.FragmentProjectTransactionsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.transaction.*
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectTransactionAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.helper.transaction.TransactionsHorizontalSwipeIndicatorHelper
import sg.searchhouse.agentconnect.viewmodel.fragment.transaction.ProjectTransactionsFragmentViewModel

class ProjectTransactionsFragment :
    ViewModelFragment<ProjectTransactionsFragmentViewModel, FragmentProjectTransactionsBinding>() {

    companion object {
        private const val ARGUMENT_PROJECT_ID = "ARGUMENT_PROJECT_ID"
        private const val ARGUMENT_PROJECT_TRANSACTION_TYPE = "ARGUMENT_PROJECT_TRANSACTION_TYPE"
        private const val ARGUMENT_PROPERTY_SUB_TYPE = "ARGUMENT_PROPERTY_SUB_TYPE"

        fun newInstance(
            projectId: Int,
            projectTransactionType: UpdateProjectTransactionsEvent.ProjectTransactionType,
            propertySubType: ListingEnum.PropertySubType
        ): ProjectTransactionsFragment {
            val fragment = ProjectTransactionsFragment()
            val bundle = Bundle()
            bundle.putInt(ARGUMENT_PROJECT_ID, projectId)
            bundle.putSerializable(ARGUMENT_PROJECT_TRANSACTION_TYPE, projectTransactionType)
            bundle.putSerializable(ARGUMENT_PROPERTY_SUB_TYPE, propertySubType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var adapter: ProjectTransactionAdapter

    private var projectId: Int = 0
    private lateinit var propertySubType: ListingEnum.PropertySubType
    private lateinit var projectTransactionType: UpdateProjectTransactionsEvent.ProjectTransactionType

    private var isRescaled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArguments()
    }

    private fun setupArguments() {
        projectTransactionType =
            arguments?.getSerializable(ARGUMENT_PROJECT_TRANSACTION_TYPE) as UpdateProjectTransactionsEvent.ProjectTransactionType?
                ?: throw IllegalArgumentException("Missing ProjectTransactionType argument")
        propertySubType =
            arguments?.getSerializable(ARGUMENT_PROPERTY_SUB_TYPE) as ListingEnum.PropertySubType?
                ?: throw IllegalArgumentException("Missing `propertySubType` argument")
        projectId = arguments?.getInt(ARGUMENT_PROJECT_ID)
            ?: throw IllegalArgumentException("Missing project ID argument")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFromExtra()
        setupList()
        listenRxBuses()
        observeLiveData()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        layout_root.setOnClickListener {
            maybeUnlockActivityScroll()
        }
    }

    private fun setupFromExtra() {
        viewModel.projectId = projectId
        viewModel.propertySubType.postValue(propertySubType)
        viewModel.projectTransactionType.postValue(projectTransactionType)
    }

    private fun observeLiveData() {
        viewModel.listItems.observe(viewLifecycleOwner) {
            adapter.listItems = it ?: emptyList()
            adapter.notifyDataSetChanged()
            if (viewModel.page.value == 1) {
                scroll_view_horizontal.scrollX = 0
                maybeShowHorizontalSwipeIndicator()
            }
            maybeRescale()
        }
        viewModel.projectTransactionRequest.observe(viewLifecycleOwner) {
            viewModel.page.postValue(1)
        }
        viewModel.sortType.observe(viewLifecycleOwner) {
            viewModel.page.postValue(1)
        }
        viewModel.page.observe(viewLifecycleOwner) {
            if (it == 1) {
                adapter.listItems = emptyList()
                adapter.notifyDataSetChanged()
            }
            viewModel.performRequest()
        }
        viewModel.scale.observeNotNull(this) {
            adapter.scale = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun maybeRescale() {
        if (!isRescaled) {
            list.postDelayed {
                val listWidth = list?.layoutManager?.width ?: return@postDelayed
                val mActivity = activity ?: return@postDelayed
                val screenWidth = ViewUtil.getScreenWidth(mActivity)
                viewModel.maybeRescale(listWidth, screenWidth)
            }
            isRescaled = true
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
        if (viewModel.projectTransactionRequest.value == null) {
            publishRxBus(RequestActivityLoadProjectTransactionsEvent())
        }
    }

    private fun listenRxBuses() {
        listenRxBus(LockFragmentScrollEvent::class.java) {
            list.isNestedScrollingEnabled = !it.isLock
            maybeShowHorizontalSwipeIndicator()
        }
        listenRxBus(RequestLoadProjectTransactionsEvent::class.java) {
            viewModel.projectTransactionRequest.postValue(it.projectTransactionRequest)
        }
        listenRxBus(RequestProjectTransactionsCsvEvent::class.java) {
            if (isResumed) {
                sendExportCsvToActivity(it.isLimited)
            }
        }
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = ProjectTransactionAdapter(propertySubType, getOwnershipType())
        list.adapter = adapter
        ViewUtil.listenVerticalScrollEnd(list, reachBottom = {
            doReachBottomAction()
        })
        layout_swipe_refresh.setOnRefreshListener {
            publishRxBus(UnlockActivityScrollEvent())
            layout_swipe_refresh.isRefreshing = false
        }
    }

    private fun getOwnershipType(): ListingEnum.OwnershipType {
        return when (projectTransactionType) {
            UpdateProjectTransactionsEvent.ProjectTransactionType.SALE -> ListingEnum.OwnershipType.SALE
            else -> ListingEnum.OwnershipType.RENT
        }
    }

    private fun doReachBottomAction() {
        if (viewModel.canLoadNext() && !adapter.isLoading()) {
            adapter.listItems += Loading()
            adapter.notifyDataSetChanged()
            list.scrollToPosition(adapter.itemCount - 1)
            maybeLoadNext()
        }
    }

    private fun maybeUnlockActivityScroll() {
        if (list.isNestedScrollingEnabled) {
            publishRxBus(UnlockActivityScrollEvent())
        }
    }

    private fun maybeLoadNext() {
        val nextPage = (viewModel.page.value ?: 1) + 1
        viewModel.page.postValue(nextPage)
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

    @Throws(IllegalArgumentException::class)
    private fun getTransactionsCsvContent(
        context: Context,
        transactionListItems: List<TableListResponse.Transactions.Result>
    ): String {
        val ownershipType = getOwnershipType()
        return when {
            viewModel.isHdb.value == true -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getProjectRealTimeHdbCsvTitle(context)
                }, content = {
                    it.getProjectRealTimeHdbCsvContent(ownershipType)
                })
            }
            viewModel.isCondo.value == true -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getProjectRealTimeNlpCsvTitle(context)
                }, content = {
                    it.getProjectRealTimeNlpCsvContent(ownershipType)
                })
            }
            viewModel.isLanded.value == true -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getProjectRealTimeLandedCsvTitle(context)
                }, content = {
                    it.getProjectRealTimeLandedCsvContent(ownershipType)
                })
            }
            viewModel.isCommercial.value == true -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getProjectRealTimeCommercialCsvTitle(
                        context
                    )
                }, content = {
                    it.getProjectRealTimeCommercialCsvContent(ownershipType)
                })
            }
            else -> throw IllegalArgumentException("Invalid property sub type `${viewModel.propertySubType.value?.name}`!")
        }
    }

    private fun List<TableListResponse.Transactions.Result>.getCustomCsvContent(
        title: () -> String,
        content: (TableListResponse.Transactions.Result) -> String
    ) = run {
        val titleResult = "No.,${title.invoke()}"
        val contentResult =
            mapIndexed { index, result -> "${index + 1},${content.invoke(result)}" }.joinToString("\n")
        "$titleResult\n$contentResult"
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_project_transactions
    }

    override fun getViewModelClass(): Class<ProjectTransactionsFragmentViewModel> {
        return ProjectTransactionsFragmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return projectTransactionType.name
    }
}
