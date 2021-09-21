package sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_home_owner_detail.*
import kotlinx.android.synthetic.main.layout_publish_listing_success_sponsor.*
import kotlinx.android.synthetic.main.list_item_listing.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentHomeOwnerDetailBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.FAIL
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.SUCCESS
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.listing.SponsorListingActivity
import sg.searchhouse.agentconnect.view.activity.listing.user.FeaturesCreditApplicationActivity
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.certifiedlisting.HomeOwnerDetailViewModel


class HomeOwnerDetailDialogFragment :
    ViewModelDialogFragment<HomeOwnerDetailViewModel, DialogFragmentHomeOwnerDetailBinding>() {

    private lateinit var mContext: Context

    companion object {
        private const val TAG_HOME_OWNER_DETAIL_DIALOG = "TAG_HOME_OWNER_DETAIL_DIALOG"
        private const val EXTRA_KEY_LISTING_ID = "EXTRA_KEY_LISTING_ID"

        fun newInstance(listingId: String): HomeOwnerDetailDialogFragment {
            val dialogFragment = HomeOwnerDetailDialogFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_KEY_LISTING_ID, listingId)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragmentManager: FragmentManager, listingId: String) {
            newInstance(listingId).show(fragmentManager, TAG_HOME_OWNER_DETAIL_DIALOG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExtraParams()
        setupListingInteraction()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtraParams() {
        val bundle = arguments ?: return
        viewModel.listingId.value = bundle.getString(EXTRA_KEY_LISTING_ID, "")
    }

    private fun setupListingInteraction() {
        //disable interaction listing list item
        layout_listing_list_item.isFocusable = false
        layout_listing_list_item.isClickable = false
        layout_listing_list_item.foreground = null
    }

    private fun observeLiveData() {
        viewModel.listingId.observe(viewLifecycleOwner) {
            val id = it ?: return@observe
            viewModel.getListingById(id)
        }

        viewModel.mainResponse.observe(viewLifecycleOwner) { response ->
            val listing = response?.fullListing?.listingDetailPO?.listingPO ?: return@observe
            viewModel.listingPO.postValue(listing)
        }

        viewModel.requestOwnerStatus.observe(viewLifecycleOwner) { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    viewModel.notifiedHomeOwner.postValue(true)
                    ViewUtil.showMessage(mContext.getString(R.string.msg_notify_homeowner))
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun handleListeners() {
        btn_featured_listing.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_FEATURE_LISTINGS,
                onSuccessAccessibility = {
                    val listingIdType = viewModel.listingPO.value?.getListingIdType() ?: ""
                    val activity = this.activity ?: return@checkModuleAccessibility
                    FeaturesCreditApplicationActivity.launch(
                        activity, ListingManagementEnum.SrxCreditMainType.FEATURED_LISTING,
                        arrayListOf(listingIdType)
                    )
                })
        }

        tv_direct_to_dashboard.setOnClickListener { dialog?.dismiss() }

        btn_enter_sponsor.setOnClickListener {
            viewModel.listingId.value?.toIntOrNull()?.run {
                val mActivity = activity as BaseActivity
                SponsorListingActivity.launch(mActivity, this)
            } ?: ErrorUtil.handleError("Missing listing ID")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_home_owner_detail
    }

    override fun getViewModelClass(): Class<HomeOwnerDetailViewModel> {
        return HomeOwnerDetailViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}