package com.equalexperts.client.softwaredesignsystems.eatout

import androidx.test.core.app.ApplicationProvider
import com.equalexperts.client.softwaredesignsystems.eatout.services.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockServiceLayer(override val restaurantInfoProvider: RestaurantInfoProvider, override val locationSearchService: LocationSearchService) : ServiceLayer

class MockRestaurantInfoProvider: RestaurantInfoProvider {
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
        response(stubLocations[query]?:throw IllegalStateException("Result for $query has not been stubbed"))
    }

    fun simulateUnavailableLocation(searchLocation: String) {
        stubLocations += searchLocation to LocationSearchResult.NotFound
    }
}

class MockServiceLayerRule: TestRule {

    val mockRestaurantInfoProvider = MockRestaurantInfoProvider()
    val mockLocationSearchService = MockLocationSearchService()

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val application = ApplicationProvider.getApplicationContext<EatOutToHelpOutApplication>()

                application.serviceLayer = MockServiceLayer(mockRestaurantInfoProvider, mockLocationSearchService)

                base?.evaluate()
            }
        }
    }

}