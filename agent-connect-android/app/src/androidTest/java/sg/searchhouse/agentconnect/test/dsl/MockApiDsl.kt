package sg.searchhouse.agentconnect.test.dsl

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import sg.searchhouse.agentconnect.constant.AppConstant
import java.net.BindException

fun dispatcher(setup: RecordedRequest.() -> MockResponse?): Dispatcher {
    return object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return setup.invoke(request) ?: MockResponse().setResponseCode(404)
        }
    }
}

infix fun RecordedRequest.isGet(endpoint: String): Boolean {
    return method == "GET" && path == endpoint
}

infix fun RecordedRequest.isPost(endpoint: String): Boolean {
    return method == "POST" && path == endpoint
}

fun initMockWebServer(setup: MockWebServer.() -> Unit): MockWebServer {
    val mockWebServer = MockWebServer()
    setup.invoke(mockWebServer)
    try {
        mockWebServer.start(AppConstant.TEST_SERVER_PORT)
    } catch (e: BindException) {
        // Do nothing
    }
    return mockWebServer
}

fun RecordedRequest.bodyString(): String {
    return body.readUtf8()
}