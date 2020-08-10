package com.equalexperts.client.softwaredesignsystems.eatout.services

data class Restaurant(val name: String, val postcode: String, val location: Location)

interface RestaurantInfoProvider {
    fun provideRestaurantInfo(restaurant: Restaurant)
}