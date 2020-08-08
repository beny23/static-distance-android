package com.equalexperts.client.softwaredesignsystems.eatout

import androidx.test.core.app.ApplicationProvider
import com.equalexperts.client.softwaredesignsystems.eatout.services.Restaurant
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantInfoProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockServiceLayer(override val restaurantInfoProvider: RestaurantInfoProvider) : ServiceLayer

class MockRestaurantInfoProvider: RestaurantInfoProvider {
    lateinit var infoProvidedForRestaurant: Restaurant

    override fun provideRestaurantInfo(restaurant: Restaurant) {
        infoProvidedForRestaurant = restaurant
    }
}

class MockServiceLayerRule: TestRule {

    val mockRestaurantInfoProvider = MockRestaurantInfoProvider()

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val application = ApplicationProvider.getApplicationContext<EatOutToHelpOutApplication>()

                application.serviceLayer = MockServiceLayer(mockRestaurantInfoProvider)

                base?.evaluate()
            }
        }
    }

}