package sg.searchhouse.agentconnect.view.fragment.listing.search

import android.os.Bundle
import android.view.View
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentExportListingsBinding
import sg.searchhouse.agentconnect.databinding.TabLayoutExportListingsBinding
import sg.searchhouse.agentconnect.dsl.findChildLayoutBindingById
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.listing.search.ExportListingsEvent
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.search.ExportListingsDialogViewModel

class ExportListingsDialogFragment :
    ViewModelFullWidthDialogFragment<ExportListingsDialogViewModel, DialogFragmentExportListingsBinding>() {
    companion object {
        const val TAG = "ExportListingsDialogFragment"
        private const val ARGUMENT_LISTING_COUNT = "ARGUMENT_LISTING_SIZE"

        fun newInstance(listingCount: Int): ExportListingsDialogFragment {
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_LISTING_COUNT, listingCount)
            val fragment = ExportListingsDialogFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    private fun getTabLayoutExportListingsBinding(): TabLayoutExportListingsBinding = binding.findChildLayoutBindingById(R.id.tab_layout_export_listings)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArguments()
        setOnClickListeners()
    }

    private fun setupArguments() {
        val listingCount = arguments?.getInt(ARGUMENT_LISTING_COUNT)
            ?: throw IllegalArgumentException("Missing listing size argument")
        viewModel.listingCount.postValue(listingCount)
    }

    private fun setOnClickListeners() {
        binding.ibClose.setOnClickListener { dialog?.dismiss() }
        binding.btnDownload.setOnClickListener {
            // Guaranteed !!, exportOption can't be null
            RxBus.publish(
                ExportListingsEvent(
                    viewModel.exportOption.value!!,
                    binding.cbAgentContact.isChecked,
                    binding.cbPhoto.isChecked
                )
            )
            dialog?.dismiss()
        }
        getTabLayoutExportListingsBinding().btnTabPdf.setOnClickListener {
            viewModel.exportOption.postValue(ListingEnum.ExportOption.PDF)
        }
        getTabLayoutExportListingsBinding().btnTabExcel.setOnClickListener {
            viewModel.exportOption.postValue(ListingEnum.ExportOption.EXCEL)
        }
        binding.cbPhoto.setOnClickListener { viewModel.toggleExportListingDetails() }
    }

    override fun getLayoutResId(): Int = R.layout.dialog_fragment_export_listings

    override fun getViewModelClass(): Class<ExportListingsDialogViewModel> =
        ExportListingsDialogViewModel::class.java

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? = null
}