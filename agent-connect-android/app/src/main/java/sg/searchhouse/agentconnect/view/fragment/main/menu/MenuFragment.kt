package sg.searchhouse.agentconnect.view.fragment.main.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_menu.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.Endpoint
import sg.searchhouse.agentconnect.databinding.FragmentMenuBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum.AdvisorModule
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.CalculatorType
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.SellingCalculatorEntryType
import sg.searchhouse.agentconnect.enumeration.app.SettingMenu
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.event.app.SwipeMainPageEvent
import sg.searchhouse.agentconnect.event.app.UpdateUserProfileEvent
import sg.searchhouse.agentconnect.tracking.MenuListTracker
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.view.activity.agent.client.MySgHomeClientsActivity
import sg.searchhouse.agentconnect.view.activity.agent.profile.AgentProfileActivity
import sg.searchhouse.agentconnect.view.activity.calculator.AdvancedAffordabilityCalculatorActivity
import sg.searchhouse.agentconnect.view.activity.calculator.AffordabilityCalculatorActivity
import sg.searchhouse.agentconnect.view.activity.calculator.CalculatorsActivity
import sg.searchhouse.agentconnect.view.activity.calculator.SellingCalculatorActivity
import sg.searchhouse.agentconnect.view.activity.listing.MyListingsActivity
import sg.searchhouse.agentconnect.view.activity.propertynews.PropertyNewsActivity
import sg.searchhouse.agentconnect.view.adapter.main.MainBottomNavigationTab
import sg.searchhouse.agentconnect.view.adapter.main.MainPagerAdapter
import sg.searchhouse.agentconnect.view.adapter.main.MenuAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.fragment.calculator.StampDutyDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.menu.MenuViewModel

class MenuFragment : ViewModelFragment<MenuViewModel, FragmentMenuBinding>() {

    private lateinit var mContext: Context
    private lateinit var menuAdapter: MenuAdapter

    private var stampDutyDialogFragment: StampDutyDialogFragment? = null

