package com.equalexperts.client.softwaredesignsystems.eatout.services

import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RemoteLocationSearchServiceIntegrationTest {
    private lateinit var searchResult: LocationSearchResult

    @Test
    fun willAlwaysResultInLocationNotFound() {
        searchFor("${System.currentTimeMillis()}")
    }

    private fun searchFor(query: String) {
        val latch = CountDownLatch(1)
        RemoteLocationSearchService().search(query) {
            searchResult = it
            latch.countDown()
        }
        latch.await(500, TimeUnit.MILLISECONDS)
    }
}