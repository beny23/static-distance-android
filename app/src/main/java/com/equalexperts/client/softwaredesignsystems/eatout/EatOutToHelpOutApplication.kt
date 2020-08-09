package com.equalexperts.client.softwaredesignsystems.eatout

import android.app.Application
import com.equalexperts.client.softwaredesignsystems.eatout.services.ExternalBrowserRestaurantInfoProvider
import com.equalexperts.client.softwaredesignsystems.eatout.services.LocationSearchService
import com.equalexperts.client.softwaredesignsystems.eatout.services.RemoteLocationSearchService
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantInfoProvider
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
        }
        Configuration.getInstance().userAgentValue = packageName
    }

}