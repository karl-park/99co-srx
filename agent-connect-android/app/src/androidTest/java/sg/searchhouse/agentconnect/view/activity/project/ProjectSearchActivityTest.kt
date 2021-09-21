package sg.searchhouse.agentconnect.view.activity.project

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import sg.searchhouse.agentconnect.constant.AppConstant

@RunWith(AndroidJUnit4ClassRunner::class)
class ProjectSearchActivityTest {
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        Intents.init()
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start(AppConstant.TEST_SERVER_PORT)
    }

    @After
    fun tearDown() {
        Intents.release()
        mockWebServer.shutdown()
    }

    private fun launch(): ActivityScenario<ProjectSearchActivity> {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ProjectSearchActivity::class.java
        )
        return ActivityScenario.launch(intent)
    }

    @Test
    fun testPlaceholder() {
        launch()
        // Do nothing
    }

    private val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
//                Success response
//                val response =
//                    this@ActivityTestTemplate.javaClass.readResourcesFile("success_response.json")
//                return MockResponse().setResponseCode(200).setBody(response)
            return MockResponse().setResponseCode(404)
        }
    }
}
