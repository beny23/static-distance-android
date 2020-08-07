package com.equalexperts.client.softwaredesignsystems.eatout

import android.view.View
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import kotlin.math.abs

class EatOutToHelpOutJourneyTest {

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
    fun willDisplayMapUponLaunch() {
        launchApp()

        onView(withId(R.id.mainMap)).check(matches(isDisplayed()))
    }

    @Test
    fun mapDefaultsToLondon() {
        launchApp()

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
        launchApp()

        onView(withId(R.id.mainMap)).check(matches(hasOverlayWithText("Stub Restaurant for testing purposes")))
    }

    private fun hasOverlayWithText(restaurantName: String): BaseMatcher<in View> {
        return object : BaseMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("map displaying overlay with restaurant $restaurantName")
            }

            override fun matches(item: Any): Boolean {
                if (item is MapView) {
                    item.overlays.forEach {
                        if (it is FolderOverlay) {
                            return it.items.any { (it as? Marker)?.title == restaurantName }
                        }
                    }
                }
                return false
            }
        }

    }

    private fun mapDisplayingBoundingBoxNear(boundingBox: BoundingBox): Matcher<in View> {
        return object : BaseMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("map displaying bounding box within 0.01 degrees of $boundingBox")
            }

            override fun matches(item: Any): Boolean {
                if (item is MapView) {
                    return abs(item.boundingBox.latNorth) - abs(boundingBox.latNorth) < 0.01 &&
                            abs(item.boundingBox.latSouth) - abs(boundingBox.latSouth) < 0.01 &&
                            abs(item.boundingBox.lonEast) - abs(boundingBox.lonEast) < 0.01 &&
                            abs(item.boundingBox.lonWest) - abs(boundingBox.lonWest) < 0.01
                }
                return false
            }
        }
    }
}