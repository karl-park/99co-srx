package sg.searchhouse.agentconnect.view.fragment.listing.portal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_fragment_auto_import_listings_preview.*
import kotlinx.android.synthetic.main.layout_bottom_action_bar.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentAutoImportListingsPreviewBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.event.dashboard.UpdateDashboardListingsCountEvent
import sg.searchhouse.agentconnect.util.JsonUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.adapter.listing.portal.PortalListingsAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal.AutoImportListingsPreviewViewModel

class AutoImportListingsPreviewDialogFragment :
    ViewModelDialogFragment<AutoImportListingsPreviewViewModel, DialogFragmentAutoImportListingsPreviewBinding>() {

    private lateinit var adapter: PortalListingsAdapter

    companion object {
        private const val TAG = "AUTO_IMPORT_LISTINGS_PREVIEW_TAG"
        private const val EXTRA_KEY_TOTAL_COUNT = "EXTRA_KEY_TOTAL_COUNT"
        private const val EXTRA_KEY_LISTINGS = "EXTRA_KEY_LISTINGS"
        private const val EXTRA_KEY_PORTAL_ACCOUNT = "EXTRA_KEY_PORTAL_ACCOUNT"

        fun newInstance(
            totalCount: Int,
            listings: List<PortalListingPO>,
            portalAccountPO: PortalAccountPO
        ): AutoImportListingsPreviewDialogFragment {
            val dialogFragment = AutoImportListingsPreviewDialogFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_KEY_TOTAL_COUNT, totalCount)
            bundle.putString(EXTRA_KEY_LISTINGS, JsonUtil.parseToJsonString(listings))
            bundle.putSerializable(EXTRA_KEY_PORTAL_ACCOUNT, portalAccountPO)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(
            fragmentManager: FragmentManager,
            totalCount: Int,
            listings: List<PortalListingPO>,
            portalAccountPO: PortalAccountPO
        ) {
            newInstance(totalCount, listings, portalAccountPO).show(fragmentManager, TAG)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExtraParams()
        setupAdapter()
        observeLiveData()
        setupListeners()
        performRequest()
    }

    private fun performRequest() {
        viewModel.getAPIs()
    }

    private fun setupExtraParams() {
        val bundle = arguments ?: return
        val totalListingsCount = bundle.getInt(EXTRA_KEY_TOTAL_COUNT, 0)
        if (totalListingsCount > 0) {
            viewModel.totalListingCountLabel.postValue(
                this.resources.getQuantityString(
                    R.plurals.auto_import_listing_count,
                    totalListingsCount,
                    NumberUtil.formatThousand(totalListingsCount)
                )
            )
        }

        bundle.getString(EXTRA_KEY_LISTINGS)?.run {
            val list = JsonUtil.parseListOrEmpty(this, PortalListingPO::class.java)
            viewModel.listings.postValue(list)
        }

        bundle.getSerializable(EXTRA_KEY_PORTAL_ACCOUNT)?.run {
            val portalAccountPO = this as PortalAccountPO
            viewModel.portalAccountPO.postValue(portalAccountPO)
        }
    }

    private fun setupAdapter() {
        activity?.run {
            adapter = PortalListingsAdapter(this,
                onSelectListing = { onSelectPortalListing(it) },
                onToggleSync = { _: PortalListingPO, _: Boolean ->
                    println("do nothing")
                })
            binding.listPortalListings.layoutManager = LinearLayoutManager(this)
            binding.listPortalListings.adapter = adapter
        }

    }

    private fun observeLiveData() {
        viewModel.listings.observeNotNull(this) { list ->
            //auto select all listings on entry of the screen
            viewModel.selectedListings.value = list
            adapter.selectedListingIds = list.map { it.id }
            adapter.populateListings(list)
        }

        viewModel.isImportingListingDone.observeNotNull(this) {
            viewModel.selectedListings.postValue(emptyList())
            //refresh dashboard listing counts
            RxBus.publish(UpdateDashboardListingsCountEvent())
            activity?.run {
                PortalImportSummaryDialogFragment.launch(
                    fragmentManager = supportFragmentManager,
                    source = ListingManagementEnum.PortalImportSummarySource.PORTAL_PREVIEW_SCREEN,
                    successCount = viewModel.importSuccessCount,
                    failedCount = viewModel.importFailedCount
                )
            }
            dialog?.dismiss()
        }

        viewModel.selectedListings.observeNotNull(this) { list ->
            if (list?.isNotEmpty() == true) {
                viewModel.listingsMessage.value = getString(
                    R.string.msg_portal_listings_selected,
                    this.resources.getQuantityString(
                        R.plurals.label_listings_count,
                        list.count(),
                        list.size.toString()
                    )
                )
            }
        }
    }

    private fun onSelectPortalListing(listingPO: PortalListingPO) {
        var listings = viewModel.selectedListings.value ?: emptyList()
        //add remove listing id from listings
        listings = if (listings.any { it.id == listingPO.id }) {
            listings - listingPO
        } else {
            listings + listingPO
        }

        viewModel.selectedListings.value = listings
        adapter.selectedListingIds = listings.map { it.id }.distinct()
        adapter.notifyDataSetChanged()
    }

    private fun setupListeners() {
        tv_skip.setOnClickListener { dialog?.dismiss() }
        btn_action.setOnClickListener { viewModel.performImportListings() }
        tv_select_all.setOnClickListener { handleSelectUnSelectListings() }
        tv_full_disclaimer.setOnClickListener { showFullDisclaimer() }
    }

    private fun showFullDisclaimer() {
        activity?.run { ImportListingsDisclaimerDialogFragment.launch(supportFragmentManager) }

    }

    private fun handleSelectUnSelectListings() {
        val listings = viewModel.listings.value ?: emptyList()
        var selectedListings = viewModel.selectedListings.value ?: emptyList()
        selectedListings = if (listings.size == selectedListings.size) {
            //un select all
            emptyList()
        } else {
            //select all
            listings
        }
        viewModel.selectedListings.value = selectedListings
        adapter.selectedListingIds = selectedListings.map { it.id }.distinct()
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
        dialog?.setCancelable(false)
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_auto_import_listings_preview
    }

    override fun getViewModelClass(): Class<AutoImportListingsPreviewViewModel> {
        return AutoImportListingsPreviewViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }


    override fun getViewModelKey(): String? {
        return null
    }
}