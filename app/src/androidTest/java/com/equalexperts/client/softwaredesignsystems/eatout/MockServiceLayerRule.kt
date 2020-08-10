package com.equalexperts.client.softwaredesignsystems.eatout

import androidx.test.core.app.ApplicationProvider
import com.equalexperts.client.softwaredesignsystems.eatout.services.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockServiceLayer(
    override val restaurantInfoProvider: RestaurantInfoProvider,
    override val locationSearchService: LocationSearchService,
    override val lastViewedLocationService: LastViewedLocationService,
    override val restaurantService: RestaurantService
) : ServiceLayer

class MockRestaurantInfoProvider : RestaurantInfoProvider {
    lateinit var infoProvidedForRestaurant: Restaurant

    override fun provideRestaurantInfo(restaurant: Restaurant) {
        infoProvidedForRestaurant = restaurant
    }
}

class MockLocationSearchService : LocationSearchService {
    private val stubLocations = mutableMapOf<String, LocationSearchResult>()

    fun simulateAvailableLocation(searchLocation: Pair<String, Location>) {
        stubLocations += searchLocation.first to LocationSearchResult.Success(searchLocation.second)
    }

    override fun search(query: String, response: (LocationSearchResult) -> Unit) {
        response(
            stubLocations[query]
                ?: throw IllegalStateException("Result for $query has not been stubbed")
        )
    }

    fun simulateUnavailableLocation(searchLocation: String) {
        stubLocations += searchLocation to LocationSearchResult.NotFound
    }

    fun simulateNetworkErrorForLocation(searchLocation: String) {
        stubLocations += searchLocation to LocationSearchResult.NetworkError
    }

    fun simulateServerErrorForLocation(searchLocation: String) {
        stubLocations += searchLocation to LocationSearchResult.ServerError
    }
}

class MockLastViewedLocationService : LastViewedLocationService {
    var storedLocation: Location? = null
    private var lastLocation = Location(53.6601, -2.4946)

    fun simulateLastViewedLocation(lastViewedLocation: Location) {
        lastLocation = lastViewedLocation
    }

    override var lastViewedLocation: Location
        get() = lastLocation
        set(value) { storedLocation = value }
}

class MockRestaurantService : RestaurantService {
    private val resultMap = mutableMapOf<Location, RestaurantServiceResult>()

    fun simulateRestaurantsAvailable(location: Location, restaurants: List<Restaurant>) {
        resultMap[location] = RestaurantServiceResult.Success(restaurants)
    }

    override fun fetchRestaurants(location: Location, results: (RestaurantServiceResult) -> Unit) {
        resultMap[location]?.let {
            results(it)
        }
    }
}

class MockServiceLayerRule : TestRule {

    val mockRestaurantInfoProvider = MockRestaurantInfoProvider()
    val mockLocationSearchService = MockLocationSearchService()
    val mockLastViewedLocationService = MockLastViewedLocationService()
    val mockRestaurantService = MockRestaurantService()

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val application =
                    ApplicationProvider.getApplicationContext<EatOutToHelpOutApplication>()

                application.serviceLayer = MockServiceLayer(
                    mockRestaurantInfoProvider,
                    mockLocationSearchService,
                    mockLastViewedLocationService,
                    mockRestaurantService
                )

                base?.evaluate()
            }
        }
    }

}