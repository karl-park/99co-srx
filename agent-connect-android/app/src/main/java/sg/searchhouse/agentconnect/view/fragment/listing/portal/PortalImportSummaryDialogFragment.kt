package sg.searchhouse.agentconnect.view.fragment.listing.portal

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.layout_import_summary_main.*
import kotlinx.android.synthetic.main.layout_import_summary_preview.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentPortalImportSummaryBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLink
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PortalImportSummarySource
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.listing.MyListingsActivity
import sg.searchhouse.agentconnect.view.activity.listing.portal.PortalListingsActivity
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal.PortalImportSummaryDialogViewModel

class PortalImportSummaryDialogFragment :
    ViewModelFullWidthDialogFragment<PortalImportSummaryDialogViewModel, DialogFragmentPortalImportSummaryBinding>() {

    companion object {
        private const val TAG_IMPORT_LISTINGS_SUMMARY = "TAG_IMPORT_LISTINGS_SUMMARY"
        private const val EXTRA_KEY_PORTAL_IMPORT_SUMMARY_SOURCE = "EXTRA_KEY_PORTAL_IMPORT_SUMMARY"
        private const val EXTRA_KEY_SUCCESS_LISTINGS_COUNT = "EXTRA_KEY_SUCCESS_LISTINGS_COUNT"
        private const val EXTRA_KEY_FAILED_LISTINGS_COUNT = "EXTRA_KEY_FAILED_LISTINGS_COUNT"

        fun newInstance(
            source: PortalImportSummarySource,
            successCount: Int,
            failedCount: Int
        ): PortalImportSummaryDialogFragment {
            val dialogFragment = PortalImportSummaryDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_PORTAL_IMPORT_SUMMARY_SOURCE, source)
            bundle.putInt(EXTRA_KEY_SUCCESS_LISTINGS_COUNT, successCount)
            bundle.putInt(EXTRA_KEY_FAILED_LISTINGS_COUNT, failedCount)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(
            fragmentManager: FragmentManager,
            source: PortalImportSummarySource,
            successCount: Int,
            failedCount: Int
        ) {
            newInstance(source, successCount, failedCount).show(
                fragmentManager,
                TAG_IMPORT_LISTINGS_SUMMARY
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExtraParams()
        observeLiveData()
        setupListeners()
    }

    fun setupExtraParams() {
        val bundle = arguments ?: return
        viewModel.successCount = bundle.getInt(EXTRA_KEY_SUCCESS_LISTINGS_COUNT, 0)
        viewModel.failedCount = bundle.getInt(EXTRA_KEY_FAILED_LISTINGS_COUNT, 0)
        bundle.getSerializable(EXTRA_KEY_PORTAL_IMPORT_SUMMARY_SOURCE)?.run {
            viewModel.summarySource.postValue(this as PortalImportSummarySource)
        } ?: throw IllegalArgumentException("Undefined import listing summary source")
    }

    private fun observeLiveData() {
        viewModel.summarySource.observeNotNull(this) {
            populateSuccessAndFailedListingsCount()
        }
    }

    private fun populateSuccessAndFailedListingsCount() {
        when (viewModel.summarySource.value) {
            PortalImportSummarySource.PORTAL_MAIN_SCREEN -> populateListingCountLabelForMain()
            PortalImportSummarySource.PORTAL_PREVIEW_SCREEN -> populateListingCountsForPreview()
            else -> println("do nothing for null")
        }
    }

    private fun populateListingCountLabelForMain() {
        //SUCCESS listings count
        if (viewModel.successCount > 0) {
            viewModel.successListings.value = activity?.getString(
                R.string.msg_portal_listings_successfully_published,
                this.resources.getQuantityString(
                    R.plurals.label_listings_count,
                    viewModel.successCount,
                    NumberUtil.formatThousand(viewModel.successCount)
                )
            )
        }

        //FAILED listings count
        if (viewModel.failedCount > 0) {
            viewModel.failedListings.value = activity?.getString(
                R.string.msg_portal_listings_failed,
                this.resources.getQuantityString(
                    R.plurals.label_listings_count,
                    viewModel.failedCount,
                    viewModel.failedCount.toString()
                )
            )
            viewModel.isFailedListingsIncluded.value = true
        }
    }

    private fun populateListingCountsForPreview() {
        if (viewModel.successCount > 0) {
            viewModel.previewSuccessCountLabel.value = activity?.getString(
                R.string.msg_portal_listings_successfully_published,
                this.resources.getQuantityString(
                    R.plurals.label_listings_count,
                    viewModel.successCount,
                    NumberUtil.formatThousand(viewModel.successCount)
                )
            )
        }

        if (viewModel.failedCount > 0) {
            val listingFailedCountLabel = activity?.getString(
                R.string.label_preview_failed_listing_count,
                this.resources.getQuantityString(
                    R.plurals.label_listings_count,
                    viewModel.failedCount,
                    NumberUtil.formatThousand(viewModel.failedCount)
                )
            ) ?: ""

            tv_preview_failed_count_label.setupLink(
                listingFailedCountLabel,
                activity?.getString(R.string.label_draft) ?: "",
                onClickListener = {
                    //TODO: currently do nothing
                    println("do nothing for now")
                }
            )
            viewModel.showFailedCountLabel.value = true
        } else {
            viewModel.showFailedCountLabel.value = false
        }
    }

    private fun setupListeners() {
        btn_primary.setOnClickListener {
            viewModel.isFailedListingsIncluded.value?.let {
                if (it) {
                    dialog?.dismiss()
                    (activity as PortalListingsActivity).refreshPortalListings()
                } else {
                    dialog?.dismiss()
                    showMyListingsScreen()
                }
            }
        }

        btn_secondary.setOnClickListener {
            viewModel.isFailedListingsIncluded.value?.let {
                if (it) {
                    dialog?.dismiss()
                    showMyListingsScreen()
                } else {
                    dialog?.dismiss()
                    (activity as PortalListingsActivity).refreshPortalListings()
                }
            }
        }

        tv_add_home_owner_numbers.setOnClickListener {
            activity?.run {
                AutoImportCertifiedListingsDialogFragment.launch(supportFragmentManager)
            }
            dialog?.dismiss()
        }

        tv_do_it_later.setOnClickListener { dialog?.dismiss() }
    }

    private fun showMyListingsScreen() {
        activity?.run {
            this.finish()
            startActivity(Intent(this, MyListingsActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_portal_import_summary
    }

    override fun getViewModelClass(): Class<PortalImportSummaryDialogViewModel> {
        return PortalImportSummaryDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

}