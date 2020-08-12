package com.equalexperts.client.softwaredesignsystems.eatout.services

import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.GET
import retrofit2.http.Path
import java.lang.reflect.Type
import java.nio.charset.Charset

class RestaurantConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return Converter<ResponseBody, List<Restaurant>> { value ->
            val restaurantCsv = value.bytes().toString(Charset.defaultCharset())
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
                }
        }
    }
}

interface EatOutToHelpOutRestaurantApi {
    @GET("pubgrid/{x}/{path}.csv")
    fun retrieveRestaurants(
        @Path("x") x: String,
        @Path("path") path: String
    ): Call<List<Restaurant>>
}

class RemoteRestaurantService(baseUrl: String) :
    RestaurantService {

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(RestaurantConverterFactory())
        .build()

    private val api = retrofit.create(EatOutToHelpOutRestaurantApi::class.java)

    override fun fetchRestaurants(
        location: Location,
        results: (RestaurantServiceResult) -> Unit
    ) {
        api.retrieveRestaurants("${location.gridX}", "${location.gridX}-${location.gridY}")
            .enqueue(object :
                Callback<List<Restaurant>?> {
                override fun onFailure(call: Call<List<Restaurant>?>, t: Throwable) {
                    results(RestaurantServiceResult.NetworkError)
                }

                override fun onResponse(
                    call: Call<List<Restaurant>?>,
                    response: Response<List<Restaurant>?>
                ) {
                    when {
                        response.isSuccessful -> parseRestaurantBody(response, results)
                        else -> results(RestaurantServiceResult.ServerError)
                    }
                }
            })
    }

    private fun parseRestaurantBody(
        response: Response<List<Restaurant>?>,
        results: (RestaurantServiceResult) -> Unit
    ) {
        response.body()?.let { restaurants ->
            results(RestaurantServiceResult.Success(restaurants))
        }
    }
}
