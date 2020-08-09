package com.equalexperts.client.softwaredesignsystems.eatout.services

import android.content.SharedPreferences

class SharedPrefsLastViewedLocationService(private val prefs: SharedPreferences) :
    LastViewedLocationService {
    override var lastViewedLocation: Location
        get() = Location(
            prefs.getString("last_lat", "51.5050")!!.toDouble(),
            prefs.getString("last_lon", "-0.0900")!!.toDouble()
        )
        set(value) {
            prefs.edit()
                .putString("last_lat", "${value.latitude}")
                .putString("last_lon", "${value.longitude}")
                .apply()
        }
}