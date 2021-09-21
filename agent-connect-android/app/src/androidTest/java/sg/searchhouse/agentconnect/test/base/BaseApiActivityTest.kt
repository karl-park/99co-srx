package sg.searchhouse.agentconnect.test.base

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockWebServer
import sg.searchhouse.agentconnect.test.dsl.initMockWebServer

abstract class BaseApiActivityTest : BaseActivityTest() {
    private lateinit var mockWebServer: MockWebServer

    override fun setupCustom() {
        super.setupCustom()
        mockWebServer = initMockWebServer { dispatcher = getDispatcher() }
    }

    override fun tearDownCustom() {
        super.tearDownCustom()
        mockWebServer.shutdown()
    }

    abstract fun getDispatcher(): Dispatcher
}