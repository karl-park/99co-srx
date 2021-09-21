package sg.searchhouse.agentconnect.view.activity.calculator

import org.junit.Test
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher

class AffordabilityCalculatorActivityTest : BaseApiActivityTest() {
    @Test
    fun testPlaceholder() {
        user launch AffordabilityCalculatorActivity::class.java
        // TODO
    }

    override fun getDispatcher() = dispatcher {
        // TODO
        null
    }
}