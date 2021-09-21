package sg.searchhouse.agentconnect.test

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import sg.searchhouse.agentconnect.test.factory.WriteEditTextFactory

class TestUser {
    infix fun click(@StringRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
    }

    infix fun write(text: String): WriteEditTextFactory {
        return WriteEditTextFactory(text)
    }

    infix fun click(viewMatcher: Matcher<View>) {
        Espresso.onView(viewMatcher).perform(ViewActions.click())
    }

    infix fun <T: Activity> launch(activity: Class<T>): ActivityScenario<T> {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            activity
        )
        return ActivityScenario.launch(intent)
    }

    infix fun <T: Activity> launch(intent: Intent): ActivityScenario<T> {
        return ActivityScenario.launch(intent)
    }
}