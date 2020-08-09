package com.equalexperts.client.softwaredesignsystems.eatout

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import kotlin.math.abs


internal fun clickOnMarkerWithText(restaurantName: String): ViewAction {
    return object : ViewAction {
        override fun getDescription() = "Click on the marker matching text '$restaurantName'"

        override fun getConstraints() = ViewMatchers.isAssignableFrom(MapView::class.java)

        override fun perform(uiController: UiController?, view: View?) {
            val mapView = view as MapView
            val folderOverlay = mapView.overlays.find { it is FolderOverlay } as FolderOverlay

            val marker = folderOverlay.items.find { (it as Marker).title == restaurantName } as Marker?

            marker?.let { it.showInfoWindow() }?:throw NoMatchingViewException.Builder()
                .build()
        }
    }
}

internal fun dragMapTo(latitude: Double, longitude: Double): ViewAction {
    return object : ViewAction {
        override fun getDescription() = "Drag the map to latitude and longitude: $latitude, $longitude"

        override fun getConstraints() = ViewMatchers.isAssignableFrom(MapView::class.java)

        override fun perform(uiController: UiController?, view: View?) {
            val mapView = view as MapView
            mapView.controller.setCenter(GeoPoint(latitude, longitude))
        }
    }
}

internal fun hasOverlayWithText(restaurantName: String): BaseMatcher<in View> {
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

internal fun outsideOfBounds(latitude: Double, longitude: Double): Matcher<in View> {
    return object : BaseMatcher<View?>() {
        override fun describeTo(description: Description) {
            description.appendText("map is not displaying $latitude $longitude")
        }

        override fun matches(item: Any): Boolean {
            if (item is MapView) {
                return !item.boundingBox.contains(latitude, longitude)
            }
            return false
        }
    }
}

internal fun insideOfBounds(latitude: Double, longitude: Double): Matcher<in View> {
    return object : BaseMatcher<View?>() {
        override fun describeTo(description: Description) {
            description.appendText("map is displaying $latitude $longitude")
        }

        override fun matches(item: Any): Boolean {
            if (item is MapView) {
                return item.boundingBox.contains(latitude, longitude)
            }
            return false
        }
    }
}

internal fun mapDisplayingBoundingBoxNear(boundingBox: BoundingBox): Matcher<in View> {
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
