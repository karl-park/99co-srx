package sg.searchhouse.agentconnect.view.fragment.listing.portal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_import_listings_disclaimer.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentImportListingsDisclaimerBinding
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment


class ImportListingsDisclaimerDialogFragment : FullScreenDialogFragment() {

    private lateinit var binding: DialogFragmentImportListingsDisclaimerBinding

    companion object {
        private const val TAG = "IMPORT_LISTINGS_DISCLAIMER"

        fun newInstance() = ImportListingsDisclaimerDialogFragment()

        fun launch(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_import_listings_disclaimer,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        btn_dismiss.setOnClickListener { dialog?.dismiss() }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow(backgroundColor = R.color.transparent)
    }

}