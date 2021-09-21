package sg.searchhouse.agentconnect.view.activity.listing

import org.junit.Test
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher

class SearchActivityTest : BaseApiActivityTest() {
    @Test
    fun testPlaceholder() {
        user launch SearchActivity::class.java
        // TODO
    }

    override fun getDispatcher() = dispatcher {
        // TODO
        null
    }
}
