package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import android.view.View
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.event.agent.NotifyRetrieveConfigEvent
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardViewModel

class DashboardFragment : ViewModelFragment<DashboardViewModel, FragmentDashboardBinding>() {
    companion object {
        fun newInstance() =
            DashboardFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListenRxBus()
        checkModuleAccessibilities()
        binding.layoutContainer.setupLayoutAnimation()
    }

    private fun setupListenRxBus() {
        listenRxBus(NotifyRetrieveConfigEvent::class.java) {
            checkModuleAccessibilities()
        }
    }

    private fun checkModuleAccessibilities() {
        //for market watch
        viewModel.shouldShowMarketWatch.postValue(
            AuthUtil.isAccessibilityModule(
                module = AccessibilityEnum.AdvisorModule.DASHBOARD,
                function = AccessibilityEnum.InAccessibleFunction.DASHBOARD_MARKET_WATCH
            )
        )

        //watch list
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.WATCHLISTS,
            onSuccessAccessibility = { viewModel.shouldShowWatchList.postValue(true) },
            onFailAccessibility = { viewModel.shouldShowWatchList.postValue(false) }
        )
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}