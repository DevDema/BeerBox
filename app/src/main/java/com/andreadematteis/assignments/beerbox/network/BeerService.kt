package com.andreadematteis.assignments.beerbox.network

import retrofit2.http.GET
import com.andreadematteis.assignments.beerbox.model.Beer
import retrofit2.http.Query

interface BeerService {

    @GET("beers")
    suspend fun getBeers(@Query("page") page: Int): List<Beer>
}