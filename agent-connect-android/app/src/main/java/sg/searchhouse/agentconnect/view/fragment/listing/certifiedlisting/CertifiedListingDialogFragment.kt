package sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.layout_action_success.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCertifiedListingBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.certifiedlisting.CertifiedListingViewModel

class CertifiedListingDialogFragment :
    ViewModelDialogFragment<CertifiedListingViewModel, DialogFragmentCertifiedListingBinding>() {

    companion object {
        private const val TAG_CERTIFIED_LISTING_DIALOG_FRAGMENT =
            "TAG_CERTIFIED_LISTING_DIALOG_FRAGMENT"
        private const val ARGUMENT_KEY_LISTING_ID_AND_TYPE = "ARGUMENT_KEY_LISTING_ID"

        fun newInstance(listingIdType: String): CertifiedListingDialogFragment {
            val dialogFragment = CertifiedListingDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARGUMENT_KEY_LISTING_ID_AND_TYPE, listingIdType)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragmentManager: FragmentManager, listingIdType: String) {
            newInstance(listingIdType).show(fragmentManager, TAG_CERTIFIED_LISTING_DIALOG_FRAGMENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArgumentParams()
        observeLiveData()
        setOnClickListeners()
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        bundle.getString(ARGUMENT_KEY_LISTING_ID_AND_TYPE, null)?.run {
            viewModel.listingIdType.value = this
        } ?: throw Throwable("Missing listing id in certified listing")
    }

    private fun observeLiveData() {
        viewModel.listingIdType.observeNotNull(this) {
            viewModel.getListingById(it)
        }

        viewModel.requestHomeOwnerStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    println("Done")
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.toolbar.setNavigationOnClickListener { dialog?.dismiss() }
        binding.tvLearnMore.setOnClickListener { showCertifiedListingHelp() }
        btn_back.setOnClickListener { dialog?.dismiss() }
    }

    private fun showCertifiedListingHelp() {
        activity?.run { CertifiedListingHelpDialogFragment.launch(supportFragmentManager) }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_certified_listing
    }

    override fun getViewModelClass(): Class<CertifiedListingViewModel> {
        return CertifiedListingViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}