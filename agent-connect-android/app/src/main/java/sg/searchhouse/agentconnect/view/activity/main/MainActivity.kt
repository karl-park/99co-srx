package sg.searchhouse.agentconnect.view.activity.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityMainBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.addOnPageSelectedListener
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum.AdvisorModule
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.agent.NotifyRetrieveConfigEvent
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.event.app.SwipeMainPageEvent
import sg.searchhouse.agentconnect.event.app.UpdateUserProfileEvent
import sg.searchhouse.agentconnect.event.chat.ChatEditEvent
import sg.searchhouse.agentconnect.event.chat.ChatRefreshEvent
import sg.searchhouse.agentconnect.event.dashboard.LaunchSearchEvent
import sg.searchhouse.agentconnect.event.dashboard.ShowFeaturedListingPromptEvent
import sg.searchhouse.agentconnect.event.dashboard.SubscriberPromptDismissEvent
import sg.searchhouse.agentconnect.event.dashboard.ViewMyListingsEvent
import sg.searchhouse.agentconnect.event.listing.user.LaunchCreateListingEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.tracking.NavigationBarTabTracker
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.agent.profile.AgentProfileActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.listing.MyListingsActivity
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.activity.report.ReportsAndResearchActivity
import sg.searchhouse.agentconnect.view.adapter.main.MainBottomNavigationTab
import sg.searchhouse.agentconnect.view.adapter.main.MainPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.create.ListingAddressSearchDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.portal.AutoImportCertifiedListingsDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.portal.AutoImportPortalListingsDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.user.FeaturedPromptFragment
import sg.searchhouse.agentconnect.view.fragment.main.menu.MenuFragment
import sg.searchhouse.agentconnect.view.widget.main.BottomAppBar
import sg.searchhouse.agentconnect.viewmodel.activity.main.MainViewModel

class MainActivity : ViewModelActivity<MainViewModel, ActivityMainBinding>() {

    private var forceUpdateVersionDialog: AlertDialog? = null

