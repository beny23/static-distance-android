package com.equalexperts.client.softwaredesignsystems.eatout.services

data class Restaurant(val name: String, val postcode: String)

interface RestaurantInfoProvider {
    fun provideRestaurantInfo(restaurant: Restaurant)
}