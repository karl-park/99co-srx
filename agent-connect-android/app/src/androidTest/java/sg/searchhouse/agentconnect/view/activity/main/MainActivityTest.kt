package sg.searchhouse.agentconnect.view.activity.main

import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.junit.Test
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey
import sg.searchhouse.agentconnect.helper.Json
import sg.searchhouse.agentconnect.test.base.BaseActivityTest
import sg.searchhouse.agentconnect.test.dsl.expect
import sg.searchhouse.agentconnect.test.dsl.expectLaunch
import sg.searchhouse.agentconnect.test.dsl.expectViewPagerPosition
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.activity.report.ReportsAndResearchActivity

class MainActivityTest : BaseActivityTest() {
    override fun setupCustom() {
        // Setup case where user has unlimited access to all pages
        SharedPreferenceKey.PREF_USER_SUBSCRIPTION_CONFIG setPreferenceValue SUBSCRIPTION_CONFIG_SUPER_USER
    }

    @Test
    fun whenClickSrxMainButton_thenShowFloatingButtons() {
        user launch MainActivity::class.java
        user click R.id.fab_main
        R.id.fab_home_report expect isDisplayed()
        R.id.fab_add_listing expect isDisplayed()
        R.id.fab_search expect isDisplayed()
        R.id.layout_fab_group_overlay expect isDisplayed()
    }

    @Test
    fun whenClickChatBottomButton_thenVisitChatFragment() {
        user launch MainActivity::class.java
        user click R.id.ib_menu_chat
        R.id.main_view_pager expectViewPagerPosition 1
    }

    @Test
    fun whenClickAgentsBottomButton_thenVisitAgentsFragment() {
        user launch MainActivity::class.java
        user click R.id.ib_menu_agent_cv
        R.id.main_view_pager expectViewPagerPosition 2
    }

    @Test
    fun whenClickMenuBottomButton_thenVisitMenuFragment() {
        user launch MainActivity::class.java
        user click R.id.ib_menu_more
        R.id.main_view_pager expectViewPagerPosition 3
    }

    @Test
    fun whenClickHomeReportFloatingButton_thenVisitReportsPage() {
        (user launch MainActivity::class.java).run {
            user click R.id.fab_main
            user click R.id.fab_home_report
            this expectLaunch ReportsAndResearchActivity::class.java
        }
    }

    @Test
    fun whenClickSearchFloatingButton_thenVisitSearchPage() {
        (user launch MainActivity::class.java).run {
            user click R.id.fab_main
            user click R.id.fab_search
            this expectLaunch SearchActivity::class.java
        }
    }

    @Test
    fun whenClickAddFloatingButton_thenVisitAddListingPage() {
        user launch MainActivity::class.java
        user click R.id.fab_main
        user click R.id.fab_add_listing
        R.id.layout_listing_address_search expect isDisplayed()
    }

    companion object {
        private val SUBSCRIPTION_CONFIG_SUPER_USER = Json {
            "config" to Json {
                "features" to Json {
                    "LISTINGS_SEARCH" to "YES"
                    "TRANSACTION_SEARCH" to "YES"
                    "AGENT_PROFILE" to "YES"
                    "TRANSACTION_SEARCH_EXPORT" to "YES"
                    "HOME_REPORT" to "YES"
                    "CHAT" to "YES"
                    "LISTING_MANAGEMENT" to "YES"
                    "PROJECT_INFO" to "YES"
                    "WATCHLISTS" to "YES"
                    "PROJECT_INFO_COMMERCIAL" to "YES"
                    "SEARCH_PANEL" to "YES"
                    "TRANSACTION_SEARCH_COMMERCIAL" to "YES"
                    "FLASH_REPORT_HISTORY" to "YES"
                    "XVALUE_REPORT" to "YES"
                    "DASHBOARD" to "YES"
                    "PG_AUTO_IMPORT" to "YES"
                    "NEWS" to "YES"
                    "PG_IMPORT" to "YES"
                    "CEA_EXCLUSIVES" to "YES"
                    "AGENT_CV" to "YES"
                    "XVALUE" to "YES"
                    "NEW_PROJECT_REPORTS" to "NO"
                    "MY_CLIENTS" to "YES"
                    "AGENT_DIRECTORY" to "YES"
                }
                "subscriptionType" to "HRD"
            }
        }
    }
}
