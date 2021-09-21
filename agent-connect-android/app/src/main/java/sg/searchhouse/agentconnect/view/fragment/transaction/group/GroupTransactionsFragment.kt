package sg.searchhouse.agentconnect.view.fragment.transaction.group

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_group_transactions.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.FragmentGroupTransactionsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.transaction.TableListResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.transaction.*
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.transaction.group.GroupTransactionAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.helper.transaction.TransactionsHorizontalSwipeIndicatorHelper
import sg.searchhouse.agentconnect.viewmodel.fragment.transaction.GroupTransactionsFragmentViewModel

class GroupTransactionsFragment :
    ViewModelFragment<GroupTransactionsFragmentViewModel, FragmentGroupTransactionsBinding>() {

    companion object {
        private const val ARGUMENT_KEY_OWNERSHIP_TYPE = "ARGUMENT_KEY_OWNERSHIP_TYPE"

        fun newInstance(ownershipType: ListingEnum.OwnershipType): GroupTransactionsFragment {
            val fragment = GroupTransactionsFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_OWNERSHIP_TYPE, ownershipType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var defaultOwnershipType: ListingEnum.OwnershipType

    private lateinit var adapter: GroupTransactionAdapter

    private var isRescaled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultOwnershipType =
            arguments?.getSerializable(ARGUMENT_KEY_OWNERSHIP_TYPE) as ListingEnum.OwnershipType
        viewModel = ViewModelProvider(this).get(GroupTransactionsFragmentViewModel::class.java)
        observeLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        listenRxBuses()
        setOnClickListeners()
        viewModel.ownershipType.postValue(defaultOwnershipType)
    }

    private fun setOnClickListeners() {
        layout_root.setOnClickListener {
            maybeUnlockActivityScroll()
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

    private fun observeLiveData() {
        viewModel.listItems.observe(this) {
            adapter.ownershipType = viewModel.ownershipType.value ?: throw IllegalArgumentException(
                "ownershipType must present here"
            )
            adapter.propertyMainType = viewModel.propertyMainType.value
            adapter.listItems = it ?: emptyList()
            adapter.notifyDataSetChanged()
            if (viewModel.transactionSearchCriteriaV2VO.value?.page == 1) {
                scroll_view_horizontal.scrollX = 0
                maybeShowHorizontalSwipeIndicator()
            }
            maybeRescale()
        }

        viewModel.transactionSearchCriteriaV2VO.observeNotNull(this) {
            viewModel.performRequest(it)
        }

        viewModel.sortType.observe(this) {
            updateSort(it ?: TransactionEnum.SortType.DEFAULT)
        }

        viewModel.ownershipType.observeNotNull(this) {
            adapter.ownershipType = it
            adapter.notifyDataSetChanged()
        }
        viewModel.scale.observeNotNull(this) {
            adapter.scale = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateSort(sortType: TransactionEnum.SortType) {
        val transactionSearchCriteriaV2VO = viewModel.transactionSearchCriteriaV2VO.value ?: return
        transactionSearchCriteriaV2VO.orderByParam = sortType.value
        transactionSearchCriteriaV2VO.page = 1
        viewModel.transactionSearchCriteriaV2VO.postValue(transactionSearchCriteriaV2VO)
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateGroupTransactionsEvent::class.java) {
            viewModel.isEnablePagination.postValue(it.isEnablePagination)
            viewModel.ownershipType.postValue(it.ownershipType)
            viewModel.propertyMainType.postValue(it.propertyMainType)
            viewModel.transactionSearchCriteriaV2VO.postValue(it.transactionSearchCriteriaV2VO)
        }
        listenRxBus(LockFragmentScrollEvent::class.java) {
            list.isNestedScrollingEnabled = !it.isLock
            maybeShowHorizontalSwipeIndicator()
        }
        listenRxBus(RequestGroupTransactionsCsvEvent::class.java) {
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
        val ownershipType =
            viewModel.ownershipType.value
                ?: return ErrorUtil.handleError("Missing `ownershipType` in view model!")
        val propertyMainType = viewModel.propertyMainType.value
            ?: return ErrorUtil.handleError("Missing `propertyMainType` in view model!")
        val csvContent =
            getTransactionsCsvContent(thisContext, listItems, ownershipType, propertyMainType)
        RxBus.publish(SendGroupTransactionsCsvEvent(csvContent))
    }

    @Throws(IllegalArgumentException::class)
    private fun getTransactionsCsvContent(
        context: Context,
        transactionListItems: List<TableListResponse.Transactions.Result>,
        ownershipType: ListingEnum.OwnershipType,
        propertyMainType: ListingEnum.PropertyMainType
    ): String {
        return when (propertyMainType) {
            ListingEnum.PropertyMainType.HDB -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getGroupHdbCsvTitle(context)
                }, content = {
                    it.getGroupHdbCsvContent(context, ownershipType)
                })
            }
            ListingEnum.PropertyMainType.CONDO -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getGroupNlpCsvTitle(context)
                }, content = {
                    it.getGroupNlpCsvContent(ownershipType)
                })
            }
            ListingEnum.PropertyMainType.LANDED -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getGroupLandedCsvTitle(context)
                }, content = {
                    it.getGroupLandedCsvContent(context, ownershipType)
                })
            }
            ListingEnum.PropertyMainType.COMMERCIAL -> {
                transactionListItems.getCustomCsvContent(title = {
                    TableListResponse.Transactions.getGroupCommercialCsvTitle(context)
                }, content = {
                    it.getGroupCommercialCsvContent(context, ownershipType)
                })
            }
            else -> throw IllegalArgumentException("Invalid property main type `${propertyMainType}`!")
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

    private fun maybeShowHorizontalSwipeIndicator() {
        if (list.isNestedScrollingEnabled) {
            TransactionsHorizontalSwipeIndicatorHelper.maybeShowGroupIndicator(layout_indicate_swipe)
        }
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = GroupTransactionAdapter(defaultOwnershipType)
        list.adapter = adapter
        ViewUtil.listenVerticalScrollEnd(list, reachBottom = {
            maybeLoadNext()
        })
        layout_swipe_refresh.setOnRefreshListener {
            maybeUnlockActivityScroll()
            layout_swipe_refresh.isRefreshing = false
        }
    }

    private fun maybeLoadNext() {
        if (viewModel.isEnablePagination.value == true && viewModel.canLoadNext() && !adapter.isLoading()) {
            adapter.listItems += Loading()
            adapter.notifyDataSetChanged()
            list.scrollToPosition(adapter.itemCount - 1)
            loadNext()
        }
    }

    private fun maybeUnlockActivityScroll() {
        if (list.isNestedScrollingEnabled) {
            publishRxBus(UnlockActivityScrollEvent())
        }
    }

    private fun loadNext() {
        val nextPage = (viewModel.transactionSearchCriteriaV2VO.value?.page ?: 1) + 1
        val transactionSearchCriteriaV2VO = viewModel.transactionSearchCriteriaV2VO.value ?: return
        transactionSearchCriteriaV2VO.page = nextPage
        viewModel.transactionSearchCriteriaV2VO.postValue(transactionSearchCriteriaV2VO)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_group_transactions
    }

    override fun getViewModelClass(): Class<GroupTransactionsFragmentViewModel> {
        return GroupTransactionsFragmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}