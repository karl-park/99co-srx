package sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_certified_listing_info.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCertifiedListingInfoBinding
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment

class CertifiedListingHelpDialogFragment : FullScreenDialogFragment() {

    companion object {
        private const val TAG_CERTIFIED_LISTING_DIALOG_FRAGMENT =
            "TAG_CERTIFIED_LISTING_DIALOG_FRAGMENT"

        fun newInstance() = CertifiedListingHelpDialogFragment()

        fun launch(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG_CERTIFIED_LISTING_DIALOG_FRAGMENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<DialogFragmentCertifiedListingInfoBinding>(
            inflater,
            R.layout.dialog_fragment_certified_listing_info,
            container,
            false

        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tb_certified_listings.setNavigationOnClickListener { dialog?.dismiss() }
    }

}