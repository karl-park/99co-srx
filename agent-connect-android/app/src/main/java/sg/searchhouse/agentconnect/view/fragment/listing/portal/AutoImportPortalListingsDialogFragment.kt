package sg.searchhouse.agentconnect.view.fragment.listing.portal

import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_auto_import_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentAutoImportListingsBinding
import sg.searchhouse.agentconnect.dsl.widget.setupSubTextColor
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.util.JsonUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal.AutoImportPortalListingsViewModel

class AutoImportPortalListingsDialogFragment :
    ViewModelFullWidthDialogFragment<AutoImportPortalListingsViewModel, DialogFragmentAutoImportListingsBinding>() {

    companion object {
        private const val TAG = "AUTO_IMPORT_PORTAL_LISTINGS_DIALOG_FRAGMENT"
        private const val ARGUMENT_KEY_TOTAL = "ARGUMENT_KEY_TOTAL"
        private const val ARGUMENT_KEY_LISTINGS = "ARGUMENT_KEY_LISTINGS"
        private const val ARGUMENT_KEY_PORTAL_ACCOUNT = "ARGUMENT_KEY_PORTAL_ACCOUNT"

        fun newInstance(
            total: Int,
            listings: List<PortalListingPO>,
            portalAccount: PortalAccountPO
        ): AutoImportPortalListingsDialogFragment {
            val dialogFragment = AutoImportPortalListingsDialogFragment()
            val bundle = Bundle()
            bundle.putInt(ARGUMENT_KEY_TOTAL, total)
            bundle.putString(ARGUMENT_KEY_LISTINGS, JsonUtil.parseToJsonString(listings))
            bundle.putSerializable(ARGUMENT_KEY_PORTAL_ACCOUNT, portalAccount)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(
            fragmentManager: FragmentManager,
            total: Int,
            listings: List<PortalListingPO>,
            portalAccount: PortalAccountPO
        ) {
            newInstance(total = total, listings = listings, portalAccount = portalAccount)
                .show(fragmentManager, TAG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArgumentParams()
        observeListeners()
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        //total count
        val total = bundle.getInt(ARGUMENT_KEY_TOTAL, 0)
        showListingCountLabel(total)
        viewModel.listingTotal.postValue(total)

        bundle.getString(ARGUMENT_KEY_LISTINGS)?.run {
            val list = JsonUtil.parseListOrEmpty(this, PortalListingPO::class.java)
            viewModel.listings.postValue(list)
        }

        bundle.getSerializable(ARGUMENT_KEY_PORTAL_ACCOUNT)?.run {
            val portalAccountPO = this as PortalAccountPO
            viewModel.portalAccountPO.postValue(portalAccountPO)
        }
    }

    private fun showListingCountLabel(total: Int) {
        var importAndPostSpannableString: SpannableString? = null

        val listingCountLabel = this.resources.getQuantityString(
            R.plurals.auto_import_listing_count,
            total,
            NumberUtil.formatThousand(total)
        )
        val foundListingsLabel =
            getString(R.string.label_auto_import_listing_count, listingCountLabel)
        tv_listing_count_label.setupSubTextColor(
            R.color.purple,
            foundListingsLabel,
            listingCountLabel
        )

        activity?.run {
            //TODO: to refactor this
            val importAndPostString = getString(R.string.label_import_and_post_listings)
            importAndPostSpannableString = SpannableString(importAndPostString)
            val firstPair = viewModel.getStartAndEndIndexOfTextLink(
                importAndPostString,
                getString(R.string.label_free_caps)
            )
            importAndPostSpannableString?.setSpan(
                ForegroundColorSpan(getColor(R.color.cyan)),
                firstPair.first,
                firstPair.second,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )

            val secondPair = viewModel.getStartAndEndIndexOfTextLink(
                importAndPostString,
                getString(R.string.label_srx)
            )
            importAndPostSpannableString?.setSpan(
                ForegroundColorSpan(getColor(R.color.purple)),
                secondPair.first,
                secondPair.second,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }?.also {
            tv_import_label.text = importAndPostSpannableString
        }
    }

    private fun observeListeners() {
        tv_skip_now.setOnClickListener { dialog?.dismiss() }
        tv_do_not_remind_me.setOnClickListener {
            SessionUtil.setAutoImportListingsDialogShowFlag(false)
            dialog?.dismiss()
        }
        btn_preview_listings.setOnClickListener {
            showImportListingsPreview()
        }
    }

    private fun showImportListingsPreview() {
        activity?.run {
            val total = viewModel.listingTotal.value ?: return@run
            val listings = viewModel.listings.value ?: return@run
            val portalAccount = viewModel.portalAccountPO.value ?: return@run
            AutoImportListingsPreviewDialogFragment.launch(
                fragmentManager = supportFragmentManager,
                totalCount = total,
                listings = listings,
                portalAccountPO = portalAccount
            )
        }
        dialog?.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        println("do something")
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_auto_import_listings
    }

    override fun getViewModelClass(): Class<AutoImportPortalListingsViewModel> {
        return AutoImportPortalListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}