    companion object {
        const val REQUEST_CODE_AGENT_PROFILE = 1

        fun newInstance() = MenuFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listenRxBuses()
        setupAdapterList()
        populateMenusByStreetSineUser()
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateUserProfileEvent::class.java) {
            //refresh menu lists
            populateMenusByStreetSineUser()
            menuAdapter.updateUserProfile()
        }
    }

    private fun setupAdapterList() {
        menuAdapter = MenuAdapter(
            mContext,
            onItemClickListener = { settingMenu -> onClickMenuItem(settingMenu) },
            onCalculatorClickListener = {
                onSelectCalculator(it)
            })
        list.layoutManager = LinearLayoutManager(this.activity)
        list.adapter = menuAdapter
    }

    private fun onSelectCalculator(calculatorType: CalculatorType?) {
        // tracking use calculator type
        MenuListTracker.trackCalculatorMenuOptionClicked(requireContext(), calculatorType)
        
        when (calculatorType) {
            CalculatorType.AFFORDABILITY_QUICK -> {
                launchActivity(AffordabilityCalculatorActivity::class.java)
            }
            CalculatorType.AFFORDABILITY_ADVANCED -> {
                launchActivity(AdvancedAffordabilityCalculatorActivity::class.java)
            }
            CalculatorType.SELLING -> {
                context?.run {
                    SellingCalculatorActivity.launch(this, SellingCalculatorEntryType.SELLING)
                }
            }
            CalculatorType.STAMP_DUTY -> {
                showStampDutyDialogFragment()
            }
            else -> {
                launchActivity(CalculatorsActivity::class.java)
            }
        }
    }

    private fun showStampDutyDialogFragment() {
        if (stampDutyDialogFragment != null) {
            if (stampDutyDialogFragment?.isVisible != true) {
                stampDutyDialogFragment?.show(childFragmentManager, StampDutyDialogFragment.TAG)
            }
        } else {
            stampDutyDialogFragment = StampDutyDialogFragment.newInstance()
            stampDutyDialogFragment?.show(childFragmentManager, StampDutyDialogFragment.TAG)
        }
    }

    private fun onClickMenuItem(menu: SettingMenu) {
        when (menu) {
            SettingMenu.MY_PROFILE -> {
                trackMenuItem(menu)
                AuthUtil.checkModuleAccessibility(
                    module = AdvisorModule.AGENT_PROFILE,
                    onSuccessAccessibility = { directToAgentProfile() })
            }
            SettingMenu.MY_LISTINGS -> {
                trackMenuItem(menu)
                AuthUtil.checkModuleAccessibility(
                    module = AdvisorModule.LISTING_MANAGEMENT,
                    onSuccessAccessibility = { directToMyListings() })
            }
            SettingMenu.MY_CLIENTS -> {
                trackMenuItem(menu)
                AuthUtil.checkModuleAccessibility(
                    module = AdvisorModule.MY_CLIENTS,
                    onSuccessAccessibility = { showMyClients() })
            }
            SettingMenu.NEWS -> {
                trackMenuItem(menu)
                AuthUtil.checkModuleAccessibility(
                    module = AdvisorModule.NEWS,
                    onSuccessAccessibility = { directToPropertyNews() })
            }
            SettingMenu.CALCULATORS -> {
                trackMenuItem(menu)
                directToCalculator()
            }
            SettingMenu.FIND_AGENT -> {
                trackMenuItem(menu)
                AuthUtil.checkModuleAccessibility(
                    module = AdvisorModule.AGENT_DIRECTORY,
                    onSuccessAccessibility = { directToFindAgent() })
            }
            SettingMenu.PRIVACY_STATEMENT -> {
                IntentUtil.visitSrxUrl(mContext, Endpoint.PRIVACY_POLICY)
            }
            SettingMenu.TERMS_OF_USE -> {
                IntentUtil.visitSrxUrl(mContext, Endpoint.TERMS_OF_USE)
            }
            SettingMenu.TERMS_OF_SALE -> {
                IntentUtil.visitSrxUrl(mContext, Endpoint.TERMS_OF_SALE)
            }
            SettingMenu.SWITCH_SERVER -> {
                showSwitchServerDialog()
            }
            SettingMenu.LOGIN_AS_AGENT -> {
                val streetSineUser = SessionUtil.getStreetSineUser()
                if (streetSineUser == null) {
                    showLoginAsAgentDialog()
                } else {
                    //TODO: remove cookies
                    if (SessionUtil.removeLoginAgentCookie()) {
                        SessionUtil.removeStreetSineUser()
                        RxBus.publish(LoginAsAgentEvent())
                    } else {
                        Log.e("no", "Couldn't remove key")
                    }
                }
            }
            SettingMenu.SIGN_OUT -> {
                SessionUtil.signOut(isUserAction = true)
            }
        }
    }

    private fun trackMenuItem(menu: SettingMenu) {
        MenuListTracker.trackMenuItemClicked(requireContext(), menu)
    }

    private fun populateMenusByStreetSineUser() {
        val currentUser = SessionUtil.getCurrentUser()
        val isAgentLoggedIn = SessionUtil.getStreetSineUser()?.debugAllowed ?: false
        if (currentUser != null && currentUser.debugAllowed || isAgentLoggedIn) {
            menuAdapter.updateMenuItems(SettingMenu.values().toList())
        } else {
            menuAdapter.updateMenuItems(
                listOf(
                    SettingMenu.MY_PROFILE,
                    SettingMenu.MY_LISTINGS,
                    SettingMenu.MY_CLIENTS,
                    SettingMenu.NEWS,
                    SettingMenu.CALCULATORS,
                    SettingMenu.FIND_AGENT,
                    SettingMenu.PRIVACY_STATEMENT,
                    SettingMenu.TERMS_OF_USE,
                    SettingMenu.TERMS_OF_SALE,
                    SettingMenu.SIGN_OUT

                )
            )
        }
    }

    private fun showMyClients() {
        startActivity(Intent(activity, MySgHomeClientsActivity::class.java))
    }

    private fun directToCalculator() {
        startActivity(Intent(activity, CalculatorsActivity::class.java))
    }

    private fun directToAgentProfile() {
        activity?.run {
            AgentProfileActivity.launchForResult(this, requestCode = REQUEST_CODE_AGENT_PROFILE)
        }
    }

    private fun directToFindAgent() {
        publishRxBus(SwipeMainPageEvent(MainBottomNavigationTab.FIND_AGENT.position))
    }

    private fun directToMyListings() {
        val intent = Intent(activity, MyListingsActivity::class.java)
        startActivity(intent)
    }

    private fun directToPropertyNews() {
        startActivity(Intent(activity, PropertyNewsActivity::class.java))
    }

    private fun showSwitchServerDialog() {
        val fm = activity?.supportFragmentManager ?: return
        SwitchServerDialogFragment.launch(fm)
    }

    private fun showLoginAsAgentDialog() {
        val fm = activity?.supportFragmentManager ?: return
        LoginAsAgentDialogFragment.launch(fm)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_menu
    }

    override fun getViewModelClass(): Class<MenuViewModel> {
        return MenuViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}