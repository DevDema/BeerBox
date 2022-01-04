package com.satispay.assignment.beerbox.network

import com.satispay.assignment.beerbox.model.Beer

class BeerRepository(
    private val service: BeerService
) {

    suspend fun getBeers(): List<Beer> = service.getBeers()
}