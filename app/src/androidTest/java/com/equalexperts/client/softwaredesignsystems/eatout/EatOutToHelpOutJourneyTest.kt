package com.equalexperts.client.softwaredesignsystems.eatout

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.equalexperts.client.softwaredesignsystems.eatout.services.Location
import com.equalexperts.client.softwaredesignsystems.eatout.services.Restaurant
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.osmdroid.config.Configuration

class EatOutToHelpOutJourneyTest {

    @get:Rule
    val mockServiceLayer = MockServiceLayerRule()

    private val testRestaurant = Restaurant("Stub Restaurant for testing purposes", "WC2N 4HZ")

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun launchApp() {
        scenario = launch(MainActivity::class.java)
    }

    @Test
    fun userAgentWillBeProvidedToOmsdroidLib() {
        assertEquals(
            Configuration.getInstance().userAgentValue,
            InstrumentationRegistry.getInstrumentation().targetContext.packageName
        )
    }

    @Test
    fun mapDefaultsToLastLocationViewed() {
        val expectedLocation = Location(51.51, -0.11)

        mockServiceLayer.mockLastViewedLocationService.simulateLastViewedLocation(expectedLocation)

        onView(withId(R.id.mainMap)).check(matches(mapCentreCloseToLocation(expectedLocation)))
    }

    @Test
    fun mapWillPersistLastViewedLocationWhenUserLeaves() {
        val expectedLocation = Location(52.45, -0.01)

        onView(withId(R.id.mainMap)).perform(dragMapTo(expectedLocation.latitude, expectedLocation.longitude))

        scenario.moveToState(Lifecycle.State.DESTROYED)

        assertEquals(expectedLocation.latitude, mockServiceLayer.mockLastViewedLocationService.storedLocation!!.latitude, 0.01)
        assertEquals(expectedLocation.longitude, mockServiceLayer.mockLastViewedLocationService.storedLocation!!.longitude, 0.01)
    }

    @Test
    fun willDisplayRestaurants() {
        onView(withId(R.id.mainMap)).check(matches(hasOverlayWithText(testRestaurant.name)))
    }

    @Ignore
    @Test
    fun tappingARestaurantRevealsInformation() {
        onView(withId(R.id.mainMap)).perform(clickOnMarkerWithText(testRestaurant.name))

        onView(withText(testRestaurant.postcode)).check(matches(isDisplayed()))
    }

    @Ignore
    @Test
    fun tappingOnRestaurantInformationWillNavigateToSearchEngine() {
        onView(withId(R.id.mainMap)).perform(clickOnMarkerWithText(testRestaurant.name))

        onView(withId(R.id.viewRestaurant)).perform(click())

        assertThat(mockServiceLayer.mockRestaurantInfoProvider.infoProvidedForRestaurant, `is`(testRestaurant))

        onView(withId(R.id.viewRestaurant)).check(doesNotExist())
    }

    @Test
    fun mapIsLockedToTheUK() {
        onView(withId(R.id.mainMap)).perform(dragMapTo(48.0, 2.0))

        onView(withId(R.id.mainMap)).check(matches(outsideOfBounds(48.0, 2.0)))

        onView(withId(R.id.mainMap)).perform(dragMapTo(61.0, -9.0))

        onView(withId(R.id.mainMap)).check(matches(outsideOfBounds(61.0, -9.0)))
    }

    @Test
    fun canSearchForATownOrCityOrPostcode() {
        mockServiceLayer.mockLocationSearchService.simulateAvailableLocation("Testtown" to Location(53.4161, -2.6491))

        onView(withId(R.id.search)).perform(typeText("Testtown"), pressImeActionButton())

        onView(withId(R.id.mainMap)).check(matches(insideOfBounds(53.4161, -2.6491)))
    }

    @Test
    fun userIsNotifiedOfEmptySearchResults() {
        mockServiceLayer.mockLocationSearchService.simulateUnavailableLocation("Not found Testtown")
        onView(withId(R.id.search)).perform(typeText("Not found Testtown"), pressImeActionButton())

        onView(withId(R.id.search)).check(matches(hasErrorText(R.string.location_not_found)))
    }

    @Test
    fun userIsNotifiedOfNetworkErrors() {
        mockServiceLayer.mockLocationSearchService.simulateNetworkErrorForLocation("Network Error Town")
        onView(withId(R.id.search)).perform(typeText("Network Error Town"), pressImeActionButton())

        onView(withId(R.id.search)).check(matches(hasErrorText(R.string.location_network_error)))
    }

    @Test
    fun userIsNotifiedOfServerErrors() {
        mockServiceLayer.mockLocationSearchService.simulateServerErrorForLocation("Server Error Town")
        onView(withId(R.id.search)).perform(typeText("Server Error Town"), pressImeActionButton())

        onView(withId(R.id.search)).check(matches(hasErrorText(R.string.location_server_error)))
    }

    @Test
    fun willFetchRestaurantForLocationOnLoad() {


        onView(withId(R.id.mainMap)).check(matches(isDisplayed()))

    }
}