package com.equalexperts.client.softwaredesignsystems.eatout

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Test

class EatOutToHelpOutJourneyTest {

    @Test
    fun willDisplayMapUponLaunch() {
        launch(MainActivity::class.java)

        onView(withId(R.id.mainMap)).check(matches(isDisplayed()))
    }
}