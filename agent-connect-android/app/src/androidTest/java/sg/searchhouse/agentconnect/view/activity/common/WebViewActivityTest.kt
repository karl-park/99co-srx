package sg.searchhouse.agentconnect.view.activity.common

import org.junit.Test
import sg.searchhouse.agentconnect.dsl.createIntent
import sg.searchhouse.agentconnect.test.base.BaseActivityTest

class WebViewActivityTest : BaseActivityTest() {
    @Test
    fun whenInputGoogleUrl_thenShowWebPage() {
        user.launch<WebViewActivity>(context.createIntent<WebViewActivity> {
            putExtra(WebViewActivity.EXTRA_URL, "https://www.google.com")
        })
        // TODO
    }
}