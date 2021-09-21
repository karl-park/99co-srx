package sg.searchhouse.agentconnect.test.factory

import androidx.annotation.LayoutRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers

class WriteEditTextFactory(private val text: String) {
    infix fun on(@LayoutRes editText: Int) {
        Espresso.onView(ViewMatchers.withId(editText)).perform(ViewActions.typeText(text))
    }
}