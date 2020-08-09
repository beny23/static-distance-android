package com.equalexperts.client.softwaredesignsystems.eatout.services

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RemoteLocationSearchServiceIntegrationTest {
    private lateinit var searchResult: LocationSearchResult
    private val mockWebServer = MockWebServer()
    private val service = RemoteLocationSearchService(mockWebServer.url("/").toString())

    @Test
    fun noSearchResultsResultsInNotFound() {
        val query = "${System.currentTimeMillis()}"
        givenNoSearchResultsFor(query)

        whenASearchHappens(query)

        thenTheResultIsNotFound()
    }

    @Test
    fun serviceErrorResultsInServerError() {
        givenServiceError()

        whenASearchHappens("unused")

        thenTheResultIsServerError()
    }

    @Test
    fun networkIssuesResultsInNetworkError() {
        givenConnectionIssues()

        whenASearchHappens("unused")

        thenTheResultIsNetworkError()
    }

    @Test
    fun resultsCanBeRetrieved() {
        val expectedLocation = Location(1.234, 5.678)
        givenSearchResultsFor("AQUERY", expectedLocation)

        whenASearchHappens("a query")

        thenTheResultIsSuccessWithLocation(expectedLocation)
    }

    @Test
    fun searchRequestMustBeAtLeast4CharactersOtherwiseLocationNotFound() {
        givenSearchResultsFor("AQUERY", Location(1.234, 5.678))

        whenASearchHappens("a qu")

        thenTheResultIsNotFound()

        andThereHasBeenNoNetworkRequest()
    }

    @Test
    fun queryIsSanitisedBeforeRequest() {
        givenSearchResultsFor("AQUERY", Location(1.234, 5.678))

        whenASearchHappens("a !£%query")

        thenRequestMadeTo("/outcode/A/Q/AQUE.csv")
    }

    private fun givenServiceError() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
    }

    private fun andThereHasBeenNoNetworkRequest() {
        assertNull(mockWebServer.takeRequest(500, TimeUnit.MILLISECONDS))
    }

    private fun givenSearchResultsFor(query: String, expectedLocation: Location) {
        mockWebServer.enqueue(
            MockResponse().setBody(
                """
                id,postcode,lat,lon
                ID,${query},${expectedLocation.latitude},${expectedLocation.longitude}
                ID,NOTAQUERY,2.234,6.678
                ID,STILLNOTAQUERY,3.234,7.678
                
            """.trimIndent().trimMargin()
            )
        )
    }

    private fun givenConnectionIssues() {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
    }

    private fun givenNoSearchResultsFor(query: String) {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
    }

    private fun whenASearchHappens(query: String) {
        val latch = CountDownLatch(1)
        service.search(query) {
            searchResult = it
            latch.countDown()
        }
        latch.await(500, TimeUnit.MILLISECONDS)
    }

    private fun thenTheResultIsNotFound() {
        assertEquals(LocationSearchResult.NotFound, searchResult)
    }

    private fun thenTheResultIsNetworkError() {
        assertEquals(LocationSearchResult.NetworkError, searchResult)
    }

    private fun thenTheResultIsSuccessWithLocation(expectedLocation: Location) {
        assertEquals(LocationSearchResult.Success(expectedLocation), searchResult)
    }

    private fun thenRequestMadeTo(expectedPath: String) {
        val request = mockWebServer.takeRequest(500, TimeUnit.MILLISECONDS)!!
        assertEquals(request.path, expectedPath)
    }

    private fun thenTheResultIsServerError() {
        assertEquals(LocationSearchResult.ServerError, searchResult)
    }
}
