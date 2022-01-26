package com.andreadematteis.assignments.beerbox

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.andreadematteis.assignments.beerbox.BeerBoxTestsAssertion.RecyclerViewItemCountAssertion
import com.andreadematteis.assignments.beerbox.BeerBoxTestsMatchers.atPositionOnView
import com.andreadematteis.assignments.beerbox.BeerBoxTestsMatchers.withDrawable
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

    class FakeBeerRepository @Inject constructor(
        service: BeerService
    ) : BeerRepository(service) {
        override suspend fun getBeers(page: Int): List<Beer> =
            when {
                page == BEERS_PAGES + 1 -> {
                    emptyList()
                }
                page > 1 -> {
                    mutableListOf<Beer>().apply {
                        repeat(20) {
                            add(getBeerPlaceHolder(it + MANDATORY_BEERS.size))
                        }
                    }
                }
                else -> {
                    MANDATORY_BEERS + mutableListOf<Beer>().apply {
                        repeat(20 - MANDATORY_BEERS.size) {
                            add(getBeerPlaceHolder(it + MANDATORY_BEERS.size))
                        }
                    }
                }
            }
    }

    class FakeImageRepository @Inject constructor(service: ImageService) :
        ImageRepository(service) {
        override suspend fun getImage(imageStringUrl: String): ResponseBody =
            when (imageStringUrl) {
                NO_IMAGE_URL_STRING -> error("Expected Error")
                else -> ResponseBody.create(MediaType.get("image/bmp"), "")
            }
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

        onView(withId(R.id.search_text_input_layout)).check(matches(isNotEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(isNotEnabled()))

        onView(withId(R.id.filter_label)).check(matches(isNotEnabled()))

        onView(withId(R.id.filter_by_name_label)).perform(click())
        onView(withId(R.id.by_brewing_time_label)).check(matches(isNotChecked()))

        onView(withId(R.id.search_text)).perform(typeText("Lager"))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.filter_label)).perform(click())
        onView(withId(R.id.search_text)).check(matches(withText("Lager")))
        onView(withId(R.id.more_filters)).check(matches(withText("All filters (1)")))

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
    fun filterByTimeframeNoBeers() {
        onView(withId(R.id.more_filters))
            .perform(click())

        onView(withId(R.id.search_text_input_layout)).check(matches(isNotEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(isNotEnabled()))

        onView(withId(R.id.by_brewing_time_label)).perform(click())
        onView(withId(R.id.filter_by_name_label)).check(matches(isNotChecked()))

        onView(withId(R.id.brewing_time_text))
            .perform(typeText("25/01/2022 - 26/01/2022"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.filter_label)).perform(click())

        onView(withId(R.id.more_filters)).check(matches(withText("All filters (1)")))
        onView(withId(R.id.nothing_matches_text)).check(matches(isDisplayed()))
    }

    @Test
    fun testRotateScreenKeepsBeers() {
        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(20)
        )

        val device = UiDevice.getInstance(getInstrumentation())

        device.setOrientationLeft()

        Thread.sleep(2000)
        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(20)
        )

        device.setOrientationRight()

        Thread.sleep(2000)
        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(20)
        )
    }

    @Test
    fun testRotateScreenKeepsFilters() {
        onView(withId(R.id.more_filters)).perform(click())
        onView(withId(R.id.filter_by_name_label)).perform(click())
        onView(withId(R.id.search_text))
            .perform(typeText("Lager"))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.filter_label))
            .perform(click())

        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(1)
        )

        val device = UiDevice.getInstance(getInstrumentation())

        device.setOrientationLeft()

        Thread.sleep(1500)

        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(1)
        )

        onView(withId(R.id.more_filters)).perform(click())
        onView(withId(R.id.filter_by_name_label)).check(matches(isChecked()))

        onView(withId(R.id.title)).perform(
            swipeUp()
        )

        Thread.sleep(500)


        onView(withId(R.id.search_text)).check(matches(withText("Lager")))
        onView(withId(R.id.cancel_label))
            .perform(click())

        device.setOrientationRight()

        Thread.sleep(1500)
        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(1)
        )

        onView(withId(R.id.more_filters)).perform(click())

        onView(withId(R.id.title)).perform(
            swipeUp()
        )
        Thread.sleep(500)


        onView(withId(R.id.filter_by_name_label)).check(matches(isChecked()))
        onView(withId(R.id.search_text)).check(matches(withText("Lager")))

    }

    @Test
    fun filterByTimeframeAndReset() {
        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(20)
        )

        onView(withId(R.id.nothing_matches_text)).check(matches(not(isDisplayed())))

        onView(withId(R.id.more_filters))
            .perform(click())

        onView(withId(R.id.search_text_input_layout)).check(matches(isNotEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(isNotEnabled()))

        onView(withId(R.id.by_brewing_time_label)).perform(click())
        onView(withId(R.id.filter_by_name_label)).check(matches(isNotChecked()))

        onView(withId(R.id.brewing_time_text))
            .perform(typeText("25/01/2022 - 26/01/2022"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.filter_label)).perform(click())

        onView(withId(R.id.more_filters)).check(matches(withText("All filters (1)")))
        onView(withId(R.id.nothing_matches_text)).check(matches(isDisplayed()))

        onView(withId(R.id.more_filters))
            .perform(click())

        onView(withId(R.id.search_text_input_layout)).check(matches(isNotEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(isEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(withText("25/01/2022 - 26/01/2022")))

        onView(withId(R.id.reset_label))
            .perform(click())

        onView(withId(R.id.more_filters)).check(matches(withText("All filters")))

        onView(withId(R.id.recycler_view_beer)).check(
            RecyclerViewItemCountAssertion(20)
        )

        onView(withId(R.id.nothing_matches_text)).check(matches(not(isDisplayed())))

        onView(withId(R.id.more_filters))
            .perform(click())

        onView(withId(R.id.search_text_input_layout)).check(matches(isNotEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(isNotEnabled()))
        onView(withId(R.id.brewing_time_text)).check(matches(withText("")))

    }

    @Test
    fun filterByTimeframeResults() {
        onView(withId(R.id.more_filters))
            .perform(click())

        onView(withId(R.id.filter_by_name_label)).check(matches(isDisplayed()))
        onView(withId(R.id.filter_label)).check(matches(isNotEnabled()))

        onView(withId(R.id.by_brewing_time_label)).perform(click())
        onView(withId(R.id.filter_by_name_label)).check(matches(isNotChecked()))

        onView(withId(R.id.brewing_time_text))
            .perform(typeText("25/01/2018 - 26/11/2019"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.filter_label)).perform(click())

        onView(withId(R.id.more_filters)).check(matches(withText("All filters (1)")))
        onView(withId(R.id.recycler_view_beer)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
            .perform(
                swipeUp()
            )


        onView(withId(R.id.brewing_date_value)).check(matches(not(withText("25/01/2018"))))

        onView(withId(android.R.id.content))
            .perform(
                swipeDown()
            )

        onView(withId(R.id.recycler_view_beer)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )

        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
            .perform(
                swipeUp()
            )

        onView(withId(R.id.brewing_date_value)).check(matches(not(withText("26/11/2019"))))
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
        onView(withId(R.id.more_filters)).check(matches(withText("All filters (1)")))

        onView(withId(R.id.more_filters)).perform(click())
        onView(withId(R.id.search_text)).check(matches(withText("Lager")))
        onView(withId(R.id.filter_by_name_label)).check(matches(isChecked()))
        onView(withId(R.id.by_brewing_time_label)).check(matches(isNotChecked()))
    }

    @Test
    fun isImageNotVisibleInBottomSheet() {
        onView(withId(R.id.search_text)).perform(typeText("Without Image"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                allOf(
                    hasDescendant(
                        withText(
                            containsStringIgnoringCase("Without Image")
                        )
                    )
                )
            )
        )

        onView(withId(R.id.nothing_matches_text)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                atPositionOnView(
                    0,
                    isDisplayed(),
                    R.id.reload_button
                )
            )
        )
    }

    @Test
    fun isImageVisibleInBottomSheet() {
        onView(withId(R.id.search_text)).perform(typeText("with Image"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                allOf(
                    hasDescendant(
                        withText(
                            containsStringIgnoringCase("with Image")
                        )
                    )
                )
            )
        )

        onView(withId(R.id.nothing_matches_text)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recycler_view_beer)).check(
            matches(
                atPositionOnView(
                    0,
                    withDrawable(R.mipmap.ic_missing_image),
                    R.id.beer_image
                )
            )
        )

        onView(withId(R.id.recycler_view_beer)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.image)).check(matches(withDrawable(R.mipmap.ic_missing_image)))
    }

    @Test
    fun clickOnBeerItemAndPopulatedBrewingDate() {
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.recycler_view_beer)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
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
    fun clickOnMoreInfoOpensBottomSheet() {
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.recycler_view_beer)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                Random.nextInt(10),
                BeerBoxTestsActions.clickChildViewWithId(R.id.more_info_button)
            )
        )

        onView(withId(R.id.title)).check(matches(not(withText(""))))
    }

    @Test
    fun scrollRecyclerView() {
        onView(isRoot()).perform(closeSoftKeyboard())

        repeat(BEERS_PAGES - 1) {
            onView(withId(R.id.recycler_view_beer)).perform(
                scrollToPosition<RecyclerView.ViewHolder>(
                    ((it + 1) * 20) - 1
                )
            )

            Thread.sleep(100) // let new items be loaded

            onView(withId(R.id.recycler_view_beer)).check(
                RecyclerViewItemCountAssertion(
                    (it + 2) * 20
                )
            )
        }

        onView(withId(R.id.recycler_view_beer)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(
                (BEERS_PAGES - 1 * 20) - 1
            )
        )

        onView(withId(R.id.recycler_view_beer)).check(RecyclerViewItemCountAssertion(BEERS_PAGES * 20))

    }

    @Test
    fun filterNameNotExistingFromField() {
        onView(withId(R.id.search_text)).perform(typeText("ThisDoesNotExist"))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.nothing_matches_text)).check(matches(isDisplayed()))
    }

    @Test
    fun filterNameFromToggle() {
        Thread.sleep(500)

        onView(withText("Lager")).perform(click())
        Thread.sleep(500)

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

    companion object {
        private const val BEERS_PAGES = 10
        private const val NO_IMAGE_URL_STRING = "noTestImage"
        private const val TEST_IMAGE_URL_STRING = "testImage"

        private fun getBeerPlaceHolder(position: Int) = Beer(
            "Your favourite beer description",
            "01/1970",
            position,
            "",
            "My Favourite beer",
            "Your favourite beer"
        )

        private val MANDATORY_BEERS = listOf(
            Beer(
                "Your favourite Blonde without image beer description",
                "01/1970",
                3,
                NO_IMAGE_URL_STRING,
                "My Favourite Blonde beer without image",
                "Your favourite Blonde beer without image"
            ),
            Beer(
                "Your favourite Blonde with image beer description",
                "01/1970",
                2,
                TEST_IMAGE_URL_STRING,
                "My Favourite Blonde beer with image",
                "Your favourite Blonde beer with image"
            ),
            Beer(
                "Your favourite Blonde without image beer description",
                "01/1970",
                3,
                NO_IMAGE_URL_STRING,
                "My Favourite Blonde beer without image",
                "Your favourite Blonde beer without image"
            ),
            Beer(
                "Your favourite Blonde of 2018 with beer description",
                "05/2018",
                4,
                "",
                "My Favourite Blonde of 2018 beer with image",
                "Your favourite Blonde of 2018 beer with image"
            ),
            Beer(
                "Your favourite Lager of 2019 beer description",
                "05/2019",
                5,
                "",
                "My Favourite Lager of 2019 beer",
                "Your favourite Lager of 2019 beer"
            ),
            Beer(
                "Your favourite Blonde beer description",
                "01/1970",
                1,
                "",
                "My Favourite Blonde beer",
                "Your favourite Blonde beer"
            )
        )
    }
}