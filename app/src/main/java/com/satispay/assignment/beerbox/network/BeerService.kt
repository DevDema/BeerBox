package com.satispay.assignment.beerbox.network

import retrofit2.http.GET
import com.satispay.assignment.beerbox.model.Beer
import retrofit2.http.Query

interface BeerService {

    @GET("beers")
    suspend fun getBeers(@Query("page") page: Int): List<Beer>
}