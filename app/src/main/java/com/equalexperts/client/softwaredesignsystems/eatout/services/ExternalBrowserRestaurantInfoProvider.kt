package com.equalexperts.client.softwaredesignsystems.eatout.services

import android.content.Context
import android.content.Intent
import android.net.Uri

class ExternalBrowserRestaurantInfoProvider(private val applicationContext: Context) :
    RestaurantInfoProvider {
    override fun provideRestaurantInfo(restaurant: Restaurant) {
        applicationContext.startActivity(
            Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setData(
                Uri.parse(
                    "https://duckduckgo.com/?q=%5C" + Uri.encode(
                        "${restaurant.name} ${restaurant.postcode}"
                    )
                )
            ))
    }
}