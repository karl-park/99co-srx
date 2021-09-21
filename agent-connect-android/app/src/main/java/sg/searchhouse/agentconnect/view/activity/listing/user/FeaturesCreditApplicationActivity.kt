package sg.searchhouse.agentconnect.view.activity.listing.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityFeaturesCreditApplicationBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.SrxCreditListingActivationPO.ListingActivationPO.ListingActivationVirtualTour
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.SrxCreditListingActivationPO.ListingActivationPO.ListingActivationValuation
import sg.searchhouse.agentconnect.model.app.AppointmentDateTime
import sg.searchhouse.agentconnect.event.agent.NotifyPaymentSuccessEvent
import sg.searchhouse.agentconnect.event.listing.user.BookAppointmentEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.agent.profile.SubscriptionCreditPackageDetailsActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.listing.user.FeaturesCreditApplicationListingsAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.user.BookAppointmentDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.user.FeaturedListingTypesDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.listing.user.FeaturesCreditApplicationViewModel
import java.io.IOException

class FeaturesCreditApplicationActivity :
    ViewModelActivity<FeaturesCreditApplicationViewModel, ActivityFeaturesCreditApplicationBinding>(
        isSliding = true
    ) {

    private lateinit var adapter: FeaturesCreditApplicationListingsAdapter

    companion object {
        private const val EXTRA_KEY_CREDIT_TYPE = "EXTRA_KEY_CREDIT_TYPE"
        private const val EXTRA_KEY_SELECTED_LISTINGS = "EXTRA_KEY_SELECTED_LISTINGS"

        // The items of `selectedListingIds` are "$listingId,$listingType", e.g. "123456,A"
        fun launch(
            activity: Activity,
            creditType: SrxCreditMainType,
            selectedListingIdTypes: ArrayList<String>
        ) {
            val intent = Intent(activity, FeaturesCreditApplicationActivity::class.java)
            intent.putExtra(EXTRA_KEY_CREDIT_TYPE, creditType)
            intent.putStringArrayListExtra(EXTRA_KEY_SELECTED_LISTINGS, selectedListingIdTypes)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeRxBus()
        setupExtraParams()
        setupAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun observeRxBus() {
        listenRxBus(NotifyPaymentSuccessEvent::class.java) {
            if (it.source == SubscriptionCreditSource.MY_LISTING_FEATURE
                && it.paymentPurpose == PaymentPurpose.CREDIT
            ) {
                viewModel.getActivateSrxCreditSummary()
            }
        }

        listenRxBus(BookAppointmentEvent::class.java) {
            handleSelectedTimeSlot(it.listingIdType, it.selectedTimeSlot)
        }
    }

    private fun handleSelectedTimeSlot(listingIdType: String, timeSlot: AppointmentDateTime) {
        val srxCreditListingPO =
            viewModel.srxCreditListings.find { it.getListingIdType() == listingIdType } ?: return
        //Update time slot
        srxCreditListingPO.selectedTimeSlot =
            "${timeSlot.dateString} ${timeSlot.getFormattedTime()}"
        when (viewModel.creditType.value) {
            SrxCreditMainType.V360 -> {
                srxCreditListingPO.activationListing?.virtualTour = ListingActivationVirtualTour(
                    virtualTourNewProjectId = null,
                    bookingSlot = timeSlot.unixTime,
                    remarks = null
                )
            }
            SrxCreditMainType.VALUATION -> {
                srxCreditListingPO.activationListing?.valuation = ListingActivationValuation(
                    underConstruction = false,
                    bookingSlot = timeSlot.unixTime,
                    remarks = null
                )
            }
            else -> {
                //do nothing
            }
        }
        //adapter changed
        adapter.notifyDataSetChanged()
    }

    private fun setupExtraParams() {
        intent.getStringArrayListExtra(EXTRA_KEY_SELECTED_LISTINGS)?.run {
            viewModel.selectedListingIds = this.mapNotNull { it }
        } ?: throw Throwable("Selected Listings Ids are missing")

        intent.getSerializableExtra(EXTRA_KEY_CREDIT_TYPE)?.run {
            viewModel.creditType.value = this as SrxCreditMainType
        } ?: throw Throwable("SRX Credit Main Type is missing")
    }

    private fun setupAdapter() {
        val creditType = viewModel.creditType.value ?: return
        adapter = FeaturesCreditApplicationListingsAdapter(
            creditType,
            viewModel.srxCreditListings,
            onRemoveListing = { _: ActivateSrxCreditListingPO, position: Int ->
                onRemoveListing(position)
            },
            onSelectEachItemOptions = { viewModel.updateActivateSrxCreditSummary(it) },
            onClickBookAppointment = { onClickBookAppointment(it) },
            onRemoveBooking = { onRemoveBooking(it) },
            onUpdateValuation = { onUpdateValuation(it) })
        binding.listSelectedListings.layoutManager = LinearLayoutManager(this)
        binding.listSelectedListings.adapter = adapter
    }

    private fun onRemoveListing(position: Int) {
        viewModel.srxCreditListings.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, viewModel.srxCreditListings.count())

        //Note: if not more listings in screen, close the screen
        if (viewModel.srxCreditListings.isEmpty()) {
            onBackPressed()
        }
    }

    private fun onUpdateValuation(srxCreditListingPO: ActivateSrxCreditListingPO) {
        srxCreditListingPO.activationListing?.valuation = ListingActivationValuation(
            underConstruction = true,
            bookingSlot = null,
            remarks = null
        )
    }

    private fun onRemoveBooking(creditListingPO: ActivateSrxCreditListingPO) {
        when (viewModel.creditType.value) {
            SrxCreditMainType.V360 -> {
                creditListingPO.activationListing?.virtualTour?.bookingSlot = null
                creditListingPO.optionalRemark = null
            }
            SrxCreditMainType.VALUATION -> {
                creditListingPO.activationListing?.valuation = null
                creditListingPO.optionalRemark = null
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun observeLiveData() {
        viewModel.creditType.observeNotNull(this) { creditMainType ->
            binding.collapsingToolbar.title = getString(creditMainType.label)
            viewModel.getActivateSrxCreditSummary()
        }

        viewModel.mainResponse.observeNotNull(this) { response ->
            response?.summary?.run {
                //update summary value
                viewModel.creditSummaryPO.postValue(this)

                viewModel.deductibleCredits.value =
                    this@FeaturesCreditApplicationActivity.resources.getQuantityString(
                        R.plurals.label_credit_count,
                        creditsDeductible,
                        creditsDeductible.toString()
                    )
                viewModel.availableCredits.value =
                    this@FeaturesCreditApplicationActivity.resources.getQuantityString(
                        R.plurals.label_credit_count,
                        creditsAvailable,
                        creditsAvailable.toString()
                    )

                //populate selected listing in list view
                val srxActivationListings = listings ?: return@run
                if (srxActivationListings.isNotEmpty()) {
                    viewModel.isAllListingInactivate =
                        srxActivationListings.filter { !it.srxCreditActivationAvailability }.size == srxActivationListings.size
                    viewModel.srxCreditListings.clear()
                    srxActivationListings.map {
                        it.activationListing = viewModel.activationListingsTemp[it.id]
                        return@map viewModel.srxCreditListings.add(it)
                    }

                    adapter.notifyDataSetChanged()
                }

            } //end of checking summary null
        }

        viewModel.isCheckAgreement.observeNotNull(this) { isChecked ->
            viewModel.creditSummaryPO.value?.let { summary ->
                when {
                    summary.creditsAvailable < summary.creditsDeductible -> {
                        viewModel.isConfirmButtonEnabled.postValue(false)
                    }
                    viewModel.isAllListingInactivate -> {
                        viewModel.isConfirmButtonEnabled.postValue(false)
                    }
                    else -> {
                        viewModel.isConfirmButtonEnabled.postValue(isChecked)
                    }
                }
            }
        }

        viewModel.activateSrxCreditStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //DO nothing
                }
            }
        }

        viewModel.activationListingsUpdatedList.observeNotNull(this) { items ->
            viewModel.srxCreditListings.map { it.activationListing = items[it.id] }
            adapter.notifyDataSetChanged()
        }
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnConfirm.setOnClickListener {
            val isChecked = viewModel.isCheckAgreement.value ?: false
            if (isChecked) {
                showConfirmationDialog()
            } else {
                ViewUtil.showMessage(R.string.msg_agree_acknowledged)
            }
        }
        binding.ibTopUpCredits.setOnClickListener { showCreditPackageDetailScreen() }
        binding.ibWallet.setOnClickListener { showCreditPackageDetailScreen() }
        binding.btnHelp.setOnClickListener { showFeaturedListingTypesDialog() }
        binding.tvDownloadValuationSample.setOnClickListener { viewValuationSampleReport() }
    }

    private fun viewValuationSampleReport() {
        val fileName = AppConstant.FILE_NAME_VALUATION_SAMPLE_REPORT
        val directory = AppConstant.DIR_MISC

        if (FileUtil.isFileExist(this, fileName, directory)) {
            IntentUtil.viewPdf(this, fileName, directory)
        } else {
            try {
                ViewUtil.showMessage(R.string.toast_downloading_valuation_sample_report)
                viewModel.downloadValuationSampleReport(fileName)
            } catch (e: IOException) {
                ViewUtil.showMessage(R.string.toast_download_failed)
            }
        }
    }

    private fun onClickBookAppointment(creditListingPO: ActivateSrxCreditListingPO) {
        BookAppointmentDialogFragment.launch(
            fragmentManager = supportFragmentManager,
            listingIdTypes = creditListingPO.getListingIdType(),
            timeSlots = creditListingPO.availableBookingSlots
        )
    }

    private fun showConfirmationDialog() {
        val creditType = viewModel.creditType.value ?: return
        val confirmationMessage = when (viewModel.creditType.value) {
            SrxCreditMainType.FEATURED_LISTING -> getString(R.string.msg_feature_listing_confirm_before_activate)
            else -> getString(R.string.msg_confirm_before_activate, getString(creditType.typeLabel))
        }
        DialogUtil(this).showActionDialog(confirmationMessage) {
            viewModel.activateSrxCreditForListings()
        }
    }

    private fun showCreditPackageDetailScreen() {
        SubscriptionCreditPackageDetailsActivity.launch(
            context = this,
            paymentPurpose = PaymentPurpose.CREDIT,
            source = SubscriptionCreditSource.MY_LISTING_FEATURE,
            creditType = viewModel.creditType.value?.value
        )
    }

    private fun showFeaturedListingTypesDialog() {
        FeaturedListingTypesDialogFragment.launch(supportFragmentManager)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_features_credit_application
    }

    override fun getViewModelClass(): Class<FeaturesCreditApplicationViewModel> {
        return FeaturesCreditApplicationViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}

