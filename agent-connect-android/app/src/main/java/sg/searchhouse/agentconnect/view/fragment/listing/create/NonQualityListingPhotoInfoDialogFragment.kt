package sg.searchhouse.agentconnect.view.fragment.listing.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_non_quality_listing_photo.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentNonQualityListingPhotoBinding
import sg.searchhouse.agentconnect.view.fragment.base.FullWidthDialogFragment

class NonQualityListingPhotoInfoDialogFragment : FullWidthDialogFragment() {

    private lateinit var binding: DialogFragmentNonQualityListingPhotoBinding

    companion object {
        const val TAG = "NON_QUALITY_LISTING_PHOTO_DIALOG_FRAGMENT"

        fun newInstance(): NonQualityListingPhotoInfoDialogFragment {
            return NonQualityListingPhotoInfoDialogFragment()
        }

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
            LayoutInflater.from(context),
            R.layout.dialog_fragment_non_quality_listing_photo,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        iv_close.setOnClickListener { dialog?.dismiss() }
    }
}