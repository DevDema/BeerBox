package com.satispay.assignment.beerbox.network

import retrofit2.http.GET
import com.satispay.assignment.beerbox.model.Beer

interface BeerService {

    @GET("beers")
    suspend fun getBeers(): List<Beer>
}