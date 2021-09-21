package sg.searchhouse.agentconnect.view.fragment.listing.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentContainerView
import kotlinx.android.synthetic.main.fragment_featured_prompt.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentFeaturedPromptBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.dashboard.ShowFeaturedListingPromptEvent
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.view.activity.listing.user.FeaturesCreditApplicationActivity
import sg.searchhouse.agentconnect.view.adapter.listing.marketing.FeaturedPromptPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.FeaturedPromptViewModel


class FeaturedPromptFragment :
    ViewModelFragment<FeaturedPromptViewModel, FragmentFeaturedPromptBinding>() {
    private val adapter = FeaturedPromptPagerAdapter { listingIdType ->
        // On click "add now" button
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
            function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_FEATURE_LISTINGS,
            onSuccessAccessibility = {
                launchAddFeaturedListing(listingIdType)
                RxBus.publish(
                    ShowFeaturedListingPromptEvent(isShow = false)
                )
            }
        )
    }

    private fun launchAddFeaturedListing(listingIdType: String) {
        activity?.run {
            FeaturesCreditApplicationActivity.launch(
                this,
                ListingManagementEnum.SrxCreditMainType.FEATURED_LISTING,
                arrayListOf(listingIdType)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        observeLiveData()
        viewModel.getAddonPrompts()
    }

    private fun observeLiveData() {

        viewModel.featuredListingPrompts.observeNotNull(viewLifecycleOwner) { prompts ->
            if (prompts.isNotEmpty()) {
                RxBus.publish(ShowFeaturedListingPromptEvent(isShow = true))
                adapter.run {
                    this.listItems = prompts.take(MAX_LIMIT_ITEM_COUNT)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun setupViewPager() {
        view_pager_indicator.attachToViewPager(view_pager)
        view_pager.adapter = adapter
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_featured_prompt
    }

    override fun getViewModelClass(): Class<FeaturedPromptViewModel> {
        return FeaturedPromptViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

    companion object {
        private const val MAX_LIMIT_ITEM_COUNT = 3

        fun showHide(
            fragmentContainerView: FragmentContainerView,
            isShow: Boolean
        ) {
            fragmentContainerView.run {
                if (isShow && visibility == View.VISIBLE) return
                if (!isShow && visibility == View.INVISIBLE) return

                if (isShow) {
                    visibility = View.VISIBLE
                }
                animate().yBy(
                    if (isShow) {
                        resources.getDimension(R.dimen.margin_bottom_featured_prompt)
                    } else {
                        -resources.getDimension(R.dimen.margin_bottom_featured_prompt)
                    }
                )
                    .setDuration(
                        if (isShow) {
                            resources.getInteger(R.integer.anim_time).toLong()
                        } else {
                            resources.getInteger(R.integer.anim_time_dismiss_featured_prompt)
                                .toLong()
                        }
                    )
                    .withEndAction {
                        if (!isShow) {
                            visibility = View.INVISIBLE
                        }
                    }
            }
        }
    }


}