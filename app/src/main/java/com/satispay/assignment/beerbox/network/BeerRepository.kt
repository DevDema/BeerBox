package com.satispay.assignment.beerbox.network

import com.satispay.assignment.beerbox.model.Beer

class BeerRepository(
    private val service: BeerService
) {

    suspend fun getBeers(page: Int): List<Beer> {
        if(page < 1) {
            error("Invalid page $page. Should be >= 1.")
        }

        return service.getBeers(page)
    }
}