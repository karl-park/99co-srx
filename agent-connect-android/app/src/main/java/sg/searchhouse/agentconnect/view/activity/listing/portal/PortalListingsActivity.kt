package sg.searchhouse.agentconnect.view.activity.listing.portal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_bottom_action_bar.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityPortalListingBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.event.dashboard.UpdateDashboardListingsCountEvent
import sg.searchhouse.agentconnect.event.listing.portal.UpdatePortalListingsEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.listing.portal.PortalListingsAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.portal.PortalImportSummaryDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.listing.portal.PortalListingsViewModel

class PortalListingsActivity :
    ViewModelActivity<PortalListingsViewModel, ActivityPortalListingBinding>() {

    private lateinit var adapter: PortalListingsAdapter
    private var dialogProgress: AlertDialog? = null

    companion object {
        private const val EXTRA_KEY_PORTAL_API_RESPONSE = "EXTRA_KEY_PORTAL_API_RESPONSE"
        private const val EXTRA_KEY_PORTAL_ACCOUNT_PO = "EXTRA_KEY_PORTAL_ACCOUNT_PO"

        fun launch(
            activity: Activity,
            portalApiResponse: GetPortalAPIsResponse? = null,
            portalAccountPO: PortalAccountPO? = null
        ) {
            val intent = Intent(activity, PortalListingsActivity::class.java)
            portalApiResponse?.run { intent.putExtra(EXTRA_KEY_PORTAL_API_RESPONSE, this) }
            portalAccountPO?.run { intent.putExtra(EXTRA_KEY_PORTAL_ACCOUNT_PO, this) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
        setupExtraParams()
        observeRxBus()
        observeLiveData()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        adapter = PortalListingsAdapter(this,
            onSelectListing = { onSelectListing(it) },
            onToggleSync = { portalListingPO: PortalListingPO, isChecked: Boolean ->
                onToggleSync(portalListingPO, isChecked)
            })
        binding.listPortalListings.layoutManager = LinearLayoutManager(this)
        binding.listPortalListings.adapter = adapter
    }

    private fun setupExtraParams() {
        when (SessionUtil.getListingPortalMode()) {
            PortalMode.SERVER -> viewModel.getServerPortalListings()
            PortalMode.CLIENT -> {
                intent.getSerializableExtra(EXTRA_KEY_PORTAL_ACCOUNT_PO)?.run {
                    viewModel.clientPortalPO = this as PortalAccountPO
                }
                intent.getSerializableExtra(EXTRA_KEY_PORTAL_API_RESPONSE)?.run {
                    viewModel.portalApiResponse.value = this as GetPortalAPIsResponse
                } ?: viewModel.getServerPortalListings()
            }
            null -> ErrorUtil.handleError("Wrong portal mode type")
        }
    }

    private fun observeRxBus() {
        listenRxBus(UpdatePortalListingsEvent::class.java) {
            when (SessionUtil.getListingPortalMode()) {
                PortalMode.SERVER -> viewModel.getServerPortalListings()
                PortalMode.CLIENT -> {
                    viewModel.clientPortalPO = it.portalAccountPO
                    viewModel.portalAccountPO.postValue(it.portalAccountPO)
                    val portalAccId =
                        viewModel.clientPortalPO?.portalId?.toIntOrNull() ?: return@listenRxBus
                    viewModel.getClientPortalListings(portalAccId)
                }
            }
        }
    }

    private fun observeLiveData() {
        //getting listing from client side
        viewModel.portalApiResponse.observeNotNull(this) {
            viewModel.portalAccountPO.postValue(viewModel.clientPortalPO)
            val portalAccId =
                viewModel.clientPortalPO?.portalId?.toIntOrNull() ?: return@observeNotNull
            viewModel.getClientPortalListings(portalAccId)
        }

        viewModel.activeListingsResult.observeNotNull(this) { viewModel.clientGetListings(it) }

        viewModel.mainResponse.observeNotNull(this) {
            viewModel.portalAccountPO.value = it.portalAccount
        }

        viewModel.listings.observeNotNull(this) {
            viewModel.portalListingsByType.value = it
        }

        viewModel.portalListingsByType.observeNotNull(this) {
            adapter.populateListings(it)
        }

        viewModel.loggedOut.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> finish()
                ApiStatus.StatusKey.FAIL -> ViewUtil.showMessage(it.error?.error)
                else -> {
                    //DO nothing
                }
            }
        }

        viewModel.isListingsImported.observeNotNull(this) {
            if (it) {
                dialogProgress?.dismiss()
                //refresh dashboard listing counts
                RxBus.publish(UpdateDashboardListingsCountEvent())
                PortalImportSummaryDialogFragment.launch(
                    fragmentManager = supportFragmentManager,
                    source = PortalImportSummarySource.PORTAL_MAIN_SCREEN,
                    successCount = viewModel.successCount,
                    failedCount = viewModel.failCount
                )
            } else {
                dialogProgress?.dismiss()
            }
        }

        viewModel.selectedListingsType.observeNotNull(this) { type ->
            binding.tabListingsType.setValue(type)
            //clear selected listings
            viewModel.selectedListings.value = arrayListOf()
            adapter.selectedListingIds = emptyList()
            when (type) {
                PortalListingsType.ALL_LISTINGS -> {
                    viewModel.portalListingsByType.value = viewModel.listings.value ?: emptyList()
                }
                PortalListingsType.IMPORTED_LISTING -> {
                    val listings = viewModel.listings.value ?: emptyList()
                    viewModel.portalListingsByType.value =
                        listings.filter { listing -> listing.srxImportedDate > 0 && listing.srxListingId > 0 }
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.toggleListingsSyncStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    println("do nothing for this status data")
                }
            }
        }

        viewModel.apiErrorMessage.observeNotNull(this) { errorMsg ->
            ViewUtil.showMessage(errorMsg)
        }

    }

    private fun onSelectListing(portalListingPO: PortalListingPO) {
        val listings = viewModel.selectedListings.value ?: arrayListOf()
        //add remove listing id from listings
        val contain = listings.any { it.id == portalListingPO.id }
        if (contain) {
            listings.remove(portalListingPO)
        } else {
            listings.add(portalListingPO)
        }

        viewModel.listingsMessage.value = getString(
            R.string.msg_portal_listings_selected,
            this.resources.getQuantityString(
                R.plurals.label_listings_count,
                listings.count(),
                listings.size.toString()
            )
        )

        viewModel.selectedListings.value = listings
        adapter.selectedListingIds = listings.map { it.id }.distinct()
        adapter.notifyDataSetChanged()
    }

    private fun onToggleSync(portalListingPO: PortalListingPO, isChecked: Boolean) {
        if (isChecked) {
            portalListingPO.syncInd = PortalSyncIndicator.SYNC_ENABLED.value
        } else {
            portalListingPO.syncInd = PortalSyncIndicator.SYNC_DISABLED.value
        }
        viewModel.toggleListingsSync(portalListingPO)
    }

    private fun handleListeners() {
        binding.tabListingsType.onToggleListingsType = {
            viewModel.selectedListingsType.value = it
        }

        btn_action.setOnClickListener {
            val message = viewModel.listingsMessage.value ?: return@setOnClickListener
            DialogUtil(this)
                .showActionDialog(getString(R.string.dialog_message_import_confirmation, message)) {
                    dialogProgress =
                        dialogUtil.showProgressDialog(R.string.dialog_message_importing_listings)
                    viewModel.importPortalListings()
                }
        }

        binding.btnReadMore.setOnClickListener {
            val isReadMore = viewModel.isReadMore.value ?: return@setOnClickListener
            viewModel.isReadMore.value = !isReadMore
        }

        binding.btnSelectAll.setOnClickListener {
            viewModel.portalListingsByType.value?.let { items ->
                viewModel.selectedListings.value?.let { selectedItems ->
                    if (selectedItems.size == items.size) {
                        selectedItems.clear()
                    } else {
                        selectedItems.clear()
                        selectedItems.addAll(items)
                        viewModel.listingsMessage.value = getString(
                            R.string.msg_portal_listings_selected,
                            this.resources.getQuantityString(
                                R.plurals.label_listings_count,
                                selectedItems.count(),
                                selectedItems.size.toString()
                            )
                        )
                    }
                    viewModel.selectedListings.value = selectedItems
                    adapter.selectedListingIds = selectedItems.map { it.id }.distinct()
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.btnSyncSetting.setOnClickListener {
            PortalSettingsActivity.launch(
                activity = this,
                portalAccountPO = viewModel.portalAccountPO.value,
                source = PortalSettingsActivity.Source.PORTAL_LISTING
            )
        }
    }

    fun refreshPortalListings() {
        //clear and reset some data
        viewModel.mainResponse.value = null

        adapter.selectedListingIds = listOf()
        adapter.failedListingIds = viewModel.failedListingIds

        viewModel.resetData()
        viewModel.getServerPortalListings()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_portal_listings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_sign_out -> {
                performLogout()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun performLogout() {
        when (SessionUtil.getListingPortalMode()) {
            PortalMode.SERVER -> viewModel.logoutPortalServer()
            PortalMode.CLIENT -> {
                SessionUtil.removePortalAuthentication(this)
                finish()
            }
            null -> ErrorUtil.handleError("Wrong portal mode type")
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_portal_listing
    }

    override fun getViewModelClass(): Class<PortalListingsViewModel> {
        return PortalListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

}