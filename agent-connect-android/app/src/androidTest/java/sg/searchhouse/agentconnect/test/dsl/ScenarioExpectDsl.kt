package sg.searchhouse.agentconnect.test.dsl

import android.app.Activity
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import junit.framework.TestCase
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.anyOf
import org.junit.Assert

infix fun <T : Activity> ActivityScenario<T>.expectResultCode(resultCode: Int) {
    TestCase.assertEquals(result.resultCode, resultCode)
}

infix fun <T : Activity> ActivityScenario<T>.expectActivityState(state: Lifecycle.State) {
    TestCase.assertEquals(state, this.state)
}

infix fun <T : Activity> ActivityScenario<T>.expectSeeToast(@StringRes text: Int) {
    this.onActivity {
        Espresso.onView(ViewMatchers.withSubstring(it.resources.getString(text)))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.`is`(it.window.decorView))))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}

infix fun <T : Activity> ActivityScenario<T>.expectSeeView(@IdRes viewResId: Int) {
    Espresso.onView(anyOf(ViewMatchers.withId(viewResId)))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

infix fun <T : Activity> ActivityScenario<*>.expectLaunch(activity: Class<T>) {
    Intents.intended(IntentMatchers.hasComponent(activity.name))
}

fun <T : Activity> ActivityScenario<T>.expectFinishActivity() {
    try {
        onActivity {
            Assert.assertTrue(it.isFinishing)
        }
    } catch (e: RuntimeException) {
        this expectActivityState Lifecycle.State.DESTROYED
    }
}