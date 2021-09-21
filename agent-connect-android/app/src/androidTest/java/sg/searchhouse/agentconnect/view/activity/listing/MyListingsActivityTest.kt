package sg.searchhouse.agentconnect.view.activity.listing

import org.junit.Test
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher

class MyListingsActivityTest: BaseApiActivityTest() {
    @Test
    fun testPlaceholder() {
        user launch MyListingsActivity::class.java
        // Do nothing
    }

    override fun getDispatcher() = dispatcher {
        // TODO
        null
    }
}