    companion object {
        const val EXTRA_KEY_ENQUIRY_ID = "EXTRA_KEY_ENQUIRY_ID"

        fun launch(activity: Activity, enquiryId: String? = null) {
            val intent = Intent(activity, MainActivity::class.java)
            enquiryId?.let { intent.putExtra(EXTRA_KEY_ENQUIRY_ID, enquiryId) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        performRequests()
        listenRxBuses()
        observeLiveData()
        setupEnquiryIdParamFromExtras()
        maybeProcessDeepLink()
    }

    // TODO Maybe decouple into deep link interface
    private fun maybeProcessDeepLink() {
        val uri: Uri = intent.data ?: return
        if (uri.scheme != getString(R.string.deeplink_scheme)) return
        when (uri.host) {
            getString(R.string.deeplink_host_agent_profile) -> AgentProfileActivity.launch(this)
            else -> {
                // Do nothing
            }
        }
    }

    private fun setupViews() {
        setupViewPager()
        setupBottomBar()
    }

    private fun setupViewPager() {
        val sectionsPagerAdapter = MainPagerAdapter(supportFragmentManager)
        binding.mainViewPager.offscreenPageLimit = 4
        binding.mainViewPager.adapter = sectionsPagerAdapter
        binding.mainViewPager.addOnPageSelectedListener { position ->
            // tab tracking
            val tab = MainBottomNavigationTab.values()[position]
            NavigationBarTabTracker.trackNavigationBarTabClicked(this, tab)
            when (position) {
                MainBottomNavigationTab.FIND_AGENT.position -> AuthUtil.checkModuleAccessibility(
                    module = AdvisorModule.AGENT_DIRECTORY
                )
                else -> {
                    //do nothing for now
                }
            }
        }
    }

    private fun setupBottomBar() {
        binding.bottomAppBar.setupWithViewPager(binding.mainViewPager)
        binding.bottomAppBar.onFloatingActionButtonClick = { action ->
            // track bottom app bar options
            NavigationBarTabTracker.trackBottomAppBarOptionClicked(this, action)
            when (action) {
                BottomAppBar.Action.REPORT_AND_RESEARCH -> {
                    AuthUtil.checkModuleAccessibility(
                        module = AdvisorModule.HOME_REPORT,
                        onSuccessAccessibility = { showReportAndResearchActivity() })
                }
                BottomAppBar.Action.ADD_LISTING -> {
                    showCreateListingActivity()
                }
                BottomAppBar.Action.SEARCH -> {
                    AuthUtil.checkModuleAccessibility(
                        module = AdvisorModule.SEARCH_PANEL,
                        onSuccessAccessibility = { showSearchListingActivity() })
                }
            }
        }
    }

    private fun showReportAndResearchActivity() {
        ReportsAndResearchActivity.launch(this)
    }

    private fun performRequests() {
        viewModel.getAgentDetails()
    }

    private fun listenRxBuses() {
        listenRxBus(LoginAsAgentEvent::class.java) {
            //Note: to clear all previous user storage
            deleteAllTables()
            viewModel.checkHasConversationFromLocal()
            //call api for dashboard
            performRequests()
            // Chun Hoe: Update accessibility config
            getLoggedInUserConfig()
        }

        listenRxBus(LaunchSearchEvent::class.java) {
            showSearchListingActivity()
        }

        listenRxBus(ChatEditEvent::class.java) { chatEditEvent ->
            viewModel.shouldShowBottomBar.postValue(!chatEditEvent.editMode)
        }

        listenRxBus(ViewMyListingsEvent::class.java) { viewMyListingsEvent ->
            MyListingsActivity.launch(
                this,
                viewMyListingsEvent.ownershipType,
                viewMyListingsEvent.propertyMainType
            )
        }

        listenRxBus(LaunchCreateListingEvent::class.java) {
            if (!it.isFromMyListing) {
                showCreateListingActivity()
            }
        }

        listenRxBus(ShowFeaturedListingPromptEvent::class.java) { event ->
            FeaturedPromptFragment.showHide(binding.fragmentFeaturedPrompt, event.isShow)
        }

        listenRxBus(SubscriberPromptDismissEvent::class.java) { event ->
            if (event.launchDashboard == true) {
                showDashboard()
            } else if (event.showAutoImportPopup == true) {
                getLoggedInUserConfig()
            }
        }

        listenRxBus(NotifyRetrieveConfigEvent::class.java) {
            performAutoImportListings()
        }

        listenRxBus(SwipeMainPageEvent::class.java) {
            binding.mainViewPager.currentItem = it.page
        }
    }

    private fun observeLiveData() {
        //note: after deleting tables are successful
        viewModel.getHasConversationStatus.observeNotNull(this) {
            // NOTE: This is inherited from the original code where the event is published when
            // (the original) conversation list is empty
            if (it.key == ApiStatus.StatusKey.SUCCESS && it.body == null) {
                RxBus.publish(ChatRefreshEvent.REFRESH_TYPES_FROM_SERVER)
            }
        }

        viewModel.mainResponse.observeNotNull(this) {
            val agentPO = it.data

            //Store agent mobile number
            viewModel.agentMobileNumber.value = agentPO.mobile.toIntOrNull()

            if (agentPO.subscriptionType.isEmpty() && agentPO.subscription.isEmpty()) {
                showSubscriberPrompt(allowDismiss = false)
                viewModel.getUserProfile()
            } else {
                //OTHER SUBSCRIPTION TYPES
                performGetAgentProfileAndConfig()
            }
        }

        viewModel.getUserProfileStatus.observeNotNull(this) { response ->
            saveCurrentUser(response.profile)
            viewModel.checkVersion()
        }

        viewModel.checkVersionStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    if (it.body?.upgrade?.mandatoryUpgrade == true) {
                        //by force update
                        forceUpdateVersionDialog = dialogUtil.showInformationDialog(
                            R.string.msg_force_update_new_version
                        ) {
                            IntentUtil.visitPlayStoreApp(this)
                        }
                    } else {
                        if (it.body?.upgrade?.upgradeAvailable == true) {
                            dialogUtil.showActionDialog(
                                getString(
                                    R.string.msg_new_version_available,
                                    it.body.upgrade.availableUpgradeVersion
                                )
                            ) {
                                IntentUtil.visitPlayStoreApp(this)
                            }
                        }
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                    viewModel.getPortalAPIs()
                }
                else -> {
                    println("Error in check version status")
                }
            }
        }

        viewModel.getPortalAPIResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.getListingAutoResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val response = it.body ?: return@observeNotNull
                    if (response.total > 0) {
                        AutoImportPortalListingsDialogFragment.launch(
                            fragmentManager = supportFragmentManager,
                            total = response.total,
                            listings = response.listings,
                            portalAccount = response.portalAccount
                        )
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun performGetAgentProfileAndConfig() {
        getLoggedInUserConfig()
        viewModel.getUserProfile()
    }

    private fun performAutoImportListings() {
        AuthUtil.checkModuleAccessibility(
            module = AdvisorModule.PG_AUTO_IMPORT,
            onSuccessAccessibility = {
                val autoImportDialogFlag = SessionUtil.getAutoImportListingsDialogShowFlag()
                if (autoImportDialogFlag) {
                    viewModel.getPortalAPIs()
                }
            },
            onFailAccessibility = {
                //do nothing
                println("do nothing")
            }
        )

    }

    private fun saveCurrentUser(userProfile: UserPO) {
        SessionUtil.setCurrentUser(userProfile)
        RxBus.publish(UpdateUserProfileEvent())
    }

    private fun setupEnquiryIdParamFromExtras() {
        when {
            intent.hasExtra(EXTRA_KEY_ENQUIRY_ID) -> {
                intent.getStringExtra(EXTRA_KEY_ENQUIRY_ID)?.let {
                    if (NumberUtil.isNaturalNumber(it)) {
                        binding.mainViewPager.currentItem = MainBottomNavigationTab.CHAT.position
                    }
                }
            }
        }
    }

    private fun showDashboard() {
        binding.mainViewPager.currentItem = MainBottomNavigationTab.DASHBOARD.position
    }

    private fun showSearchListingActivity() {
        //Note : from dashboard source
        val extras = Bundle()
        extras.putSerializable(
            SearchActivity.EXTRA_KEY_EXPAND_MODE,
            SearchActivity.ExpandMode.FULL
        )
        launchActivity(SearchActivity::class.java, extras)
    }

    private fun showCreateListingActivity() {
        ListingAddressSearchDialogFragment.launch(
            fragmentManager = supportFragmentManager,
            addressSearchSource = ListingManagementEnum.AddressSearchSource.MAIN_SCREEN,
            createListingTracker = CreateListingTrackerPO(DateTimeUtil.getCurrentTimeInMillis())
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MenuFragment.REQUEST_CODE_AGENT_PROFILE -> {
                when (resultCode) {
                    AgentProfileActivity.RESULT_PROFILE_PHOTO_UPDATED -> {
                        viewModel.getAgentDetails()
                    }
                    else -> super.onActivityResult(requestCode, resultCode, data)
                }
            }
            AutoImportCertifiedListingsDialogFragment.REQUEST_CODE_PHONE_BOOK -> {
                supportFragmentManager
                    .findFragmentByTag(AutoImportCertifiedListingsDialogFragment.TAG)
                    ?.onActivityResult(requestCode, resultCode, data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        when {
            binding.fragmentFeaturedPrompt.visibility == View.VISIBLE -> {
                FeaturedPromptFragment.showHide(binding.fragmentFeaturedPrompt, false)
            }
            binding.bottomAppBar.isFloatActionActive() -> binding.bottomAppBar.showSubFloatingActionButtons(
                false
            )
            binding.mainViewPager.currentItem != 0 -> binding.mainViewPager.currentItem = 0
            else -> super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        //Force to update version -> so showing dialog again on Resume state
        forceUpdateVersionDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}