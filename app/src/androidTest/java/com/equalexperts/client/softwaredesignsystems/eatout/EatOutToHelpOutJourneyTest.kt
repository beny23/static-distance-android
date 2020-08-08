package com.equalexperts.client.softwaredesignsystems.eatout

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox

class EatOutToHelpOutJourneyTest {

    private val testRestaurantName = "Stub Restaurant for testing purposes"
    private val testRestaurantPostcode = "WC2N 4HZ"

    @Before
    fun launchApp() {
        launch(MainActivity::class.java)
    }

    @Test
    fun userAgentWillBeProvidedToOmsdroidLib() {
        assertEquals(
            Configuration.getInstance().userAgentValue,
            InstrumentationRegistry.getInstrumentation().targetContext.packageName
        )
    }

    @Test
    fun mapDefaultsToLondon() {

        onView(withId(R.id.mainMap)).check(
            matches(
                mapDisplayingBoundingBoxNear(
                    BoundingBox(
                        51.58,
                        -0.03,
                        51.42,
                        -0.20
                    )
                )
            )
        )
    }

    @Test
    fun willDisplayRestaurants() {
        onView(withId(R.id.mainMap)).check(matches(hasOverlayWithText(testRestaurantName)))
    }

    @Test
    fun tappingARestaurantRevealsInformation() {
        onView(withId(R.id.mainMap)).perform(clickOnMarkerWithText(testRestaurantName))

        onView(withText(testRestaurantPostcode)).check(matches(isDisplayed()))
    }
}