package sg.searchhouse.agentconnect.test.base

import android.content.SharedPreferences
import android.view.View
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import sg.searchhouse.agentconnect.AgentConnectApplication
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.helper.Json
import sg.searchhouse.agentconnect.test.TestUser

@RunWith(AndroidJUnit4ClassRunner::class)
abstract class BaseActivityTest {
    val user = TestUser()
    val context: AgentConnectApplication = ApplicationProvider.getApplicationContext()
    val backButton: Matcher<View> =
        ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description)
    private val preferenceEditor: SharedPreferences.Editor =
        PreferenceManager.getDefaultSharedPreferences(context).edit()

    @Before
    fun setup() {
        Intents.init()
        setupCustom()
    }

    @After
    fun tearDown() {
        tearDownCustom()
        Intents.release()
    }

    open fun setupCustom() {}

    open fun tearDownCustom() {}

    infix fun String.setPreferenceValue(value: String) {
        preferenceEditor.putString(this, value).commit()
    }

    infix fun String.setPreferenceValue(value: Json) {
        preferenceEditor.putString(this, value.toString()).commit()
    }
}