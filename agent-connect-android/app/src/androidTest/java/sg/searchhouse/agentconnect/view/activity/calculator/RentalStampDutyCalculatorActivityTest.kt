package sg.searchhouse.agentconnect.view.activity.calculator

import org.junit.Test
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.dispatcher

class RentalStampDutyCalculatorActivityTest : BaseApiActivityTest() {
    @Test
    fun testPlaceholder() {
        user launch RentalStampDutyCalculatorActivity::class.java
        // TODO
    }

    override fun getDispatcher() = dispatcher {
        // TODO
        null
    }
}