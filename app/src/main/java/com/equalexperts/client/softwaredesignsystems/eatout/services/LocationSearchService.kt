package com.equalexperts.client.softwaredesignsystems.eatout.services

import kotlin.math.abs

data class Location(val latitude: Double, val longitude: Double) {
    override fun equals(other: Any?): Boolean {
        return other != null && other is Location && abs(other.latitude) - abs(latitude) <= 0.01 && abs(
            other.longitude
        ) - abs(longitude) <= 0.01
    }

    override fun hashCode(): Int {
        return (latitude * 1_000.0).toInt() + (longitude * 1_000_000.0).toInt()
    }
}

sealed class LocationSearchResult {
    object NotFound : LocationSearchResult()
    object NetworkError : LocationSearchResult()
    object ServerError : LocationSearchResult()
    data class Success(val location: Location) : LocationSearchResult()
}

interface LocationSearchService {
    fun search(query: String, response: (LocationSearchResult) -> Unit)
}