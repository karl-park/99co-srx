package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.readResourcesFile
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher
import sg.searchhouse.agentconnect.test.dsl.expectLaunch
import sg.searchhouse.agentconnect.test.dsl.isGet
import sg.searchhouse.agentconnect.view.activity.listing.MyListingsActivity
import sg.searchhouse.agentconnect.view.activity.main.MainActivity

class DashboardUserListingsFragmentTest: BaseApiActivityTest() {
    @Test
    fun whenClickViewAllSaleButton_thenVisitMySaleListings() {
        (user launch MainActivity::class.java).run {
            user click R.id.btn_view_all_active_sale_listings
            this expectLaunch MyListingsActivity::class.java
        }
    }

    override fun getDispatcher(): Dispatcher = dispatcher {
        if (this isGet "/api/v1/listings/management?action=getListingsSummary") {
            val response =
                this@DashboardUserListingsFragmentTest.javaClass.readResourcesFile("response_get_listings_summary.json")
            MockResponse().setResponseCode(200).setBody(response)
        } else {
            null
        }
    }
}