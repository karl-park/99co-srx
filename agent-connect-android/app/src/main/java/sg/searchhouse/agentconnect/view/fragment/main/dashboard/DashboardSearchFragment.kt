package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_dashboard_search.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardSearchBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.app.UpdateUserProfileEvent
import sg.searchhouse.agentconnect.event.dashboard.LaunchSearchEvent
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardSearchViewModel

// TODO Remove
class DashboardSearchFragment :
    ViewModelFragment<DashboardSearchViewModel, FragmentDashboardSearchBinding>() {
    companion object {
        fun newInstance() = DashboardSearchFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listenRxBus(UpdateUserProfileEvent::class.java) {
            viewModel.getAppGreeting()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_search.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.SEARCH_PANEL,
                onSuccessAccessibility = { RxBus.publish(LaunchSearchEvent()) })
        }

        viewModel.greeting.observe(this, Observer { greeting ->
            tv_greeting.text = StringUtil.getSpannedFromHtml(greeting)
        })
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_search
    }

    override fun getViewModelClass(): Class<DashboardSearchViewModel> {
        return DashboardSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}