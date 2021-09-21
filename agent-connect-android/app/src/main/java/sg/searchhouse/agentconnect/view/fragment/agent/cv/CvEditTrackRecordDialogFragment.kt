package sg.searchhouse.agentconnect.view.fragment.agent.cv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_fragment_cv_edit_track_record.*
import kotlinx.android.synthetic.main.layout_list.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCvEditTrackRecordBinding
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.model.api.agent.GetAgentTransactions.AgentTransaction.*
import sg.searchhouse.agentconnect.event.agent.UpdateAgentCv
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.agent.CvTrackRecordAdapter
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv.CvEditTrackRecordFragmentViewModel

class CvEditTrackRecordDialogFragment : FullScreenDialogFragment() {

    private lateinit var mContext: Context
    private lateinit var viewModel: CvEditTrackRecordFragmentViewModel
    private var agentCv: AgentCvPO? = null
    private var transactionType: TransactionType? = null
    private var transactionPropertyType: TransactionPropertyType? = null

    companion object {
        private const val CV = "agent_cv"
        private const val TRANSACTION_TYPE = "transaction_type"
        private const val TRANSACTION_PROPERTY_TYPE = "transaction_property_type"
        const val TAG = "cv_edit_track_record"
        fun newInstance(
            cv: AgentCvPO,
            transactionType: TransactionType,
            transactionPropertyType: TransactionPropertyType
        ): CvEditTrackRecordDialogFragment {
            val dialogFragment = CvEditTrackRecordDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(CV, cv)
            bundle.putSerializable(TRANSACTION_TYPE, transactionType)
            bundle.putSerializable(TRANSACTION_PROPERTY_TYPE, transactionPropertyType)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    private val adapter = CvTrackRecordAdapter { transactionId: Int, isRevealed: Boolean ->
        if (isRevealed) {
            viewModel.revealTransaction(transactionId)
        } else {
            viewModel.concealTransaction(transactionId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //Agent Cv
            it.getSerializable(CV)?.let { data ->
                agentCv = data as AgentCvPO
            } ?: ErrorUtil.handleError(mContext, R.string.error_missing_agent)
            //TransactionType -> Sold or Rented
            it.getSerializable(TRANSACTION_TYPE)?.let { data ->
                transactionType = data as TransactionType
            } ?: ErrorUtil.handleError(mContext, R.string.error_missing_transaction_type)
            //TransactionPropertyType -> HDB or Private
            it.getSerializable(TRANSACTION_PROPERTY_TYPE)?.let { data ->
                transactionPropertyType = data as TransactionPropertyType
            } ?: ErrorUtil.handleError(mContext, R.string.error_missing_transaction_property_type)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogFragmentCvEditTrackRecordBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_cv_edit_track_record,
            container,
            false
        )
        viewModel =
            ViewModelProvider(this).get(CvEditTrackRecordFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateViewModelData()
        setupList()
        observeResultLiveData()
        setOnClickListeners()
    }

    private fun populateViewModelData() {
        agentCv?.let { viewModel.agentCv.value = it }
        transactionType?.let { viewModel.transactionType.value = it }
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(this.activity)
        list.adapter = adapter
        list.itemAnimator = null
    }

    private fun observeResultLiveData() {
        viewModel.transactionType.observe(this, Observer {
            it?.let { type ->
                if (type == TransactionType.SOLD) {
                    viewModel.saleTransactions.value?.let { transactions ->
                        populateItemsGroupByPropertyType(transactions)
                    }
                } else {
                    viewModel.rentTransactions.value?.let { transactions ->
                        populateItemsGroupByPropertyType(transactions)
                    }
                }
            }
        })

        viewModel.saleTransactions.observe(this, Observer {
            it?.let { listings ->
                viewModel.transactionType.value?.let { type ->
                    if (type == TransactionType.SOLD) {
                        populateItemsGroupByPropertyType(listings)
                    }
                }
            }
        })

        viewModel.rentTransactions.observe(this, Observer {
            it?.let { listings ->
                viewModel.transactionType.value?.let { type ->
                    if (type == TransactionType.RENTED) {
                        populateItemsGroupByPropertyType(listings)
                    }
                }
            }
        })

        viewModel.apiCallResult.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    it.error?.let { apiError ->
                        ViewUtil.showMessage(apiError.error)
                    }
                }
                else -> {
                    //Do nothing
                }
            }
        })
    }

    private fun populateItemsGroupByPropertyType(listings: List<TransactedListingPO>) {
        viewModel.selectedOrderCriteria.value?.let { orderCriteria ->
            if (orderCriteria == OrderCriteria.CATEGORY) {
                //ONLY group listings for category default
                val mapByPropertyType =
                    listings.groupBy { it.getTransactionPropertyMainType() }.toList()
                        .sortedBy { getPropertyMainType(it.first) }

                var result = emptyList<Any>()
                mapByPropertyType.map { type ->
                    TransactionPropertyMainType.values()
                        .find { it.value == type.first }
                        ?.let {
                            result =
                                result + CvTrackRecordAdapter.PropertyTypeHeader(
                                    resources.getString(
                                        it.label
                                    )
                                )
                        }
                    result = result + type.second
                }
                adapter.items = result
            } else {
                adapter.items = listings
            }
        }
        //scroll to position of recycler view by selected transaction property type
        if (transactionPropertyType == TransactionPropertyType.HDB) {
            list.scrollToPosition(0)
        } else {
            //private and hdb
            val privateTypeIndex =
                adapter.items.indexOfFirst { item ->
                    item is CvTrackRecordAdapter.PropertyTypeHeader &&
                            (item.title == resources.getString(TransactionPropertyMainType.Condo.label) ||
                                    item.title == resources.getString(TransactionPropertyMainType.Landed.label) ||
                                    item.title == resources.getString(TransactionPropertyMainType.Commercial.label))
                }
            list.scrollToPosition(privateTypeIndex)
        }
        adapter.notifyDataSetChanged()
    }

    private fun getPropertyMainType(cdResearchSubType: Int): ListingEnum.PropertyMainType? {
        val groupSubType = ListingEnum.PropertySubType.values().find { thisSubType ->
            thisSubType.type == cdResearchSubType
        }
        return ListingEnum.PropertyMainType.values().filter {
            it != ListingEnum.PropertyMainType.RESIDENTIAL
        }.find { thisMainType ->
            thisMainType.propertySubTypes.contains(groupSubType)
        }
    }

    private fun showSortDialog() {
        val orderCriteriaGroup = OrderCriteria.values()
        val orderCriteriaLabels = orderCriteriaGroup.map { it.label }
        dialogUtil.showListDialog(orderCriteriaLabels, { _, position ->
            val selectedCategory = orderCriteriaGroup[position]
            viewModel.selectedOrderCriteria.postValue(selectedCategory)
            viewModel.orderCriteriaLabel.postValue(getString(selectedCategory.label))
            viewModel.getAgentTransactions(selectedCategory.value)
        }, R.string.dialog_title_listing_sort_order)
    }

    private fun setOnClickListeners() {
        tab_transaction_type.onTabClickListener = {
            viewModel.transactionType.postValue(it)
        }

        toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        switch_transaction.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                agentCv?.let { data ->
                    data.showTransactions = b
                    RxBus.publish(UpdateAgentCv(data))
                }
            }
        }
        btn_sort.setOnClickListener {
            showSortDialog()
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }
}