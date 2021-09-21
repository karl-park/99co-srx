package sg.searchhouse.agentconnect.view.activity.listing

import kotlinx.android.synthetic.main.layout_listing_details_basic.view.*
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import sg.searchhouse.agentconnect.dsl.createIntent
import sg.searchhouse.agentconnect.dsl.readResourcesFile
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher
import sg.searchhouse.agentconnect.test.dsl.expectDisplayText
import sg.searchhouse.agentconnect.test.dsl.isGet

class ListingDetailsActivityTest : BaseApiActivityTest() {
    @Test
    fun whenLaunchActivity_thenShowContent() {
        user.launch<ListingDetailsActivity>(context.createIntent<ListingDetailsActivity> {
            putExtra(ListingDetailsActivity.EXTRA_KEY_LISTING_ID, "78553092")
            putExtra(ListingDetailsActivity.EXTRA_KEY_LISTING_TYPE, "A")
            putExtra(ListingDetailsActivity.EXTRA_KEY_LAUNCH_MODE, ListingDetailsActivity.LaunchMode.VIEW)
        }).run {
            Thread.sleep(3000)
            "main_title" expectDisplayText "TwentyOne Angulae Park (D9), Condominium"
        }
    }

    override fun getDispatcher() = dispatcher {
        if (this isGet "/api/v1/listings/search?action=getListing&listingId=78553092&listingType=A") {
            val response =
                this@ListingDetailsActivityTest.javaClass.readResourcesFile("response_listing_details.json")
            MockResponse().setResponseCode(200).setBody(response)
        } else {
            null
        }
    }
}