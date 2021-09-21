package sg.searchhouse.agentconnect.test.dsl

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.textfield.TextInputLayout
import junit.framework.TestCase
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher

@LayoutRes
infix fun Int.expect(viewMatcher: Matcher<View>) {
    Espresso.onView(withId(this)).check(ViewAssertions.matches(viewMatcher))
}

@LayoutRes
infix fun Int.expectDisplayText(text: String) {
    Espresso.onView(allOf(withId(this), isDisplayed()))
        .check(ViewAssertions.matches(withText(text)))
}

infix fun String.expectDisplayText(text: String) {
    Espresso.onView(allOf(withTagValue(`is`(this)), isDisplayed()))
        .check(ViewAssertions.matches(withText(text)))
}

@LayoutRes
infix fun Int.expectViewPagerPosition(position: Int) {
    Espresso.onView(withId(this)).check { view, noViewFoundException ->
        val viewPager = view as ViewPager? ?: throw noViewFoundException
        TestCase.assertEquals(position, viewPager.currentItem)
    }
}

infix fun Matcher<View>.expect(viewMatcher: Matcher<View>) {
    Espresso.onView(this).check(ViewAssertions.matches(viewMatcher))
}

@IdRes
infix fun Int.expectShowErrorText(expectedErrorText: String): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun matchesSafely(item: View?): Boolean {
            return item is TextInputLayout && expectedErrorText == item.error
        }

        override fun describeTo(description: Description?) {}
    }
}