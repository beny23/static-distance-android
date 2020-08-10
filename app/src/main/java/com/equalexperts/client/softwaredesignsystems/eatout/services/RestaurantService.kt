package com.equalexperts.client.softwaredesignsystems.eatout.services

sealed class RestaurantServiceResult {
    data class Success(val restaurants: List<Restaurant>) : RestaurantServiceResult()
    object ServerError : RestaurantServiceResult()
    object NetworkError : RestaurantServiceResult()
}

interface RestaurantService {
    fun fetchRestaurants(location: Location, results: (RestaurantServiceResult) -> Unit)

}
