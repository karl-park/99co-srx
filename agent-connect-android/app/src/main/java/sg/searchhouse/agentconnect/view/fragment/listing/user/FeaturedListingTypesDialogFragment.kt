package sg.searchhouse.agentconnect.view.fragment.listing.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_featured_listing_types.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.DialogFragmentFeaturedListingTypesBinding
import sg.searchhouse.agentconnect.util.FileUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.FeaturedListingTypesDialogViewModel
import java.io.IOException

class FeaturedListingTypesDialogFragment :
    ViewModelFullWidthDialogFragment<FeaturedListingTypesDialogViewModel, DialogFragmentFeaturedListingTypesBinding>() {
    companion object {
        const val TAG = "FeaturedListingTypesDialogFragment"

        fun newInstance(): FeaturedListingTypesDialogFragment {
            return FeaturedListingTypesDialogFragment()
        }

        fun launch(supportFragmentManager: FragmentManager) {
            newInstance().show(supportFragmentManager, TAG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btn_view_product_info_sheet.setOnClickListener {
            viewProductInfoSheet()
        }
    }

    private fun viewProductInfoSheet() {
        activity?.run {
            val fileName = AppConstant.FILE_NAME_FEATURED_LISTING_TYPES
            val directory = AppConstant.DIR_MISC

            if (FileUtil.isFileExist(this, fileName, directory)) {
                IntentUtil.viewPdf(this, fileName, directory)
            } else {
                try {
                    ViewUtil.showMessage(R.string.toast_downloading_featured_listing_info)
                    viewModel.downloadProductInfoSheet(fileName, directory) {
                        // Do nothing
                    }
                } catch (e: IOException) {
                    ViewUtil.showMessage(R.string.toast_download_failed)
                }
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_featured_listing_types
    }

    override fun getViewModelClass(): Class<FeaturedListingTypesDialogViewModel> {
        return FeaturedListingTypesDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        // Do nothing
    }

    override fun getViewModelKey(): String? {
        return null
    }
}