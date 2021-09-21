package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.card_dashboard_user_listings_occupied.*
import kotlinx.android.synthetic.main.fragment_dashboard_user_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardUserListingsBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.event.dashboard.UpdateDashboardListingsCountEvent
import sg.searchhouse.agentconnect.event.dashboard.ViewMyListingsEvent
import sg.searchhouse.agentconnect.event.listing.create.NotifyPostListingEvent
import sg.searchhouse.agentconnect.event.listing.user.LaunchCreateListingEvent
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardUserListingsViewModel

class DashboardUserListingsFragment :
    ViewModelFragment<DashboardUserListingsViewModel, FragmentDashboardUserListingsBinding>() {

    companion object {
        fun newInstance() = DashboardUserListingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performRequest()
        observeRxBuses()
    }

    private fun observeRxBuses() {
        listenRxBus(LoginAsAgentEvent::class.java) {
            performRequest()
        }
        listenRxBus(NotifyPostListingEvent::class.java) {
            performRequest()
        }
        listenRxBus(UpdateDashboardListingsCountEvent::class.java) {
            performRequest()
        }
    }

    private fun performRequest() {
        viewModel.getListingSummary()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btn_view_all_active_sale_listings.setOnClickListener {
            RxBus.publish(ViewMyListingsEvent(ListingEnum.OwnershipType.SALE))
        }

        btn_view_all_active_rent_listings.setOnClickListener {
            RxBus.publish(ViewMyListingsEvent(ListingEnum.OwnershipType.RENT))
        }

        card_user_listings_empty.showCreateListingDialog = {
            RxBus.publish(LaunchCreateListingEvent(isFromMyListing = false))
        }
    }
    
    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_user_listings
    }

    override fun getViewModelClass(): Class<DashboardUserListingsViewModel> {
        return DashboardUserListingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}