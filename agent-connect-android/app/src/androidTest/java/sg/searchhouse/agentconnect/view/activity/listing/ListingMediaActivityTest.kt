package sg.searchhouse.agentconnect.view.activity.listing

import org.junit.Test
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher

class ListingMediaActivityTest : BaseApiActivityTest() {
    @Test
    fun testPlaceholder() {
        user launch FilterListingActivity::class.java
        // Do nothing
    }

    override fun getDispatcher() = dispatcher {
        // TODO
        null
    }
}
