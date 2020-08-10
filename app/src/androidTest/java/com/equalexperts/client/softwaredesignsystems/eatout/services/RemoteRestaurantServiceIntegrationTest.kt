package com.equalexperts.client.softwaredesignsystems.eatout.services

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RemoteRestaurantServiceIntegrationTest {
    private lateinit var restaurantResult: RestaurantServiceResult
    private val mockWebServer = MockWebServer()
    private val service =
        RemoteRestaurantService(
            mockWebServer.url("/").toString()
        )

    @Test
    fun willAdaptLocationsIntoNormalisedGrid() {
        val targetLocation = Location(53.10, -0.11)

        givenASearchWillReturnResults()
        whenASearchIsMadeAt(targetLocation)
        thenTheRequestPathIs("/pubgrid/${targetLocation.gridX}/${targetLocation.gridX}-${targetLocation.gridY}.csv")
    }

    @Test
    fun willAdaptRestaurants() {
        val targetLocation = Location(53.10, -0.11)

        givenASearchWillReturnResults()
        whenASearchIsMadeAt(targetLocation)
        thenTheResultsWillContain(listOf(
            Restaurant("A RESTAURANT 1", "PO5 1DE", Location(53.80, -2.35)),
            Restaurant("A RESTAURANT 2", "PO5 2DE", Location(52.40, -1.75)),
            Restaurant("A RESTAURANT 3", "PO5 3DE", Location(51.20, -0.15))
        ))
    }

    @Test
    fun willAdaptNon200ResponseAsAServerError() {
        val targetLocation = Location(53.10, -0.11)

        givenASearchWillReturnANon200Response()
        whenASearchIsMadeAt(targetLocation)
        thenTheResultsWillBeAServerError()
    }

    @Test
    fun willAdaptANetworkIssueAsANetworkError() {
        val targetLocation = Location(53.10, -0.11)

        givenASearchWillFailDueToNetworkConditions()
        whenASearchIsMadeAt(targetLocation)
        thenTheResultsWillBeANetworkError()
    }

    private fun givenASearchWillReturnResults() {
        mockWebServer.enqueue(MockResponse().setBody(
        """
            name,postcode,lat,lon
            A RESTAURANT 1,PO5 1DE,53.80,-2.35
            A RESTAURANT 2,PO5 2DE,52.40,-1.75
            A RESTAURANT 3,PO5 3DE,51.20,-0.15
        """.trimIndent()))
    }

    private fun givenASearchWillReturnANon200Response() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
    }

    private fun givenASearchWillFailDueToNetworkConditions() {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
    }

    private fun whenASearchIsMadeAt(targetLocation: Location) {
        val latch = CountDownLatch(1)
        service.fetchRestaurants(targetLocation) {
            restaurantResult = it
            latch.countDown()
        }
        latch.await(500, TimeUnit.MILLISECONDS)
    }

    private fun thenTheRequestPathIs(expectedPath: String) {
        val request = mockWebServer.takeRequest(500, TimeUnit.MILLISECONDS)?:throw IllegalStateException("No request was made within 500ms")
        assertEquals(request.path, expectedPath)
    }

    private fun thenTheResultsWillContain(restaurants: List<Restaurant>) {
        val results = restaurantResult as RestaurantServiceResult.Success
        assertEquals(restaurants, results.restaurants)
    }

    private fun thenTheResultsWillBeAServerError() {
        assertTrue(restaurantResult is RestaurantServiceResult.ServerError)
    }

    private fun thenTheResultsWillBeANetworkError() {
        assertTrue(restaurantResult is RestaurantServiceResult.NetworkError)
    }

}