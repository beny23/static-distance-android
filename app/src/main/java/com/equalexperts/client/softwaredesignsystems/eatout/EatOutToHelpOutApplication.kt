package com.equalexperts.client.softwaredesignsystems.eatout

import android.app.Application
import com.equalexperts.client.softwaredesignsystems.eatout.services.ExternalBrowserRestaurantInfoProvider
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantInfoProvider
import org.osmdroid.config.Configuration

class EatOutToHelpOutApplication : Application() {
    lateinit var serviceLayer: ServiceLayer

    override fun onCreate() {
        super.onCreate()
        serviceLayer = object : ServiceLayer {
            override val restaurantInfoProvider: RestaurantInfoProvider
                get() = ExternalBrowserRestaurantInfoProvider(this@EatOutToHelpOutApplication)
        }
        Configuration.getInstance().userAgentValue = packageName
    }

}