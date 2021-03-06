package com.equalexperts.client.softwaredesignsystems.eatout

import com.equalexperts.client.softwaredesignsystems.eatout.services.LastViewedLocationService
import com.equalexperts.client.softwaredesignsystems.eatout.services.LocationSearchService
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantInfoProvider
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantService

interface ServiceLayer {
    val restaurantInfoProvider: RestaurantInfoProvider
    val locationSearchService: LocationSearchService
    val lastViewedLocationService : LastViewedLocationService
    val restaurantService : RestaurantService
}