package com.equalexperts.client.softwaredesignsystems.eatout.services

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedPrefsLastViewedLocationServiceIntegrationTest {

    @Test
    fun willDefaultToLondonWhenEmpty() {
        val prefs = InstrumentationRegistry.getInstrumentation().context.getSharedPreferences(
            "test_prefs",
            Context.MODE_PRIVATE
        )

        prefs.edit().clear().apply()

        assertEquals(
            Location(51.5050, -0.09),
            SharedPrefsLastViewedLocationService(
                prefs
            ).lastViewedLocation
        )
    }

    @Test
    fun willReturnLastViewedLocationWhenPreviouslyStored() {
        val prefs = InstrumentationRegistry.getInstrumentation().context.getSharedPreferences(
            "test_prefs",
            Context.MODE_PRIVATE
        )

        prefs.edit().putString("last_lat", "1.2345").putString("last_lon", "2.3456").apply()

        assertEquals(
            Location(1.2345, 2.3456),
            SharedPrefsLastViewedLocationService(
                prefs
            ).lastViewedLocation
        )
    }

    @Test
    fun lastKnownLocationCanBeUpdated() {
        val prefs = InstrumentationRegistry.getInstrumentation().context.getSharedPreferences(
            "test_prefs",
            Context.MODE_PRIVATE
        )

        val service =
            SharedPrefsLastViewedLocationService(
                prefs
            )

        val expectedLocation = Location(0.1234, 1.2345)
        service.lastViewedLocation = expectedLocation

        assertEquals(
            expectedLocation,
            service.lastViewedLocation
        )
    }

}