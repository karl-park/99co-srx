package sg.searchhouse.agentconnect.view.activity.agent.cv

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_cv_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCvListingsBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.CobrokeDialogOption
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.listing.ListingDetailsActivity
import sg.searchhouse.agentconnect.view.activity.listing.SmsBlastActivity
import sg.searchhouse.agentconnect.view.activity.listing.SrxChatActivity
import sg.searchhouse.agentconnect.view.adapter.listing.search.ListingListAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.agent.cv.CvListingsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingsViewModel

//TODO: to refine this activity
class CvListingsActivity : BaseActivity() {

    private lateinit var binding: ActivityCvListingsBinding
    private lateinit var viewModel: CvListingsViewModel
    private var adapter = ListingListAdapter(onClickListener = { listingId, listingType ->
        viewListingDetails(listingId, listingType)
    }, onCheckListener = { listingId, listingType ->
        viewModel.selectedMode.postValue(ListingsViewModel.SelectMode.COBROKE)
        viewModel.toggleSelectedListItems(listingId, listingType)
    })

    private var agentId: Int = 0
    private var ownershipType: ListingEnum.OwnershipType = ListingEnum.OwnershipType.SALE
    private var dialogCobroke: androidx.appcompat.app.AlertDialog? = null
    private val currentUser = SessionUtil.getCurrentUser()

    companion object {
        const val EXTRA_KEY_OWNERSHIP_TYPE = "ownership_type"
        const val EXTRA_KEY_AGENT_ID = "agent_id"
        const val EXTRA_KEY_AGENT_NAME = "agent_name"
        const val EXTRA_KEY_TOTAL_PROPERTIES = "total_properties"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cv_listings)
        viewModel = ViewModelProvider(this).get(CvListingsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupActionBar()
        setupParamsFromExtra()
        setupList()
        observeLiveData()
        setScrollListener()
        handleListeners()
    }

    private fun setupActionBar() {
        viewModel.title.value = getString(R.string.label_cv_listings)
    }

    private fun setupParamsFromExtra() {
        intent.getSerializableExtra(EXTRA_KEY_OWNERSHIP_TYPE)?.let {
            //get agent id and ownership type
            agentId = intent.getIntExtra(EXTRA_KEY_AGENT_ID, 0)
            ownershipType = it as ListingEnum.OwnershipType
            viewModel.index.value = viewModel.listingItems.value?.size ?: 0

            //update title with agent name
            val agentName = intent.getStringExtra(EXTRA_KEY_AGENT_NAME) ?: ""
            if (ownershipType == ListingEnum.OwnershipType.SALE) {
                viewModel.title.value = getString(R.string.label_cv_sale_listings)
            } else {
                viewModel.title.value = getString(R.string.label_cv_rent_listings)
            }

            //total properties
            val totalCount = intent.getIntExtra(EXTRA_KEY_TOTAL_PROPERTIES, 0)
            if (totalCount > 0) {
                if (totalCount == 1) {
                    viewModel.totalProperties.value =
                        getString(R.string.msg_property, totalCount.toString())
                } else {
                    viewModel.totalProperties.value =
                        getString(R.string.msg_properties, totalCount.toString())
                }
            }

            //Check own cv or not
            val currentUserId = currentUser?.id ?: 0
            viewModel.isOwnCv.value = currentUserId == agentId
            adapter.isShowCheckBox = currentUserId != agentId

        } ?: ErrorUtil.handleError("Missing ownership type in cv listings")
    }

    private fun setupList() {
        binding.listListings.layoutManager = LinearLayoutManager(this)
        binding.listListings.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                it.listings.srxStpListings?.let { srxStpListings ->
                    viewModel.total.value = srxStpListings.total
                    viewModel.listingItems.value?.addAll(srxStpListings.listingPOs)
                    adapter.items.removeAll(adapter.items.filterIsInstance<Loading>())
                    adapter.items.addAll(srxStpListings.listingPOs)
                    adapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.index.observe(this, Observer { index ->
            index?.let {
                if (it == 0) {
                    viewModel.findListings(it, ownershipType, agentId)
                } else {
                    viewModel.loadMoreListings(it, ownershipType, agentId)
                }
            }
        })
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.layoutActionListings.btnSubmitCobroke.setOnClickListener {
            showCobrokeDialog()
        }
    }

    private fun setScrollListener() {
        ViewUtil.listenVerticalScrollEnd(list_listings, reachBottom = {
            if (viewModel.canLoadMore()) {
                if (adapter.items.last() !is Loading) {
                    adapter.items.add(Loading())
                    adapter.notifyDataSetChanged()
                    viewModel.index.value = viewModel.listingItems.value?.size?.plus(1) ?: 0
                }
            }
        })
    }

    private fun viewListingDetails(listingId: String, listingType: String) {
        val intent = Intent(this, ListingDetailsActivity::class.java)
        intent.putExtra(ListingDetailsActivity.EXTRA_KEY_LISTING_ID, listingId)
        intent.putExtra(ListingDetailsActivity.EXTRA_KEY_LISTING_TYPE, listingType)
        startActivity(intent)
    }

    //TODO:  DON NOT REMOVE COMMENT refactor this cobroking code after this phase .. do as separate widget
    //TODO if they want to add, then add
    private fun showCobrokeDialog() {
        val list = CobrokeDialogOption.values().map { it.label }
        dialogCobroke = dialogUtil.showListDialog(list, { dialogInterface, position ->
            when (position) {
                CobrokeDialogOption.PHONE_SMS.position -> {
                    showPhoneSmsCobrokeDisclaimer()
                }
                CobrokeDialogOption.SRX_CHAT.position -> {
                    performSsmCobroke()
                }
                CobrokeDialogOption.SRX_SMS_BLAST.position -> {
                    launchSmsBlastActivity()
                }
            }
            dialogInterface.dismiss()
        })
    }

    private fun launchSmsBlastActivity() {
        try {
            SmsBlastActivity.launch(this, viewModel.getSelectedCobrokeListings())
        } catch (e: IllegalArgumentException) {
            ViewUtil.showMessage(R.string.toast_error_no_listings)
        }
    }

    private fun showPhoneSmsCobrokeDisclaimer() {
        dialogUtil.showInformationDialog(R.string.dialog_message_warning_sms_telco) {
            val phoneNumbers = getSelectedAgents().mapNotNull { it?.mobile }.joinToString(",")
            IntentUtil.sendSms(this, phoneNumbers)
        }
    }

    private fun performSsmCobroke() {
        val selectedListings = viewModel.selectedListItems.value ?: return
        if (selectedListings.isNotEmpty()) {
            SrxChatActivity.launch(
                activity = this,
                listingIds = selectedListings
            )
        }
    }

    private fun getSelectedAgents(): List<AgentPO?> {
        val selectedListingTypeIds = viewModel.selectedListItems.value?.map {
            "${it.second}${it.first}"
        } ?: emptyList()

        return viewModel.getCurrentListingPOs().filter {
            selectedListingTypeIds.contains(it.getListingTypeId())
        }.distinctBy {
            it.agentPO?.id ?: ""
        }.map {
            it.agentPO
        }
    }
}