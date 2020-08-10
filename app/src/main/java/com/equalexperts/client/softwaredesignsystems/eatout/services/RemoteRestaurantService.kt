package com.equalexperts.client.softwaredesignsystems.eatout.services

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface EatOutToHelpOutRestaurantApi {
    @GET("pubgrid/{x}/{path}.csv")
    fun retrieveRestaurants(@Path("x") x: String, @Path("path") path: String): Call<String>
}

class RemoteRestaurantService(baseUrl: String) :
    RestaurantService {

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val api = retrofit.create(EatOutToHelpOutRestaurantApi::class.java)

    override fun fetchRestaurants(
        location: Location,
        results: (RestaurantServiceResult) -> Unit
    ) {
        api.retrieveRestaurants("${location.gridX}", "${location.gridX}-${location.gridY}").enqueue(object :
            Callback<String?> {
            override fun onFailure(call: Call<String?>, t: Throwable) {
                results(RestaurantServiceResult.NetworkError)
            }

            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                when {
                    response.isSuccessful -> parseRestaurantBody(response, results)
                    else -> results(RestaurantServiceResult.ServerError)
                }
            }
        })
    }

    private fun parseRestaurantBody(
        response: Response<String?>,
        results: (RestaurantServiceResult) -> Unit
    ) {
        response.body()?.let { restaurantCsv ->
            results(
                RestaurantServiceResult.Success(
                    restaurantCsv
                        .split("\n")
                        .drop(1)
                        .filter { !it.isBlank() }
                        .map {
                            val (name, postcode, lat, lon) = it.split(",")
                            Restaurant(
                                name,
                                postcode,
                                Location(
                                    lat.toDouble(),
                                    lon.toDouble()
                                )
                            )
                        })
            )
        }
    }

}