package com.equalexperts.client.softwaredesignsystems.eatout

import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.equalexperts.client.softwaredesignsystems.eatout.services.Location
import com.equalexperts.client.softwaredesignsystems.eatout.services.Restaurant
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EatOutToHelpOutJourneyTest {

    @get:Rule
    val mockServiceLayer = MockServiceLayerRule()

    private val testRestaurant = Restaurant("Stub Restaurant for testing purposes", "WC2N 4HZ", Location(51.5089, -0.1257))

    private lateinit var scenario: ActivityScenario<MainActivity>

    private fun onMapView() = onView(withContentDescription("Google Map"))

    @Before
    fun launchApp() {
        scenario = launch(MainActivity::class.java)
    }

    @Test
    fun aUserCanLearnAboutTheApp() {
        onView(withId(R.id.logo)).perform(click())

        onView(withText(R.string.about_title)).check(matches(isDisplayed()))
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
    fun tappingARestaurantRevealsInformation() {
        val expectedLocation = testRestaurant.location

        mockServiceLayer.mockLastViewedLocationService.simulateLastViewedLocation(expectedLocation)

        mockServiceLayer.mockRestaurantService.simulateRestaurantsAvailable(expectedLocation, listOf(testRestaurant))

        onMapView().check(matches(isDisplayed()))

        onMapView().perform(clickOnMarkerWithText(testRestaurant.name))
    }

    @Test
    fun tappingOnRestaurantInformationWillNavigateToSearchEngine() {
        val expectedLocation = testRestaurant.location

        mockServiceLayer.mockLastViewedLocationService.simulateLastViewedLocation(expectedLocation)

        mockServiceLayer.mockRestaurantService.simulateRestaurantsAvailable(expectedLocation, listOf(testRestaurant))

        onMapView().check(matches(isDisplayed()))

        onMapView().perform(clickOnMarkerWithText(testRestaurant.name))

        SystemClock.sleep(1000)

        onMapView().perform(clickOnInfoWindow())

        SystemClock.sleep(1000)

        assertThat(mockServiceLayer.mockRestaurantInfoProvider.infoProvidedForRestaurant, `is`(testRestaurant))
    }
}