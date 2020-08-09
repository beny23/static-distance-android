package com.equalexperts.client.softwaredesignsystems.eatout

import com.equalexperts.client.softwaredesignsystems.eatout.services.LocationSearchService
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantInfoProvider

interface ServiceLayer {
    val restaurantInfoProvider: RestaurantInfoProvider
    val locationSearchService: LocationSearchService
}