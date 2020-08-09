package com.equalexperts.client.softwaredesignsystems.eatout.services

data class Location(val latitude: Double, val longitude: Double)

sealed class LocationSearchResult {
    object NotFound : LocationSearchResult()
    object NetworkError : LocationSearchResult()
    object ServerError : LocationSearchResult()
    data class Success(val location: Location) : LocationSearchResult()
}

interface LocationSearchService {
    fun search(query: String, response: (LocationSearchResult) -> Unit)
}