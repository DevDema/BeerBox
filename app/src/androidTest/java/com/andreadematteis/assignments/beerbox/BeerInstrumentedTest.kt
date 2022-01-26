package com.andreadematteis.assignments.beerbox

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreadematteis.assignments.beerbox.model.Beer
import com.andreadematteis.assignments.beerbox.network.*
import com.andreadematteis.assignments.beerbox.network.repositories.BeerRepository
import com.andreadematteis.assignments.beerbox.network.repositories.ImageRepository
import com.andreadematteis.assignments.beerbox.view.BeerActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

import kotlin.random.Random
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import org.hamcrest.CoreMatchers

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class BeerInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var mActivityScenario: ActivityScenario<BeerActivity>

    class FakeBeerRepository @Inject constructor(service: BeerService) : BeerRepository(service) {
        override suspend fun getBeers(page: Int): List<Beer> =
            if (page > 1) {
                mutableListOf<Beer>().apply {
                    repeat((BEERS_PAGES * 20) - MANDATORY_BEERS.size) {
                        add(
                            Beer(
                                "Your favourite beer description",
                                "01/01/1970",
                                0,
                                "",
                                "My Favourite beer",
                                "Your favourite beer"
                            )
                        )
                    }
                }
            } else {
                MANDATORY_BEERS
            }
    }

    class FakeImageRepository @Inject constructor(service: ImageService) :
        ImageRepository(service) {
        override suspend fun getImage(imageStringUrl: String): ResponseBody =
            ResponseBody.create(MediaType.get(""), "")
    }


    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {

        @Singleton
        @Binds
        abstract fun provideBeerRepository(beerRepository: FakeBeerRepository): BeerRepository

        @Singleton
        @Binds
        abstract fun provideImageRepository(imageRepository: FakeImageRepository): ImageRepository
    }

    @Before
    fun setup() {
        mActivityScenario = ActivityScenario.launch(BeerActivity::class.java)
        hiltRule.inject()

    }

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

        repeat(6) {
            onView(withId(R.id.recycler_view_beer)).perform(
                scrollToPosition<RecyclerView.ViewHolder>(
                    it * 20 - 1
                )
            )

            onView(withId(R.id.recycler_view_beer)).check(
                matches(
                    not(
                        RecyclerViewItemCountAssertion(
                            120
                        )
                    )
                )
            )

        }

        onView(withId(R.id.recycler_view_beer)).check(RecyclerViewItemCountAssertion(120))

    }

    @Test
    fun filterNameNotExistingFromField() {
        onView(withId(R.id.search_text)).perform(typeText("ThisDoesNotExist"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.nothing_matches_text)).check(matches(isDisplayed()))
    }

    @Test
    fun filterNameFromToggle() {
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

    private class RecyclerViewItemCountAssertion(private val count: Int) : ViewAssertion {

        override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            if (view !is RecyclerView) {
                throw IllegalStateException("The asserted view is not RecyclerView")
            }

            if (view.adapter == null) {
                throw IllegalStateException("No adapter is assigned to RecyclerView")
            }

            assertThat(
                "RecyclerView item count",
                view.adapter!!.itemCount,
                CoreMatchers.equalTo(count)
            )
        }
    }

    companion object {
        private const val BEERS_PAGES = 6
        private val MANDATORY_BEERS = listOf(
            Beer(
                "Your favourite Lager beer description",
                "01/01/1970",
                0,
                "",
                "My Favourite Lager beer",
                "Your favourite Lager beer"
            ),
            Beer(
                "Your favourite Blonde beer description",
                "01/01/1970",
                0,
                "",
                "My Favourite Blonde beer",
                "Your favourite Blonde beer"
            )
        )
    }
}