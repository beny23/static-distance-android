package com.equalexperts.client.softwaredesignsystems.eatout

import android.app.Application
import android.content.Context
import com.equalexperts.client.softwaredesignsystems.eatout.services.*
import org.osmdroid.config.Configuration

class EatOutToHelpOutApplication : Application() {
    lateinit var serviceLayer: ServiceLayer

    override fun onCreate() {
        super.onCreate()
        serviceLayer = object : ServiceLayer {
            override val restaurantInfoProvider: RestaurantInfoProvider
                get() = ExternalBrowserRestaurantInfoProvider(this@EatOutToHelpOutApplication)
            override val locationSearchService: LocationSearchService
                get() = RemoteLocationSearchService("https://www.eat-out-to-help-out.co/")
            override val lastViewedLocationService: LastViewedLocationService
                get() = SharedPrefsLastViewedLocationService(getSharedPreferences("last_location", Context.MODE_PRIVATE))
            override val restaurantService: RestaurantService
                get() = object : RestaurantService {
                    override fun fetchRestaurants(
                        location: Location,
                        results: (RestaurantServiceResult) -> Unit
                    ) {
                        results(RestaurantServiceResult.Success(listOf(Restaurant("WOO", "WOO 123", location))))
                    }
                }
        }
        Configuration.getInstance().userAgentValue = packageName
    }

}