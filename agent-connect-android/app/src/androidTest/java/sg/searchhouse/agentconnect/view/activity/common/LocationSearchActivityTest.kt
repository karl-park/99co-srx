package sg.searchhouse.agentconnect.view.activity.common

import org.junit.Test
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher

class LocationSearchActivityTest : BaseApiActivityTest() {
    @Test
    fun testPlaceholder() {
        user launch LocationSearchActivity::class.java
        // TODO
    }

    override fun getDispatcher() = dispatcher {
        // TODO
        null
    }
}