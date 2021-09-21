package sg.searchhouse.agentconnect.view.activity.common

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import org.junit.Test
import sg.searchhouse.agentconnect.dsl.createIntent
import sg.searchhouse.agentconnect.test.base.BaseActivityTest
import sg.searchhouse.agentconnect.test.dsl.expectResultCode

class CropImageActivityTest: BaseActivityTest() {
    private fun launch(): ActivityScenario<CropImageActivity> {
        val intent = context.createIntent<CropImageActivity> {
            putExtra(CropImageActivity.EXTRA_IMAGE_URL, "https://www.imagesource.com/wp-content/uploads/2019/06/Rio.jpg")
        }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun whenClickBackButton_thenFinish() {
        val scenario = launch()
        user click backButton
        scenario expectResultCode Activity.RESULT_CANCELED
    }
}
