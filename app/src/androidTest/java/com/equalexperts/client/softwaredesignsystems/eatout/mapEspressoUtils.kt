package com.equalexperts.client.softwaredesignsystems.eatout

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import kotlin.concurrent.thread


fun clickCentreOfScreen(): ViewAction {
    return GeneralClickAction(
        Tap.SINGLE,
        CoordinatesProvider { view ->
            val screenPos = IntArray(2)
            view.getLocationOnScreen(screenPos)

            val screenX = screenPos[0] + view.width / 2.0f
            val screenY = screenPos[1] + view.height / 2.0f - 200.0f
            floatArrayOf(screenX, screenY)
        },
        Press.FINGER, 0, 0
    )
}

internal fun clickOnMarkerWithText(restaurantName: String): ViewAction {
    return object : ViewAction {
        override fun getDescription() = "Click on the marker matching text '$restaurantName'"

        override fun getConstraints() = ViewMatchers.isAssignableFrom(View::class.java)

        override fun perform(uiController: UiController?, view: View?) {
            thread {
                val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())
                val marker: UiObject =
                    uiDevice.findObject(UiSelector().descriptionContains(restaurantName))
                marker.click()
            }
        }
    }
}

internal fun clickOnInfoWindow(): ViewAction {
    return clickCentreOfScreen()
}

internal fun hasErrorText(stringResource: Int) =
    ViewMatchers.hasErrorText(
        getInstrumentation().targetContext.getString(
            stringResource
        )
    )