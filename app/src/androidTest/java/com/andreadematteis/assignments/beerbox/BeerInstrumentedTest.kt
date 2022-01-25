package com.andreadematteis.assignments.beerbox

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.andreadematteis.assignments.beerbox.view.BeerActivity
import org.hamcrest.Matchers.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import kotlin.random.Random


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BeerInstrumentedTest {

    @Rule
    @JvmField
    var mActivityRule: ActivityTestRule<BeerActivity> = ActivityTestRule(BeerActivity::class.java)

    @Test
    fun filterNameFromFragment() {
        onView(withId(R.id.more_filters))
            .perform(click())

        onView(withId(R.id.filter_by_name_label)).check(matches(isDisplayed()))
        onView(withId(R.id.filter_label)).check(matches(isNotEnabled()))

        onView(withId(R.id.filter_by_name_label)).perform(click())
        onView(withId(R.id.search_text)).perform(typeText("Lager"))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.filter_label)).perform(click())

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                allOf(
                    hasDescendant(
                        withText(
                            containsStringIgnoringCase("Lager")
                        )
                    )
                )
            )
        )
    }

    @Test
    fun filterNameFromField() {
        onView(withId(R.id.search_text)).perform(typeText("Lager"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                allOf(
                    hasDescendant(
                        withText(
                            containsStringIgnoringCase("Lager")
                        )
                    )
                )
            )
        )

        onView(withId(R.id.nothing_matches_text)).check(matches(not(isDisplayed())))

    }

    @Test
    fun clickOnBeerItemAndPopulatedBrewingDate() {
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.recycler_view_beer)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                Random.nextInt(10),
                click()
            )
        )

        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
            .perform(
                swipeUp()
            )

        onView(withId(R.id.brewing_date_value)).check(matches(not(withText(""))))
    }

    @Test
    fun scrollRecyclerView() {
        onView(isRoot()).perform(closeSoftKeyboard())


        repeat(5) {
            onView(withId(R.id.recycler_view_beer)).perform(
                scrollToPosition<RecyclerView.ViewHolder>(
                     - 1
                )
            )

            Thread.sleep(1500)
        }
    }

    @Test
    fun filterNameNotExistingFromField() {
        onView(withId(R.id.search_text)).perform(typeText("ThisDoesNotExist"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.nothing_matches_text)).check(matches(isDisplayed()))
    }

    @Test
    fun filterNameFromToggle() {
        // TODO: 25/01/22 Wait because you still have to wait for all items to be loaded (To be fixed!)
        Thread.sleep(1000)

        onView(withText("Lager")).perform(click())
        onView((isRoot())).perform(closeSoftKeyboard())

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                allOf(
                    hasDescendant(
                        withText(
                            containsStringIgnoringCase("Lager")
                        )
                    )
                )
            )
        )
    }
}