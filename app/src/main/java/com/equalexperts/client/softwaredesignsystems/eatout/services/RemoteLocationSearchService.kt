package com.equalexperts.client.softwaredesignsystems.eatout.services

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

data class ApiResponse(val name: String, val latitude: Double, val longitude: Double)

interface EatOutToHelpOutApi {
    @GET("outcode/{firstChar}/{secondChar}/{term}.csv")
    fun search(
        @Path("firstChar") firstChar: String,
        @Path("secondChar") secondChar: String,
        @Path("term") term: String
    ): Call<String>
}

class RemoteLocationSearchService(baseUrl: String) : LocationSearchService {

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private val api = retrofit.create(EatOutToHelpOutApi::class.java)

    override fun search(query: String, response: (LocationSearchResult) -> Unit) {
        val sanitisedQuery = query.filter(Char::isLetterOrDigit).toUpperCase(Locale.ROOT)
        if (sanitisedQuery.length < 3) {
            response(LocationSearchResult.NotFound)
        } else {
            performApiCall(response, sanitisedQuery)
        }
    }

    private fun performApiCall(
        response: (LocationSearchResult) -> Unit,
        query: String
    ) {
        api.search("${query[0]}", "${query[1]}", if (query.length > 4) query.substring(0, 4) else query).enqueue(object : Callback<String?> {
            override fun onFailure(call: Call<String?>, t: Throwable) {
                response(LocationSearchResult.NetworkError)
            }

            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                when (response.code()) {
                    200 -> {
                        val apiResponse = response.body()?.let { body ->
                            body
                                .split("\n")
                                .filter { !it.isBlank() && it != "id,postcode,lat,lon" }
                                .map {
                                    val (_, name, lat, lon) = it.split(",")
                                    ApiResponse(name, lat.toDouble(), lon.toDouble())
                                }
                                .firstOrNull {
                                    val lastDigit = query.indexOfLast(Char::isDigit)
                                    if (lastDigit != -1 && lastDigit != query.length-1) {
                                        val postcode = query.substring(0, lastDigit) + " " + query.substring(lastDigit)
                                        it.name == postcode
                                    }
                                    else {
                                        it.name == query
                                    }
                                }
                        }
                        apiResponse?.let {
                            response(
                                LocationSearchResult.Success(
                                    Location(
                                        it.latitude,
                                        it.longitude
                                    )
                                )
                            )
                        } ?: response(LocationSearchResult.NotFound)
                    }
                    404 -> response(LocationSearchResult.NotFound)
                    else -> response(LocationSearchResult.ServerError)
                }
            }
        })
    }
}
